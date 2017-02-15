Soap2Rest project
=================
JavaEE: security, soap, rest.

SOAP part works here as a Strangler Application pattern. 

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

### Working hints

1) Deploy war-files.
For examples:
mvn clean install && cp rest/web/target/s2r-rest.war /home/nikilipa/Apps/WildFly/10.1.0.Final/standalone/deployments/
mvn clean install && cp soap/web/target/s2r-soap.war /home/nikilipa/Apps/WildFly/10.1.0.Final/standalone/deployments/

2) SoapUI could help you to send some SOAP-requests to SOAP endpoint.
 

### Services in SOAP part
Multicast
Sync
Async


 