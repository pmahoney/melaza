package org.polycrystal.melaza;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.mentorgen.tools.profile.instrument.Transformer;

public class Agent {
    
    private static final Logger logger = LoggerFactory.getLogger(Agent.class);

    /**
     * Usage:
     * 
     * <p>{@code java -javaagent:melaza-agent.jar=properties:file -Dmelaza.properties=file}
     * 
     * <p>Configuration values can be set as agent args (by appending {@code =args} to
     * the javaagent line), by system properties prefixed with {@code melaza.}, or
     * through a properties file specified by one of the above methods.
     * 
     * @param args
     * @param inst
     */
    public static void premain(String args, Instrumentation inst) {
        final Config config = new Config(args);
        configureLogger(config);
        inst.addTransformer(new Transformer());
    }
    
    private static void configureLogger(Config config) {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        try {
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            context.putProperty("level", config.getLoggerLevel());
            // We're on the boot classpath, so our resources are found in the system
            // class loader
            final URL url = ClassLoader.getSystemResource("logback-melaza.xml");
            configurator.doConfigure(url);
        } catch (JoranException je) {
            // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);

        logger.info("Starting Melaza Profiler");
    }

}
