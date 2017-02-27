Soap2Rest project
=================
JavaEE: security, soap, rest.

SOAP part works here as a Strangler Application pattern. 

Apache Camel in SOAP part.

### WildFly configuration
How to setup these applications in WildFly (8.2.1.Final, 10.0.0.Final, 10.1.0.Final versions are tested):

1) create a security-domain:
```xml

    <security-domain name="Soap2RestSecurityDomain" cache-type="default">
        <authentication>
            <login-module code="UsersRoles" flag="required">
                <module-option name="usersProperties" value="${jboss.server.config.dir}/soap2rest-users.properties"/>
                <module-option name="rolesProperties" value="${jboss.server.config.dir}/soap2rest-roles.properties"/>
            </login-module>
        </authentication>
    </security-domain>
```

2) create a managed-thread-factory for both parts:
```xml
    <managed-thread-factory name="s2r" jndi-name="java:/javaee/threads/factories/s2r"/>
```

2) create a manageable thread pool for REST part:
```xml

    <managed-executor-service name="s2r-rest" jndi-name="java:/s2r/threads/executors/rest" 
            thread-factory="s2r" context-service="default" hung-task-threshold="60000" 
            core-threads="20" max-threads="50" keepalive-time="5000" queue-length="65535"/>
```

3) create a manageable thread pool for SOAP part:
```xml
    <managed-executor-service name="s2r-soap" jndi-name="java:/s2r/threads/executors/soap" 
        thread-factory="s2r" context-service="default" hung-task-threshold="60000" 
        core-threads="20" max-threads="50" keepalive-time="5000" queue-length="65535"/>
```

4) create the soap2rest-users.properties file inside of the directory ./standalone/configuration with the content:
```properties
soap2rest=soap2rest
restadmin=restadmin
```

5) create the soap2rest-roles.properties file inside of the directory ./standalone/configuration with the content:
```properties
soap2rest=WEBSERVICE.SOAP2REST.SOAP
restadmin=WEBSERVICE.SOAP2REST.REST
```
6) create the ./standalone/configuration/properties/javaee/projects directory.
7) create the file s2r.properties with the content:
```properties
rest.host=http://localhost:8078/soap2rest/rest/v1
```
8) Put jdbc6.jar into WildFly/modules/com/oracle/ojdbc/main
```xml
<module xmlns="urn:jboss:module:1.3" name="com.oracle.ojdbc">
    <resources>
        <resource-root path="ojdbc6.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
```

9) add below xml to drivers
```xml
<driver name="oracle" module="com.oracle.ojdbc">
    <xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource</xa-datasource-class>
</driver>
```

10) create a datasource:
```xml
<xa-datasource jndi-name="java:/S2ROracle11Db" pool-name="s2rpool" enabled="true" use-ccm="false">
    <xa-datasource-property name="URL">
        jdbc:oracle:thin:@localhost:49161:xe
    </xa-datasource-property>
    <xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource</xa-datasource-class>
    <driver>oracle</driver>
    <xa-pool>
        <min-pool-size>5</min-pool-size>
        <max-pool-size>32</max-pool-size>
        <is-same-rm-override>false</is-same-rm-override>
        <interleaving>false</interleaving>
        <no-tx-separate-pools>true</no-tx-separate-pools>
        <pad-xid>false</pad-xid>
        <wrap-xa-resource>false</wrap-xa-resource>
    </xa-pool>
    <security>
        <user-name>system</user-name>
        <password>oracle</password>
    </security>
    <validation>
        <check-valid-connection-sql>select 1 from dual</check-valid-connection-sql>
        <validate-on-match>false</validate-on-match>
        <background-validation>true</background-validation>
        <background-validation-millis>10000</background-validation-millis>
    </validation>
    <statement>
        <share-prepared-statements>false</share-prepared-statements>
    </statement>
</xa-datasource>
```


```


### Working hints

1) Deploy war-files.
For examples:
mvn clean install && cp rest/web/target/s2r-rest.war /home/nikilipa/Apps/WildFly/10.1.0.Final/standalone/deployments/
mvn clean install && cp soap/web/target/s2r-soap.war /home/nikilipa/Apps/WildFly/10.1.0.Final/standalone/deployments/

2) SoapUI could help you to send some SOAP-requests to SOAP endpoint.
 

### Services in SOAP part
1) Multicast
2) Sync
3) Async


 