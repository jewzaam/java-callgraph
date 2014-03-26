/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.gousiosg.javacg.stat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import gr.gousiosg.javacg.stat.model.CGClass;
import java.util.List;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.Type;

/**
 *
 * @author nmalik
 */
public class JsonNodeParser {

    public static ObjectNode objectNode() {
        return new ObjectNode(JsonNodeFactory.instance);
    }

    /**
     * Takes input className and splits it into package and name (simple name) and returns in new ObjectNode with
     * following structure: {class:{package:<package>,name:<simple-name>}}
     *
     * @param className
     * @return
     */
    public static JsonNode toJsonFromClass(String className) {
        if ("void".equals(className)) {
            return new TextNode(className);
        }

        String pkg = className.substring(0, className.lastIndexOf('.'));
        String name = className.substring(className.lastIndexOf('.') + 1);

        ObjectNode output = objectNode();
        ObjectNode clazz = output.objectNode();
        output.put("class", clazz);
        clazz.put("package", pkg);
        clazz.put("name", name);

        return output;
    }
    
    public static JsonNode toJson(List<CGClass> classes) {
        ArrayNode output = new ArrayNode(JsonNodeFactory.instance);
        for (CGClass c: classes) {
            output.add(toJson(c));
        }
        return output;
    }

    public static JsonNode toJson(CGClass clazz) {
        ObjectNode output = objectNode();
        
        output.put("className", clazz.className);
        output.put("extends", clazz.superClassName);
        
        ArrayNode method = output.arrayNode();
        output.put("methods", method);
        
        for (CGMethod m: clazz.methods) {
            // TODO finish me.. tired
        }
        
        return output;
    }
    
    public static JsonNode toJson(JavaClass jc, Method[] methods) {
        ObjectNode output = objectNode();

        output.put("package", jc.getPackageName());
        output.put("class", jc.getClassName());

        output.put("extends", jc.getSuperclassName());

        if (jc.getInterfaceNames() != null && jc.getInterfaceNames().length > 0) {
            ArrayNode an = output.arrayNode();
            output.put("implements", an);
            for (String i : jc.getInterfaceNames()) {
                an.add(i);
            }
        }
        
        ArrayNode an = output.arrayNode();
        output.put("methods", an);

        return output;
    }

    public static JsonNode toJson(InvokeInstruction ii, ConstantPoolGen cp) {
        ObjectNode output = objectNode();

        output.put("name", ii.getMethodName(cp));
        output.put("returning", toJsonFromClass(ii.getReturnType(cp).toString()));

        if (ii.getArgumentTypes(cp) != null && ii.getArgumentTypes(cp).length > 0) {
            ArrayNode an = output.arrayNode();
            output.put("arguments", an);
            for (Type type : ii.getArgumentTypes(cp)) {
                an.add(toJsonFromClass(type.toString()));
            }
        }

        if (ii.getExceptions() != null && ii.getExceptions().length > 0) {
            ArrayNode an = output.arrayNode();
            output.put("throws", an);
            for (Class c : ii.getExceptions()) {
                an.add(toJsonFromClass(c.getName().toString()));
            }
        }

        return output;
    }
}
