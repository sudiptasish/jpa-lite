package org.javalabs.jpa.util;

import org.javalabs.jpa.util.CompressionUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

/**
 *
 * @author schan280
 */
public class CompressionUtilTest {

    @Test
    public void testCompress() {
        try {
            byte[] buff = Files.readAllBytes(Paths.get(new File(System.getProperty("user.dir") + File.separator + "src/test/resources/generate_compressed_flight_object_true.json").toURI()));
            System.out.println("Read file content. Length: " + buff.length);
            
            String hash = CompressionUtil.calculateHash("{\"mt_norm\": {\"transaction\": {\"processing_code\": \"014000\"}}}");
            System.out.println("Compressed hash: " + hash);
            
            String ret = CompressionUtil.compress("{\"mt_norm\": {\"transaction\": {\"processing_code\": \"014000\"}}}");
            System.out.println("Compressed and encoded content: " + ret);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private InputStream getInput(String filename) throws IOException {
        InputStream in = null;
        
        File file = new File(filename);
        if (file.exists()) {
            in = new FileInputStream(file);
        }
        else {
            in = getClass().getClassLoader().getResourceAsStream(filename);
            if (in != null) {

            }
            else {
                URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
                if (url != null) {
                    String urlFile = url.getFile();
                    urlFile = urlFile.replaceAll("%20", " ");
                    file = new File(urlFile);
                    if (! file.exists()) {
                        // Read using loader
                        in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
                    }
                    else {
                        in = new FileInputStream(new File(urlFile));
                    }
                }
            }
        }
        return in;
    }
}
