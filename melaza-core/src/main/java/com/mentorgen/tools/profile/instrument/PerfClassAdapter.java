/*
Copyright (c) 2005, MentorGen, LLC
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

+ Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
+ Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
+ Neither the name of MentorGen LLC nor the names of its contributors may be 
  used to endorse or promote products derived from this software without 
  specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */
package com.mentorgen.tools.profile.instrument;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.polycrystal.melaza.instrument.DefaultInstrumenter;
import org.polycrystal.melaza.instrument.Instrumenter;
import org.polycrystal.melaza.instrument.JRubyInstrumenter;
import org.polycrystal.melaza.instrument.MethodInstrumenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mentorgen.tools.profile.Controller;

/**
 * 
 * @author Andrew Wilcox
 * @author Patrick Mahoney
 * @see org.objectweb.asm.jip.ClassAdapter
 */
public class PerfClassAdapter extends ClassVisitor {
    
    private static final Logger logger = LoggerFactory.getLogger(PerfClassAdapter.class);

    private String className;
    
    private final Instrumenter[] instrumenters;
    
    public PerfClassAdapter(ClassVisitor visitor, String theClass) {
        super(Opcodes.ASM4, visitor);
        this.className = theClass;
        
        instrumenters = new Instrumenter[] {
            new JRubyInstrumenter(),
            new DefaultInstrumenter()
        };
    }
    
    public MethodVisitor visitMethod(int arg,
                                     String name,
                                     String descriptor,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor mv = super.visitMethod(arg, 
                                             name, 
                                             descriptor, 
                                             signature, 
                                             exceptions);
        // TODO: method signatures (descriptor)
        
        // TODO: actually have a list of these, avoid null?
        MethodInstrumenter methodInstrumenter = null;
        for (Instrumenter instrumenter : instrumenters) {
            methodInstrumenter = instrumenter.getMethodInstrumenter(className, name);
            if (methodInstrumenter != null) break;
        }
        
        if (methodInstrumenter != null) {
            logger.debug("instrumenting {}.{} using {}",
                         new Object[] { className, name, methodInstrumenter });
            return new PerfMethodAdapter(mv, methodInstrumenter);
        } else {
            return mv;
        }
    }
    
}
