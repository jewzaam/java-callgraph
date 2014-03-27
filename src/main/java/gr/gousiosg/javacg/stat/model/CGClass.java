/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.gousiosg.javacg.stat.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReferenceType;

/**
 *
 * @author nmalik
 */
public class CGClass {
    public String className;
    public String superClassName;
    public final List<String> interfaceNames = new ArrayList<>();
    public final List<CGMethod> methods = new ArrayList<>();

    public String key() {
        return className;
    }

    public static CGClass create(JavaClass jc) {
        CGClass cgc = new CGClass();

        cgc.className = jc.getClassName();
        cgc.superClassName = jc.getSuperclassName();

        if (jc.getInterfaceNames() != null && jc.getInterfaceNames().length > 0) {
            cgc.interfaceNames.addAll(Arrays.asList(jc.getInterfaceNames()));
        }
        
        return cgc;
    }
    
    public static CGClass create(JavaClass jc, MethodGen mg, InvokeInstruction ii) {
        ReferenceType rt = ii.getReferenceType(mg.getConstantPool());

        CGClass cgc = new CGClass();

        cgc.className = rt.toString();
        
        return cgc;
    }
}
