package org.javalabs.jpa;

import org.javalabs.jpa.LitePersistenceProvider;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LitePersistenceProviderTest {

    @Test
    public void testDialects() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LitePersistenceProvider liteProvider = new LitePersistenceProvider();
        Method method = LitePersistenceProvider.class.getDeclaredMethod("dialects");
        method.setAccessible(true);
        String[] dialects = new String[9];
        //method.invoke(dialects,null);

    }
}
