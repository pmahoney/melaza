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

    private static final Pattern RUBYJIT_PATTERN =
            Pattern.compile("^(rubyjit/[\\w:]+)\\$\\$(\\w+)_\\p{XDigit}+$");

    /**
     * Note: due to the constraints of constructors and the need for both
     * className and methodName to translate a method name, the regex
     * ends up being matched twice.
     * 
     * @param className
     * @param methodName
     */
    public JRubyJitMethodInstrumenter(String className, String methodName) {
        super(translateClassName(className),
              translateMethodName(className, methodName));
        logger.debug("mapping {}#{} to {}#{}",
                     new Object[] { className, methodName,
                                    getClassName(), getMethodName() });
    }
    
    public static String translateClassName(String className) {
        final Matcher m = RUBYJIT_PATTERN.matcher(className);
        if (m.matches()) {
            return m.group(1);
        } else {
            if (className.startsWith("rubyjit/")) {
                logger.debug("no class name match on {}", className);
            }
            return className;
        }
    }
    
    public static String translateMethodName(String className, String methodName) {
        if ("<init>".equals(methodName)) {
            // leave <init> because that's how we detect that we're in a constructor
            return methodName;
        } else {
            final Matcher m = RUBYJIT_PATTERN.matcher(className);
            if (m.matches()) {
                return m.group(2);
            } else {
                logger.debug("no method name match on {}", className);
                return methodName;
            }
        }
    }

}
