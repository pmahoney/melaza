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
public class JRubyMethodTranslator {
    
    public static final class MethodDescriptor {
        
        public final String className;
        
        public final String methodName;
        
        public MethodDescriptor(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
        
        @Override
        public int hashCode() {
            return className.hashCode() + methodName.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (!(obj instanceof MethodDescriptor)) {
                return false;
            } else {
                final MethodDescriptor other = (MethodDescriptor) obj;
                return (className.equals(other.className) &&
                        methodName.equals(other.methodName));
            }
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(JRubyMethodTranslator.class);

    private static final Pattern RUBYJIT_PATTERN =
            Pattern.compile("^(rubyjit/[\\w:]+)\\$\\$(\\w+)_\\p{XDigit}+$");
    
    public MethodDescriptor translate(String className, String methodName) {
        return translate(new MethodDescriptor(className, methodName));
    }
    
    public MethodDescriptor translate(MethodDescriptor desc) {
        final Matcher m = RUBYJIT_PATTERN.matcher(desc.className);
        if (m.matches()) {
            final MethodDescriptor ret;
            if ("<init>".equals(desc.methodName)) {
                // leave <init> because that's how we detect that we're in a constructor
                ret = new MethodDescriptor(m.group(1), desc.methodName);
            } else {
                ret = new MethodDescriptor(m.group(1), m.group(2));
            }
            logger.debug("mapping {}#{} to {}#{}",
                         new Object[] { desc.className, desc.methodName,
                                        ret.className, ret.methodName });
            return ret;
        } else {
            if (desc.className.startsWith("rubyjit/")) {
                logger.debug("no match on {}", desc.className);
            }
            return desc;
        }
    }

}
