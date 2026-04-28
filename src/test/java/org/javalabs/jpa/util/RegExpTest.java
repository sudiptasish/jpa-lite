package org.javalabs.jpa.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 *
 * @author schan280
 */
public class RegExpTest {
    
    @Test
    public void testGroup() {
        // String regExp = "'([^']*)'\\s*::character varying";
        extract("((role)::text = ANY ((ARRAY['CUSTOMER'::character varying, 'PROFESSIONAL'::character varying, 'ADMIN'::character varying])::text[]))");
        extract("(id = ANY (ARRAY[(1)::numeric, (2)::numeric, (300)::numeric]))");
        extract("(id = ANY (ARRAY[1, 2, 300]))");
    }
    
    private void extract(String original) {
        String regExp = "ARRAY\\[(.*?)\\]";
        
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(original);
        
        if (matcher.find()) {
            String fullMatch = matcher.group(0); // ARRAY[...]
            String innerContent = matcher.group(1); // inside the brackets

            System.out.println("Full ARRAY match: " + fullMatch);
            System.out.println("Inner content: " + innerContent);
            
            String[] arr = innerContent.split(",");
            String[] rs = new String[arr.length];
            for (int i = 0; i < arr.length; i ++) {
                rs[i] = arr[i].split("::")[0].trim();
                if (rs[i].charAt(0) == '(' && rs[i].charAt(rs[i].length() - 1) == ')') {
                    rs[i] = rs[i].substring(1, rs[i].length() - 1);
                }
            }
            System.out.println("(" + String.join(", ", rs) + ")");
            
        } else {
            System.out.println("No match found.");
        }
    }
}
