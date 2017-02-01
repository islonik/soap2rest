package org.javaee.soap2rest.utils.properties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by nikilipa on 8/13/16.
 */
public class PropertyFactory {

    private static final String JBOSS_PROPERTIES_HOME = System.getProperty("jboss.server.config.dir") + "/properties/";

    private static final List<String> locations = new ArrayList<String>() {{
        add("javaee/projects");
    }};

    protected String getPropertyValue(String propertyName) {
        for (String location : locations) {

            String file = null;
            String propertyKey = null;
            if (!propertyName.contains(".")) {
                throw new IllegalArgumentException(
                        String.format("Property %s doesn't contain filename! (Where filename is a prefix before first dot)", propertyName)
                );
            } else {
                file = propertyName.substring(0, propertyName.indexOf(".")) + ".properties";
                propertyKey = propertyName.substring(propertyName.indexOf(".") + 1, propertyName.length());
                location = location + File.separator + file;
            }

            try {
                File fileLocation = new File(JBOSS_PROPERTIES_HOME + location);
                if (fileLocation.exists()) {
                    Properties props = new Properties();
                    try (FileInputStream fis = new FileInputStream(fileLocation.getAbsoluteFile())) {
                        props.load(fis);
                    }
                    if (props != null) {
                        Optional<String> property = Optional.ofNullable(props.getProperty(propertyKey));
                        if (property.isPresent()) {
                            return property.get();
                        }
                    }
                }
            } catch (IOException fnfe) {
                System.err.println(fnfe.getMessage());
            }
        }
        return null;
    }

    @Produces
    @S2RProperty("")
    String findProperty(InjectionPoint ip) {
        S2RProperty annotation = ip.getAnnotated().getAnnotation(S2RProperty.class);

        String name = annotation.value();
        Optional<String> value = Optional.ofNullable(getPropertyValue(name));
        if (value.isPresent()) {
            return value.get();
        }
        throw new IllegalStateException("S2RProperty '" + name + "' is not defined!");
    }
}