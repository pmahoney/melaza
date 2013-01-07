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

import static org.objectweb.asm.Opcodes.*;

import org.jruby.ast.FCallNode;
import org.jruby.runtime.builtin.IRubyObject;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mentorgen.tools.profile.runtime.Profile;

/**
 * 
 * @since Jan 5, 2013
 * @author Patrick Mahoney <pat@polycrystal.org>
 *
 */
public class JRubyInterpMethodInstrumenter implements MethodInstrumenter {
    
    private static final Logger logger = LoggerFactory.getLogger(JRubyInterpMethodInstrumenter.class);
    
    public static String getClassName(IRubyObject target) {
        final String className;
        if (target.isModule()) {
            className = target.toString();
        } else if (target.isClass()) {
            className = target.toString();
        } else {
            className = target.getType().toString();
        }
        return "rubyinterp/" + className;
    }
    
    public static void profileStart(IRubyObject target, FCallNode node) {
        Profile.start(getClassName(target), node.getName());
    }
    
    public static void profileEnd(IRubyObject target, FCallNode node) {
        Profile.end(getClassName(target), node.getName());
    }
    
    public static void profileBeginWait(IRubyObject target, FCallNode node) {
        Profile.beginWait(getClassName(target), node.getName());
    }

    public static void profileEndWait(IRubyObject target, FCallNode node) {
        Profile.endWait(getClassName(target), node.getName());
    }

    public static void profileUnwind(IRubyObject target, FCallNode node, String type) {
        Profile.unwind(getClassName(target), node.getName(), type);
    }

    public void profilerCall(MethodVisitor mv, String profilerMethod) {
        mv.visitVarInsn(ALOAD, 3); // the self obj
        mv.visitVarInsn(ALOAD, 0); // the call node
        mv.visitMethodInsn(INVOKESTATIC,
                           "org/polycrystal/melaza/instrument/JRubyInterpMethodInstrumenter",
                           profilerMethod,
                           "(Lorg/jruby/runtime/builtin/IRubyObject;Lorg/jruby/ast/FCallNode;)V");
    }


    /* (non-Javadoc)
     * @see org.polycrystal.melaza.instrument.MethodInstrumenter#profilerStart(org.objectweb.asm.MethodVisitor)
     */
    @Override
    public void profilerStart(MethodVisitor mv) {
        profilerCall(mv, "profileStart");
    }

    /* (non-Javadoc)
     * @see org.polycrystal.melaza.instrument.MethodInstrumenter#profilerEnd(org.objectweb.asm.MethodVisitor)
     */
    @Override
    public void profilerEnd(MethodVisitor mv) {
        profilerCall(mv, "profileEnd");
    }

    /* (non-Javadoc)
     * @see org.polycrystal.melaza.instrument.MethodInstrumenter#profilerBeginWait(org.objectweb.asm.MethodVisitor)
     */
    @Override
    public void profilerBeginWait(MethodVisitor mv) {
        profilerCall(mv, "profileBeginWait");
    }

    /* (non-Javadoc)
     * @see org.polycrystal.melaza.instrument.MethodInstrumenter#profilerEndWait(org.objectweb.asm.MethodVisitor)
     */
    @Override
    public void profilerEndWait(MethodVisitor mv) {
        profilerCall(mv, "profileEndWait");
    }

    /* (non-Javadoc)
     * @see org.polycrystal.melaza.instrument.MethodInstrumenter#profilerUnwind(org.objectweb.asm.MethodVisitor, java.lang.String)
     */
    @Override
    public void profilerUnwind(MethodVisitor mv, String labelType) {
        mv.visitVarInsn(ALOAD, 3); // the self obj
        mv.visitVarInsn(ALOAD, 0); // the call node
        mv.visitLdcInsn(labelType);
        mv.visitMethodInsn(INVOKESTATIC,
                           "org/polycrystal/melaza/instrument/JRubyInterpMethodInstrumenter",
                           "unwind",
                           "(Lorg/jruby/runtime/builtin/IRubyObject;Lorg/jruby/ast/FCallNode;Ljava/lang/String;)V");
    }

}
