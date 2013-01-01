package org.polycrystal.melaza;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ConfigTest {

    @Test
    public void parsesArgString() {
        Config config = new Config("key:value");
        assertEquals("value", config.getProperty("key"));
    }

    @Test
    public void parses2ArgString() {
        Config config = new Config("key1:value1;key2=value2");
        assertEquals("value1", config.getProperty("key1"));
        assertEquals("value2", config.getProperty("key2"));
    }

    @Test
    public void parsesDottedArgString() {
        Config config = new Config("logger.level:debug;properties:somefile");
        assertEquals("debug", config.getProperty("logger.level"));
        assertEquals("somefile", config.getProperty("properties"));
    }
    
    @Test
    public void loadsSystemProperties() {
        Config config;
        
        System.clearProperty("melaza.logger.level");
        config = new Config("");
        assertNull(config.getProperty("logger.level"));
        
        System.setProperty("melaza.logger.level", "trace");
        config = new Config("");
        assertEquals("trace", config.getProperty("logger.level"));
    }
    
    @Test
    public void argsOverrideSystemProperties() {
        System.setProperty("melaza.logger.level", "trace");
        Config config = new Config("logger.level:info");
        assertEquals("info", config.getProperty("logger.level"));
    }

}
