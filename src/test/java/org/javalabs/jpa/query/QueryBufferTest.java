package org.javalabs.jpa.query;

import org.javalabs.jpa.query.QueryBuffer;
import org.junit.jupiter.api.Test;

public class QueryBufferTest {

    @Test
    public void testQueryBufferAppend() {
        QueryBuffer buff = new QueryBuffer(12);
        buff.id();
        buff.hashCode();
        buff.append(1);
        byte b = 01;
        buff.append(b);
        long l = 12;
        buff.append(l);
        float f = 14;
        buff.append(f);
        double d = 13;
        buff.append(d);
        Object obj = "true";
        buff.append(obj);
        buff.append(true);
    }
}
