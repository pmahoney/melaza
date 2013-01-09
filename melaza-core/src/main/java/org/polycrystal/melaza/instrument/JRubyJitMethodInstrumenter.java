/*
 * Copyright (c) 2013 Patrick Mahoney
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.polycrystal.melaza.instrument;

import static java.util.regex.Pattern.quote;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translates rubyjit class and method names into their ruby equivalents.
 * For example, on the java side a compiled ruby method may be in class
 * {@code rubyjit/Mod::Klass$$to_s_<hex string>} with method name
 * {@code __file__}.  This would be translated to class
 * {@code rubyjit/Mod::Klass} and method {@code to_s}.
 * 
 * <p>Note that later steps split the full class name into a package name and short
 * class name. 
 * 
 * @since Jan 1, 2013
 * @author Patrick Mahoney <pat@polycrystal.org>
 *
 */
public class JRubyJitMethodInstrumenter extends DefaultMethodInstrumenter {
    
    private static final Logger logger = LoggerFactory.getLogger(JRubyJitMethodInstrumenter.class);

    // See JRubyJitMethodInstrumenterTest for example class names.
    private static final Pattern RUBYJIT_PATTERN =
            Pattern.compile("^(rubyjit/[\\w:]+)\\$\\$([\\w=\\?\\^\\\\{}]+)_\\p{XDigit}+$");

    /** prefix given to special case ops such as <=>, [], []=, etc. */
    private static final String OP_PREFIX = "\\=";
    
    /**
     * Simple representation of a class and method name.
     * 
     * @since Jan 8, 2013
     * @author Patrick Mahoney <pat@polycrystal.org>
     *
     */
    public static final class MethodDescriptor {

        public final String className;
        
        public final String methodName;
        
        public MethodDescriptor(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
        
        public static String translateOp(String op) {
            return op.substring(OP_PREFIX.length())
                    .replaceAll(quote("{"), "[")
                    .replaceAll(quote("}"), "]")
                    .replaceAll(quote("^"), "<")
                    .replaceAll(quote("_"), ">")
                    .replaceAll(quote("\\"), "");
        }

        /**
         * Translate a class and method name in the rubyjit package into the (rough)
         * equivalent names that would be seen within Ruby.
         * 
         * @param rubyjitClass
         * @param rubyjitMethod
         * @return
         */
        public static MethodDescriptor translate(String rubyjitClass, String rubyjitMethod) {
            final Matcher m = RUBYJIT_PATTERN.matcher(rubyjitClass);
            if (m.matches()) {
                final String className = m.group(1);
                final String methodName;
                
                if ("<init>".equals(rubyjitMethod)) {
                    // leave <init> because that's how we detect that we're in a constructor
                    methodName = rubyjitMethod;
                } else if (m.group(2).startsWith(OP_PREFIX)) {
                    methodName = translateOp(m.group(2));
                } else {
                    methodName = m.group(2);
                }
                return new MethodDescriptor(className, methodName);
            } else {
                logger.warn("no rubyjit match on {} {}", rubyjitClass, rubyjitMethod);
                return new MethodDescriptor(rubyjitClass, rubyjitMethod);
            }
            
        }

    }

    /**
     * 
     * @param rubyjitClass A java class in the rubyjit package
     * @param rubyjitMethod
     */
    public JRubyJitMethodInstrumenter(String rubyjitClass, String rubyjitMethod) {
        this(MethodDescriptor.translate(rubyjitClass, rubyjitMethod));
    }
    
    public JRubyJitMethodInstrumenter(MethodDescriptor desc) {
        super(desc.className, desc.methodName);
    }

}
