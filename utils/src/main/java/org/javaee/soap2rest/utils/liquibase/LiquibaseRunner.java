package org.javaee.soap2rest.utils.liquibase;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import liquibase.integration.cdi.CDILiquibaseConfig;
import liquibase.integration.cdi.annotations.LiquibaseType;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.javaee.soap2rest.utils.liquibase.annotations.Liquibase;
import org.javaee.soap2rest.utils.liquibase.annotations.Schema;
import org.javaee.soap2rest.utils.sql.DbResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static java.util.stream.Collectors.toList;

/**
 * Created by nikilipa on 2/27/17.
 */
@ApplicationScoped
public class LiquibaseRunner {

    private final static Logger log = LoggerFactory.getLogger(LiquibaseRunner.class);

    private final static String INCLUDE_TPL = "\t<include file=\"%s\"/>%n";
    private final static String ROOT_PATH = System.getProperty("java.io.tmpdir");
    private final static String TEMPLATE_NAME = "liqibase_schema.xml";

    private final static Object jvmLockObject = new Object();

    private final DbResources dataSource;
    private final BeanManager bm;
    private final SimpleSchemesBuilder treeBuilder;

    @Inject
    public LiquibaseRunner(DbResources resources, BeanManager bm, SimpleSchemesBuilder treeBuilder) {
        this.dataSource = resources;
        this.bm = bm;
        this.treeBuilder = treeBuilder;
    }

    @Produces
    @LiquibaseType
    public CDILiquibaseConfig createConfig() {
        log.trace("createConfig()");

        try (InputStreamReader isr = new InputStreamReader(
                getClass().getResourceAsStream("/liquibase_template.xml"),
                Charsets.UTF_8)
        ) {
            return doLocked(new Callable<CDILiquibaseConfig>() {
                @Override
                public CDILiquibaseConfig call() throws Exception {
                    File s2rDir = new File(String.format("%s/s2r/schemes", ROOT_PATH));
                    if (!s2rDir.exists()) {
                        if(s2rDir.mkdirs()) {
                            log.debug("File [path={}] was deleted", s2rDir.getAbsolutePath());
                        }
                    }
                    log.debug("Includes directory: [path={}]", s2rDir.getAbsolutePath());

                    String path = String.format("%s/%s", ROOT_PATH, TEMPLATE_NAME);
                    File output = new File(path);

                    if (output.exists()) {
                        log.debug("File [path={}] already exists, deleting", path);
                        if (output.delete()) {
                            log.debug("File [path={}] was deleted", path);
                        }
                    }
                    if (output.createNewFile()) {
                        log.debug("File [path={}] was created", path);
                    }
                    log.info(String.format("File %s was created.", output));
                    log.debug("Root liquibase file [path={}] ready.", path);

                    long start = System.currentTimeMillis();
                    log.info("Scanning application for liquibase schemes.");

                    StringBuilder schemes = treeBuilder.build(
                                bm.getBeans(Object.class, new AnnotationLiteral<Liquibase>() {
                            }).stream()
                                    .map((b) -> b.getBeanClass())
                                    .map((c) -> c.getAnnotation(Schema.class))
                                    .collect(toList())).stream()
                            .map((a) -> a.resource())
                            .flatMap((rs) -> Arrays.stream(rs))
                            .map((s) -> copyToFile(s2rDir, s))
                            .collect(() -> new StringBuilder(),
                                    (sb, s) -> sb.append(String.format(INCLUDE_TPL, s)).append("\n"),
                                    (sb1, sb2) -> sb1.append("\n").append(sb2));

                    log.info("Scan complete [took={} milliseconds]", System.currentTimeMillis() - start);
                    log.debug("Resolved schemes: \n{}\n", schemes);

                    log.debug("Generationg root liquibase file.");
                    String template = CharStreams.toString(isr);

                    String xml = String.format(template, schemes);

                    Files.write(xml, output, Charsets.UTF_8);
                    log.info(String.format("File %s was written.", output));
                    log.debug("Generation complete");
                    log.trace("Root liquibase xml: \n {} \n", xml);

                    CDILiquibaseConfig config = new CDILiquibaseConfig();
                    config.setChangeLog(TEMPLATE_NAME);

                    return config;
                }
            });
        } catch (Exception e) {
            log.error("Unable to initialize liquibase", e);
            throw new RuntimeException(e);
        }
    }

    @Produces
    @LiquibaseType
    public DataSource createDataSource() throws SQLException {
        return dataSource.getDataSource();
    }

    @Produces
    @LiquibaseType
    public ResourceAccessor create() {
        return new FileSystemResourceAccessor(ROOT_PATH);
    }

    public static CDILiquibaseConfig doLocked(Callable<CDILiquibaseConfig> action) throws Exception {
        synchronized (jvmLockObject) {
            log.info("SYNCHRONIZED ON : [{}]", jvmLockObject.hashCode());

            log.info("JVM lock acquired, acquiring file lock");
            String lockPath = String.format("%s/t2prov.liquibase.lock", ROOT_PATH);

            File lockFile = new File(lockPath);
            if (!lockFile.exists() && lockFile.createNewFile()) {
                log.info("Created lock file [path={}]", lockPath);
            }

            log.info("Try to acquire the file lock [file={}]", lockPath);
            CDILiquibaseConfig actionResult = null;
            FileLock lock = null;
            try (FileOutputStream fileStream = new FileOutputStream(lockPath);
                 FileChannel fileChannel = fileStream.getChannel()) {
                while (null == lock) {
                    try {
                        lock = fileChannel.tryLock();
                    } catch (OverlappingFileLockException e) {
                        log.debug("Lock already acquired, waiting for lock...");
                    }
                    if (null == lock) {
                        log.debug("Waiting for lock...");
                        jvmLockObject.wait(100L);
                    }
                }
                log.info("File lock acquired, running liquibase");
                actionResult = action.call();
                lock.release();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return actionResult;
        }
    }

    private String copyToFile(File s2r, String schema) {
        log.trace("copyToFile({}, {})", s2r, schema);

        try (InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream(schema), Charsets.UTF_8)){
            log.info("Transferring schema [resource={}] to directory [path={}]", schema, s2r.getAbsolutePath());
            String path = schema.startsWith("/") ? schema.substring(1) : schema;
            log.debug("Schema path is [{}]", path);

            if (path.contains("/")) {

                String dirPath = String.format("%s/%s", s2r.getAbsolutePath(),
                        path.substring(0, path.lastIndexOf("/")));
                log.debug("Schema path contains intermediate directories [path={}], preparing its.", dirPath);

                File file = new File(dirPath);
                if (!file.exists()) {
                    if (file.mkdirs()) {
                        log.debug("Log {} was created", file.getAbsolutePath());
                    }
                }
            }

            File file = new File(String.format("%s/%s", s2r.getAbsolutePath(), path));
            if (file.exists()) {
                log.info("Schema file [path={}] already exists, deleting", file.getAbsolutePath());
                if (file.delete()) {
                    log.debug("File [path={}] was removed", file.getAbsolutePath());
                }
            }
            if (file.createNewFile()) {
                log.debug("File [path={}] was created", file.getAbsolutePath());
            }
            log.debug("Schema file [path={}] is ready, copying data", file.getAbsolutePath());

            Files.write(
                    CharStreams.toString(isr),
                    file,
                    Charsets.UTF_8
            );
            String schemaPath = file.getAbsolutePath().replace(ROOT_PATH, "");
            log.debug("Data copied, schema path is [{}]", schemaPath);

            return schemaPath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
