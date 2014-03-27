/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.gousiosg.javacg.stat;

import gr.gousiosg.javacg.stat.model.CGClass;
import gr.gousiosg.javacg.stat.model.CGMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;

/**
 *
 * @author nmalik
 */
public class CallGraphCache {
    private static final Map<String, CGClass> cacheClass = new HashMap<>();
    private static final Map<String, CGMethod> cacheMethod = new HashMap<>();

    private static String getKeyForClass(String className) {
        return className;
    }

    protected static void register(CGClass cgc) {
        if (cacheClass.containsKey(cgc.key())) {
            return;
        }

        cacheClass.put(cgc.key(), cgc);
    }

    protected static void register(CGMethod cgm) {
        if (cacheMethod.containsKey(cgm.key())) {
            return;
        }

        cgm.clazz = cacheClass.get(getKeyForClass(cgm.className));
        cacheMethod.put(cgm.key(), cgm);
    }

    public static void register(JavaClass jc, MethodGen mg) {
//        System.out.println("register(JavaClass, MethodGen): " + jc.getClassName() + ", " + mg.getName());
        
        register(CGClass.create(jc));
        register(CGMethod.create(jc, mg));
    }

    public static void register(JavaClass jc, MethodGen mg, InvokeInstruction ii) {
//        System.out.println("register(JavaClass, MethodGen, InvokeInstruction): " + jc.getClassName() + ", " + mg.getName());
        // register caller class and method
        CGClass callerClass = CGClass.create(jc);
        register(callerClass);
        CGMethod callerMethod = CGMethod.create(jc, mg);
        register(callerMethod);
        callerClass.methods.add(callerMethod);

        // register target class and method
        CGClass targetClass = CGClass.create(jc, mg, ii);
        register(targetClass);
        CGMethod targetMethod = CGMethod.create(jc, mg, ii);
        register(targetMethod);
        targetClass.methods.add(targetMethod);

        // add caller / target references
        callerMethod.invoking.add(targetMethod);
        targetMethod.invokedBy.add(callerMethod);
        
//        System.out.println("class key = " + callerClass.key());
//        System.out.println("caller key = " + callerMethod.key());
//        System.out.println("target key = " + targetMethod.key());
    }

    public static void register(JavaClass jc, Method m) {
//        System.out.println("register(JavaClass, Method): " + jc.getClassName() + ", " + m.getName());
        register(CGClass.create(jc));
        register(CGMethod.create(jc, m));
    }

    /**
     * Convert call graph to json but starting from only classes that match the given rootRegex.
     *
     * @param rootRegex
     * @return
     */
    public static List<CGClass> getCallGraph(String rootRegex) {
        List<CGClass> output = new ArrayList<>();

        // find root classes
        CGClass current;
        for (CGClass root : cacheClass.values()) {
            if (root.className.matches(rootRegex)) {
                output.add(root);
            }
        }

        return output;
    }
}
