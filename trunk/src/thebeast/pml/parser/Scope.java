package thebeast.pml.parser;

import thebeast.util.HashMultiMapList;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 */
public class Scope {

    private HashMap<String,Object> values = new HashMap<String, Object>();
    private HashMultiMapList<Class,String> class2objname = new HashMultiMapList<Class, String>();

    private HashSet<Class<?>> classes = new HashSet<Class<?>>();

     public void addClass(Class<Object> clazz){
         classes.add(clazz);
     }

     public Collection<Class<?>> getClasses(){
         return classes;
     }

    public void set(String name, Object value){
        if (values.get(name) != null){
            Object old = values.get(name);
            class2objname.get(old.getClass()).remove(name);
        }
        values.put(name,value);
        class2objname.add(value.getClass(),name);
    }

    public Collection<String> getObjectsByClass(Class clazz){
        return class2objname.get(clazz);
    }

    public Object get(String name){
        return values.get(name);
    }

    public void setByString(String name, String value){
        if (value.startsWith("\"") && value.endsWith("\"")){
            set(name,value.substring(1,value.length()-1));
            return;
        }
        if (name.equals("true")){
            set(name, true);
            return;
        }
        if (name.equals("false")){
            set(name, false);
            return;
        }
        try {
            set(name, Double.parseDouble(value));
        } catch (NumberFormatException ex1){
            try {
                set(name, Integer.parseInt(value));
            } catch (NumberFormatException ex2){
                set(name, new File(value));
            }
        }

    }

    public Collection<Constructor> getUsableConstructors(Class<?> typeContext){
        LinkedList<Constructor> result = new LinkedList<Constructor>();
        for (Class<?> c : classes){
            if (typeContext == null || typeContext.isAssignableFrom(c))
                for (Constructor constructor : c.getConstructors())
                    result.add(constructor);
        }
        return result;

    }

    public Collection<Method> getUsableMethods(Class<?> typeContext, Object objectContext){
        LinkedList<Method> result = new LinkedList<Method>();
        for (Method m: objectContext.getClass().getMethods()){
            if (typeContext == null || typeContext.isAssignableFrom(m.getReturnType()))
                result.add(m);
        }
        return result;
    }

    public Collection<String> getUsableObjects(Class<?> typeContext){
        LinkedList<String> result = new LinkedList<String>();
        for (Class c : class2objname.keySet()){
            if (typeContext == null || typeContext.isAssignableFrom(c))
                result.addAll(class2objname.get(c));
        }
        return result;
    }

    public Collection<String> getObjectNames() {
        return values.keySet(); 
    }
}
