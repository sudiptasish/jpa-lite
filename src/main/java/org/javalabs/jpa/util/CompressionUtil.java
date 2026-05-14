package org.javalabs.jpa.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

/**
 * Utility class for handling data compression and decompression operations.
 * 
 * <p>
 * Provides methods to compress and decompress data using standard algorithms
 * such as GZIP, ZIP, or other supported formats. This class is typically used
 * to optimize storage or transmission of data.
 * <p>
 * All methods are expected to be stateless and thread-safe.
 *
 * @author Sudiptasish Chanda
 */
public class CompressionUtil {
    
    public static void main(String[] args) {
        String str = "{\"mt_norm\": {\"transaction\": {\"processing_code\": \"014000\"}}}";
        System.out.println("Compressed string: " + compress(str));
        System.out.println("Hashed string: " + calculateHash(str));
    }
    
    public static String compress(String str) {
        try {
            if (str == null || str.length() == 0) {
                throw new Exception("Input string is null or empty");
            }
            ByteArrayOutputStream obj = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(obj);
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            
            for (byte zb : obj.toByteArray()) {
                System.out.print(String.format("%02x ", zb));
            }
            return Base64.getEncoder().encodeToString(obj.toByteArray());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String calculateHash(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash); // Encode as Base64 for readability
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
