package org.javalabs.jpa.util;

/**
 *
 * @author schan280
 */
public final class CharUtil {
    
    public static String toCapitalisedCamelCase(String word) {
        return toCamelCase(word, true);
    }
    
    public static String toCamelCase(String word) {
        return toCamelCase(word, false);
    }
    
    public static String toCamelCase(String word, boolean isCapital) {
        final char[] arr = new char[word.length()];
        int idx = 0;
        char ch = '\0';
        
        boolean capitalizedNext = false;
        
        for (int i = 0; i < word.length(); i ++) {
            ch = word.charAt(i);
            if (ch == '_') {
                capitalizedNext = true;
            }
            else {
                if (i == 0) {
                    if (isCapital) {
                        arr[idx] = Character.toUpperCase(ch);
                    }
                    else {
                        arr[idx] = Character.toLowerCase(ch);
                    }
                }
                else if (capitalizedNext) {
                    arr[idx] = Character.toUpperCase(ch);
                    capitalizedNext = false;
                }
                else {
                    arr[idx] = Character.toLowerCase(ch);
                }
                idx ++;
            }
        }
        return new String(arr, 0, idx);
    }
}
