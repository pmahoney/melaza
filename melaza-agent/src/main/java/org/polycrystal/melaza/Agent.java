package org.polycrystal.melaza;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Properties;

import com.mentorgen.tools.profile.instrument.Transformer;

public class Agent {

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

}
