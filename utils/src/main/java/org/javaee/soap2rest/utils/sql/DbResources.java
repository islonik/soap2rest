package org.javaee.soap2rest.utils.sql;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

/**
 * Created by nikilipa on 2/27/17.
 */
@ApplicationScoped
public class DbResources {

    @Resource(lookup = "java:/S2ROracle11Db")
    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

}
