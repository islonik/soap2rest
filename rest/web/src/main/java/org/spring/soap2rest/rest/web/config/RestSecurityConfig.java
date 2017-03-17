package org.spring.soap2rest.rest.web.config;

import org.spring.soap2rest.rest.web.RestResources;
import org.spring.soap2rest.rest.web.RestRoles;
import org.spring.soap2rest.rest.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * To use PreAuthorize and JSR-250 annotations, we should set up: prePostEnabled = true, jsr250Enabled = true
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String USERS_CONFIG = "users.properties";
    private static final String ROLES_CONFIG = "roles.properties";

    private Properties readUsersFile() throws IOException {
        // it works one time during spring-boot startup
        Properties users = new Properties();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(USERS_CONFIG)) {
            users.load(inputStream);
        }
        return users;
    }

    private Properties readRolesFile() throws IOException {
        // it works one time during spring-boot startup
        Properties users = new Properties();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(ROLES_CONFIG)) {
            users.load(inputStream);
        }
        return users;
    }

    private List<User> getUsers() throws IOException {
        Properties usersFile = readUsersFile();
        Properties rolesFile = readRolesFile();
        List<User> userList = new ArrayList<>();
        for (Map.Entry<Object, Object> userEntry : usersFile.entrySet()) {
            String name = (String) userEntry.getKey();
            String pass = (String) userEntry.getValue();
            User user = new User(name, pass);
            userList.add(user);
        }
        for (Map.Entry<Object, Object> roleEntry : rolesFile.entrySet()) {
            String userName = (String) roleEntry.getKey();
            String roles = (String) roleEntry.getValue();
            for (User user : userList) {
                if (user.getName().equalsIgnoreCase(userName)) {
                    user.setUpRoles(roles);
                }
            }
        }
        return userList;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(RestResources.SYNC_SECURITY, RestResources.ASYNC_SECURITY).hasRole(RestRoles.REST_ADMIN_ROLE)
                .antMatchers(RestResources.SYNC_SECURITY, RestResources.ASYNC_SECURITY).hasRole(RestRoles.REST_CLIENT_ROLE)
                .and().httpBasic().realmName(RestRoles.S2R_REALM_NAME).authenticationEntryPoint(getBasicAuthEntryPoint())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        for (User user : getUsers()) {
            auth
                    .inMemoryAuthentication()
                    .withUser(user.getName())
                    .password(user.getPassword())
                    .roles(user.getRoles());
        }
    }

    @Bean
    public RestAuthenticationEntryPoint getBasicAuthEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

}
