package org.javalabs.jpa.query;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that represents the buffer to temporarily hold the raw sql query string.
 *
 * @author schan280
 */
public final class QueryBuffer {
    
    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    
    private final Integer id;
    private final StringBuilder buff;
    
    QueryBuffer(int length) {
        id = COUNTER.getAndIncrement();
        buff = new StringBuilder(length);
    }
    
    Integer id() {
        return id;
    }
    
    QueryBuffer append(String s) {
        buff.append(s);
        return this;
    }
    
    QueryBuffer append(byte b) {
        buff.append(b);
        return this;
    }
    
    QueryBuffer append(int i) {
        buff.append(i);
        return this;
    }
    
    QueryBuffer append(long l) {
        buff.append(l);
        return this;
    }
    
    QueryBuffer append(float f) {
        buff.append(f);
        return this;
    }
    
    QueryBuffer append(double d) {
        buff.append(d);
        return this;
    }
    
    QueryBuffer append(boolean b) {
        buff.append(b);
        return this;
    }
    
    QueryBuffer append(Object o) {
        buff.append(o);
        return this;
    }
    
    int length() {
        return buff.length();
    }
    
    void delete(int start, int end) {
        buff.delete(start, end);
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QueryBuffer) {
            return ((QueryBuffer)obj).id.equals(id);
        }
        return false;
    }

    @Override
    public String toString() {
        return buff.toString();
    }
}
