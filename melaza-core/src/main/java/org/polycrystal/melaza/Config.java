package org.polycrystal.melaza;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Config extends Properties {

    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    
    /**
     * System properties with this prefix will be loaded into each Config object.
     */
    public static final String SYSTEM_PROPERTY_PREFIX = "melaza.";
    
    /**
     * Create a new Config object with properties taken from, in
     * order of precedence:
     * 
     * <ol>
     * <li>an arg string in the format {@code key1:value1;key2:value2...}
     * <li>System properties with the prefix {@code melaza.}
     * <li>a properties file (given as an arg or system property)
     * </ol>
     * 
     * @param args
     */
    public Config(String args) {
        // load system and arg string into a temporary Config
        // just so we can extract any configured properties file
        final Config temp = new Config();
        temp.loadSystemProperties(SYSTEM_PROPERTY_PREFIX);
        temp.loadArgString(args);

        // load the properties file, which has lowest priority
        loadFile(temp.getProperties());
        // now load the system properties and arg string values
        // which will overwrite properties loaded from the file
        putAll(temp);
    }
    
    /**
     * Create a new, uninitialized Config object
     */
    private Config() {
    }

    private static void closeQuietly(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            logger.warn("error closing {}", c, e);
        }
    }

    /**
     * Load a properties file.
     * 
     * @param file
     */
    public void loadFile(File file) {
        if (file == null) {
            return;
        }
        
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                load(reader);
            } catch (IOException e) {
                logger.error("error loading properties: {}", file, e);
            } finally {
                closeQuietly(reader);
            }
        } catch (FileNotFoundException e) {
            logger.error("error loading properties: file not found: {}", file, e);
        }
    }
    
    /**
     * Load properties from {@code args} which must be in
     * a Java properties-esque format: {@code key1:value1;key2:value2...}
     * 
     * @param args
     */
    public void loadArgString(String args) {
        if (args == null) {
            return;
        }

        final StringReader reader = new StringReader(args.replace(";", "\n"));
        try {
            load(reader);
        } catch (IOException e) {
            logger.error("error reading arg string {}", args, e);
        }
    }
    
    /**
     * For each system property starting with {@code prefix}, strip the prefix
     * and set a key in this Config to the corresponding value.
     * 
     * @param prefix
     */
    public void loadSystemProperties(String prefix) {
        if (prefix == null) {
            return;
        }

        for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                final String key = (String) entry.getKey();
                final String value = (String) entry.getValue();
                if (key.startsWith(prefix)) {
                    final String configKey = key.substring(prefix.length());
                    if (configKey.length() > 0) {
                        setProperty(configKey, value);
                    }
                }
            }
        }
    }
    
    private File getFileProperty(String key) {
        final String value = getProperty(key);
        if (value == null) {
            return null;
        }
        return new File(value);
    }
    
    public String getLoggerLevel() {
        return getProperty("logger.level", "info");
    }
    
    public File getProperties() {
        return getFileProperty("properties");
    }

}
