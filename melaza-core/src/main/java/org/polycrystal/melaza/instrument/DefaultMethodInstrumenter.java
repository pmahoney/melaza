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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMethodInstrumenter implements MethodInstrumenter {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultMethodInstrumenter.class);

    private final String className;
    
    private final String methodName;
    
    private final String profilerClass;

    public DefaultMethodInstrumenter(String className, String methodName) {
        this.profilerClass = "com/mentorgen/tools/profile/runtime/Profile";
        this.methodName = methodName;
        this.className = className;
    }
    
    public final String getClassName() {
        return className;
    }
    
    public final String getMethodName() {
        return methodName;
    }
    
    public final String getProfilerClass() {
        return profilerClass;
    }
    
    private void profilerCall(MethodVisitor mv, String profilerMethod) {
        mv.visitLdcInsn(className);
        mv.visitLdcInsn(methodName);
        mv.visitMethodInsn(INVOKESTATIC, 
                           profilerClass,
                           profilerMethod,
                           "(Ljava/lang/String;Ljava/lang/String;)V");
    }
    
    @Override
    public void profilerStart(MethodVisitor mv) {
        profilerCall(mv, "start");
    }

    @Override
    public void profilerEnd(MethodVisitor mv) {
        profilerCall(mv, "end");
    }
    
    @Override
    public void profilerBeginWait(MethodVisitor mv) {
        profilerCall(mv, "beginWait");
    }

    @Override
    public void profilerEndWait(MethodVisitor mv) {
        profilerCall(mv, "endWait");
    }
    
    @Override
    public void profilerUnwind(MethodVisitor mv, String labelType) {
        mv.visitLdcInsn(className);
        mv.visitLdcInsn(methodName);
        mv.visitLdcInsn(labelType);
        mv.visitMethodInsn(INVOKESTATIC, 
                           profilerClass,
                           "unwind",
                           "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    }

}