package org.javaee.soap2rest.utils.liquibase;

import org.javaee.soap2rest.utils.liquibase.annotations.Schema;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * Created by nikilipa on 2/27/17.
 */
public class SimpleSchemesBuilderTest {

    public Class getClass(String s) {
        try {
            return Class.forName(s);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCompareToCase01() {
        Collection schemes = Arrays.asList("C", "B", "A").stream()
                .map((s) -> String.format("org.javaee.soap2rest.utils.liquibase.%s", s))
                .map(this::getClass)
                .map((c) -> (Schema) c.getAnnotation(Schema.class))
                .collect(toList());

        List<Schema> schemaList = (new SimpleSchemesBuilder()).build(schemes);
        assertEquals("A", schemaList.get(0).name());
        assertEquals("B", schemaList.get(1).name());
        assertEquals("C", schemaList.get(2).name());
    }

}


// schemas
@Schema(name = "A", resource = "fileA.xml")
class A {
}

@Schema(name = "B", resource = "fileB.xml")
class B {
}

@Schema(name = "C", resource = "fileC.xml")
class C {
}