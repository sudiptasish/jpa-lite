package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.descriptor.SqlQueryCache;
import org.junit.jupiter.api.Test;

public class SqlQueryCacheTest {
    @Test
    public void testSqlQueryCache() {
        SqlQueryCache sqlQuery = SqlQueryCache.get() ;
        sqlQuery.clear();

    }
    /*@Test
    public void testSqlQueryCache2() {
        QueryCache.QueryType type = null;
        SqlQueryCache.NativeSQLQuery nativeSQLQuery = new SqlQueryCache.NativeSQLQuery("native", type);
        nativeSQLQuery.type();
    }*/
}
