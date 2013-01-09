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

import static org.junit.Assert.assertEquals;
import static org.polycrystal.melaza.instrument.JRubyJitMethodInstrumenter.MethodDescriptor.translate;

import org.junit.Test;
import org.polycrystal.melaza.instrument.JRubyJitMethodInstrumenter.MethodDescriptor;

/**
 * 
 * @since Jan 8, 2013
 * @author Patrick Mahoney <pat@polycrystal.org>
 *
 */
public class JRubyJitMethodInstrumenterTest {
    
    // in groups of 4: rubyjit class, method; expected class, method
    private final String[] cases = new String[] {
        "rubyjit/Gem::Specification$$platform_EB94BD653ED20C5722676233CCDC7B3ACED15B9A405384577",
        "__file__",
        "rubyjit/Gem::Specification",
        "platform",
        
        "rubyjit/Gem::Version$$correct?_E33A0EF8080F33E29D208824F256265FA08E2A96405384577",
        "__file__",
        "rubyjit/Gem::Version",
        "correct?",
        
        // this seems to be how rubyjit represents the spaceship <=> operator
        "rubyjit/Gem::Version$$\\=\\^=\\__B6B8D7200747E4DC7C29A2D3359F955400DBE2DC405384577",
        "setPosition",
        "rubyjit/Gem::Version",
        "<=>",
        
        "rubyjit/C$$\\=\\{\\}_722D754E15472A5D28B1A2E02038A59EC4F438F9170051694",
        "__file__",
        "rubyjit/C",
        "[]",

        "rubyjit/C$$\\=\\{\\}\\=_722D754E15472A5D28B1A2E02038A59EC4F438F9170051694",
        "__file__",
        "rubyjit/C",
        "[]=",

        "notrubyjit/SomeClass$$m_ABCDEF",
        "someMethod",
        "notrubyjit/SomeClass$$m_ABCDEF",
        "someMethod"
    };
    
    @Test
    public void translatesRubyjitMethods() {
        for (int i = 0; i < cases.length; i += 4) {
            final String rubyjitClass = cases[i+0];
            final String rubyjitMethod = cases[i+1];
            final String expectedClass = cases[i+2];
            final String expectedMethod = cases[i+3];
            
            final MethodDescriptor desc = translate(rubyjitClass, rubyjitMethod);
            assertEquals(expectedClass, desc.className);
            assertEquals(expectedMethod, desc.methodName);
        }
    }

}
