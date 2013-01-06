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
public class DefaultInstrumenter implements Instrumenter {
    
    /**
     * DefaultInstrumenter matches all methods except static initializers.
     * 
     * <p>
     * FIXME: Static initializers are excluded. The reason for this is the the
     * profiling algorithm we're using mirrors the call stack. Since static
     * initializers are called by the classloader and therefore aren't part of
     * the programs flow of control, static initializers can really mess up the
     * profiler, especially when they're called before the program's flow of
     * control is started (for example, the when the class with the main()
     * method has a static initalizer). So yes, this is a short comming in the
     * design of the profiler, but we're willing to live with it because this
     * profiler is lightweight and allows us to use it interactively.
     */

    @Override
    public MethodInstrumenter getMethodInstrumenter(String className, String methodName) {
        if (methodName.equals("<clinit>")) {
            return null;
        } else if (false && "<init>".equals(methodName)) {
            // TODO: configurable init tracking, but also TODO: move init tracking into profiler
            return new AllocTrackingMethodInstrumenter(className, methodName);
        } else {
            return new DefaultMethodInstrumenter(className, methodName);
        }
    }

}
