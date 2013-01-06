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

/**
 * 
 * @since Jan 5, 2013
 * @author Patrick Mahoney <pat@polycrystal.org>
 *
 */
public class JRubyInstrumenter implements Instrumenter {

    /* (non-Javadoc)
     * @see org.polycrystal.melaza.instrument.Instrumenter#getMethodInstrumenter(java.lang.String, java.lang.String)
     */
    @Override
    public MethodInstrumenter getMethodInstrumenter(String className, String methodName) {
        if (className.startsWith("org/jruby/ast/FCall") && "interpret".equals(methodName)) {
            return new JRubyInterpMethodInstrumenter();
        } else if (className.startsWith("org/jruby/Ruby")) {
            return new DefaultMethodInstrumenter(className, methodName);
        } else if (className.startsWith("org/jruby")) {
            return new NullMethodInstrumenter();
        } else if (className.startsWith("rubyjit/")) {
            return new JRubyJitMethodInstrumenter(className, methodName);
        } else {
            return null;
        }
    }

}
