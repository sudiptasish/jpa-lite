package org.javalabs.jpa.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MD5 hash generator class.
 *
 * @author Sudiptasish Chanda
 */
public class MD5HashGenerator {
    
    private static final byte[] key = "jpa-lite".getBytes(StandardCharsets.UTF_8);
    
    private static final boolean IDLE = false;
    private static final boolean USED = true;
    private static final AtomicBoolean LOCK = new AtomicBoolean();
    
    private static final MessageDigest MD;
    
    static {
        try {
            MD = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * It is a one hashing function.
     * The literals are first joined together with colon(:) as a separator and later
     * the MD5 algorithm is applied on the entire string to generate a unique fixed
     * length id. Because it is one way, thus you cannot get back the original 
     * string literal from it.
     * 
     * @param keys
     * @return String
     */
    public static String digest(String... keys) {
        StringBuilder buffer = new StringBuilder(keys.length * 10);
        
        for (int i = 0; i < keys.length; i ++) {
            String key = keys[i];
            buffer.append(key);
            if (i < keys.length - 1) {
                buffer.append(":");
            }
        }
        byte[] result;
        try {
            while ( ! LOCK.compareAndSet(IDLE, USED));
            MD.update(buffer.toString().getBytes());
            result = MD.digest();
        }
        finally {
            MD.reset();
            LOCK.set(IDLE);
        }
        buffer.delete(0, buffer.length());
        for (int i = 0; i < result.length; i ++) {
            buffer.append(String.format("%02x", result[i]).toUpperCase());
        }
        return buffer.toString();
    }
}
