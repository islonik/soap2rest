Soap2Rest project
=================
JavaEE: security, soap, rest.

SOAP part works here as a Strangler Application pattern. 

How to setup this applications in WildFly (8.2.1.Final and 10.0.0.Final versions are tested):
1) create a security-domain:
    <security-domain name="Soap2RestSecurityDomain" cache-type="default">
        <authentication>
            <login-module code="UsersRoles" flag="required">
                <module-option name="usersProperties" value="${jboss.server.config.dir}/soap2rest-users.properties"/>
                <module-option name="rolesProperties" value="${jboss.server.config.dir}/soap2rest-roles.properties"/>
                <module-option name="unauthenticatedIdentity" value="rdb"/>
            </login-module>
        </authentication>
    </security-domain>
2) create soap2rest-users.properties file with content:
soap2rest=soap2rest
restadmin=restadmin

3) create soap2rest-roles.properties file with content:
soap2rest=WEBSERVICE.SOAP2REST.SOAP
restadmin=WEBSERVICE.SOAP2REST.REST

4) Deploy war-files.

5) SoapUI could help you to send some SOAP-requests to SOAP endpoint.
 