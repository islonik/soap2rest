package org.javaee.soap2rest.utils.liquibase.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author antoermo
 * @since 31/07/2015
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Schema {
    String name();

    String[] resource();
}
