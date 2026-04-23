package org.javalabs.jpa.util;

import org.javalabs.jpa.util.GeneralUtility;
import org.javalabs.jpa.util.QueryHints;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class GeneralUtilityTest {
    @Test
    public void testIsPrimitive(){
        GeneralUtility.isPrimitive(Integer.class);
        GeneralUtility.isPrimitive(String.class);
        GeneralUtility.isPrimitive(byte.class);
        GeneralUtility.isPrimitive(char.class);
        GeneralUtility.isPrimitive(short.class);
        GeneralUtility.isPrimitive(int.class);
        GeneralUtility.isPrimitive(long.class);
        GeneralUtility.isPrimitive(float.class);
        GeneralUtility.isPrimitive(double.class);
        GeneralUtility.isPrimitive(Byte.class);
        GeneralUtility.isPrimitive(Character.class);
        GeneralUtility.isPrimitive(Short.class);
        GeneralUtility.isPrimitive(Long.class);
        GeneralUtility.isPrimitive(Float.class);
        GeneralUtility.isPrimitive(Double.class);

        GeneralUtility.isCollection(Integer.class);

        QueryHints.RetrievalStrategy name = QueryHints.RetrievalStrategy.NAME;
        String allowNativeQuery = QueryHints.ALLOW_NATIVE_QUERY;
        String queryType = QueryHints.QUERY_TYPE;
        String retrievalStrategy = QueryHints.RETRIEVAL_STRATEGY;
        String enableBatch = QueryHints.ENABLE_BATCH;
        String fetchDef = QueryHints.FETCH_DEF;
        String fetchType = QueryHints.FETCH_TYPE;
    }
}
