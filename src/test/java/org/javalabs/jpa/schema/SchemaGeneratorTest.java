package org.javalabs.jpa.schema;

import org.javalabs.jpa.schema.SchemaGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SchemaGeneratorTest {

    @Test
    public void testGenerate() {
        SchemaGenerator schema = new SchemaGenerator();
        String[] args = {"--dialect","--file","--help"};
        schema.generate(args);
    }

    @Test
    public void testGenerate2() throws IOException, InterruptedException {
        SchemaGenerator schema = new SchemaGenerator();
        String[] args = {"--help","--dialect","--file"};
        schema.generate(args);
        schema.stream("persistence.xml");
        schema.main(args);
    }
}
