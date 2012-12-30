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
     * <p>{@code java -javaagent:melaza-agent.jar -Dprofile.properties=file}
     * 
     * @param args
     * @param inst
     */
    public static void premain(String args, Instrumentation inst) {
        Properties props = null;
        
        configureLogger();
        
        if (args == null || args.length() == 0 || args.equals("null")) {
            props = new Properties();
        } else {
            try {
                final BufferedInputStream input =
                        new BufferedInputStream(new FileInputStream(args));
                try {
                    props = new Properties();
                    props.load(input);
                } catch (IOException e) {
                    System.err.println("warning: error loading " + args);
                } finally {
                    try { input.close(); } catch (IOException e) {}
                }
            } catch (FileNotFoundException e1) {
                System.err.println("warning: no such file " + args);
                props = new Properties();
            }
        }
        
        inst.addTransformer(new Transformer());
    }
    
    private static void configureLogger() {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        try {
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            // TODO: make these properties configurable as well as logback config itself
            context.putProperty("level", "debug");
            final URL url = Agent.class.getClassLoader().getResource("logback-melaza.xml");
            configurator.doConfigure(url);
        } catch (JoranException je) {
            // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);

        logger.info("Starting Melaza Profiler");
    }

}