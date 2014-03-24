/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gr.gousiosg.javacg.stat;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Type;

/**
 * The simplest of method visitors, prints any invoked method signature for all method invocations.
 *
 * Class copied with modifications from CJKM: http://www.spinellis.gr/sw/ckjm/
 */
public class MethodVisitor extends EmptyVisitor {

    JavaClass visitedClass;
    private final MethodGen mg;
    private final ConstantPoolGen cp;
    private final String format;

    public MethodVisitor(MethodGen m, JavaClass jc) {
        visitedClass = jc;
        mg = m;
        cp = mg.getConstantPool();
        /*
         {
         class: class,
         method: method,
         invokeType: {enum:INVOKEVIRTUAL,INVOKEINTERFACE,INVOKESPECIAL,INVOKESTATIC},
         reference: {
         class: class,
         method: method
         args:
         }
         }
         */
        format = "{\"class\":\"" + visitedClass.getClassName() + "\",\"method\":\"" + mg.getName() + "\",\"invokeType\":\"%s\",\"reference\":{\"class\":\"%s\",\"method\":\"%s\",\"args\":\"%s\"}}";
    }

    public void start() {
        if (mg.isAbstract() || mg.isNative()) {
            return;
        }
        for (InstructionHandle ih = mg.getInstructionList().getStart();
                ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();

            if (!visitInstruction(i)) {
                i.accept(this);
            }
        }
    }

    private boolean visitInstruction(Instruction i) {
        short opcode = i.getOpcode();

        return ((InstructionConstants.INSTRUCTIONS[opcode] != null)
                && !(i instanceof ConstantPushInstruction)
                && !(i instanceof ReturnInstruction));
    }

    private String getTypeString(Type[] types) {
        StringBuilder buff = new StringBuilder();
        for (int x = 0; types != null && x < types.length; x++) {
            buff.append(types[x].toString());
            if (x + 1 < types.length) {
                buff.append(",");
            }
        }
        return buff.toString();
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
        System.out.println(String.format(format, "INVOKEVIRTUAL", i.getReferenceType(cp), i.getMethodName(cp), getTypeString(i.getArgumentTypes(cp))));
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
        System.out.println(String.format(format, "INVOKEINTERFACE", i.getReferenceType(cp), i.getMethodName(cp), getTypeString(i.getArgumentTypes(cp))));
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
        System.out.println(String.format(format, "INVOKESPECIAL", i.getReferenceType(cp), i.getMethodName(cp), getTypeString(i.getArgumentTypes(cp))));
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
        System.out.println(String.format(format, "INVOKESTATIC", i.getReferenceType(cp), i.getMethodName(cp), getTypeString(i.getArgumentTypes(cp))));
    }
}
