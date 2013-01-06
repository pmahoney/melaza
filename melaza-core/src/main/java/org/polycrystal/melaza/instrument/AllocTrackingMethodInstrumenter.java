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

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;

/**
 * TODO: probably easier to track alloc in the profiler itself? Otherwise
 * every instrumenter needs to worry about this.
 * 
 * @since Jan 5, 2013
 * @author Patrick Mahoney <pat@polycrystal.org>
 *
 */
public class AllocTrackingMethodInstrumenter extends DefaultMethodInstrumenter {

    /**
     * @param className
     * @param methodName
     */
    public AllocTrackingMethodInstrumenter(String className, String methodName) {
        super(className, methodName);
    }

    @Override
    public void profilerStart(MethodVisitor mv) {
        mv.visitLdcInsn(getClassName());
        mv.visitMethodInsn(INVOKESTATIC, 
                           getProfilerClass(),
                           "alloc", 
                           "(Ljava/lang/String;)V");
        super.profilerStart(mv);
    }
}
