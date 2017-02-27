package org.javaee.soap2rest.utils.liquibase;

import org.javaee.soap2rest.utils.liquibase.annotations.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

/**
 * Created by nikilipa on 2/27/17.
 */
@ApplicationScoped
public class SimpleSchemesBuilder {

    private final static Logger log = LoggerFactory.getLogger(SimpleSchemesBuilder.class);

    private static class SchemaNode implements Comparable<Schema> {
        private final Schema item;

        public SchemaNode(Schema item) {
            this.item = item;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SchemaNode that = (SchemaNode) o;

            return item.name().equals(that.item.name());
        }

        @Override
        public int hashCode() {
            return item.name().hashCode();
        }

        @Override
        public int compareTo(Schema that) {
            return this.item.name().compareTo(that.name());
        }

    }

    // it could be modified to support depends 'param'
    public List<Schema> build(Collection<Schema> schemes) {
        log.trace("build({})", schemes);

        log.info("Sorting schemes according dependencies");

        Iterator<Schema> schemaIterator = schemes.iterator();
        Map<String, Schema> schemaMap = new TreeMap();
        while (schemaIterator.hasNext()) {
            Schema next = schemaIterator.next();
            schemaMap.put(next.name(), next);
        }

        return new ArrayList<>(schemaMap.values());

    }
}
