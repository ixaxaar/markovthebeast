package thebeast.pml.parser;

import jline.Completor;

import java.util.List;
import java.util.Stack;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.lang.reflect.Method;

/**
 *
 */
public class PMLCompletor implements Completor {

    private Scope scope;

    public PMLCompletor(Scope scope) {
        this.scope = scope;
    }

    public int complete(String buffer, int cursor, List candidates) {
        Stack<Context> stack = new Stack<Context>();
        int assign = buffer.indexOf('=',cursor);
        if (assign!=-1)
            buffer = buffer.substring(assign + 1);

        int lparen = buffer.indexOf('(',cursor);
        if (lparen == -1){
            int period = buffer.indexOf('.',cursor);
            if (period != -1){
                SortedSet<String> names = new TreeSet<String>(scope.getUsableObjects(null)).tailSet(buffer);
                for (String name : names)
                    if (name.startsWith(buffer)) candidates.add(name);
                return 0;
            } else {
                String name = buffer.substring(0,period);
                String method = buffer.substring(period + 1);
                Object object = scope.get(name);
                if (object == null) return -1;
                //look for methods to call
                for (Method m : scope.getUsableMethods(null,object)){
                    if (m.getName().startsWith(buffer)) candidates.add(m.getName());
                }
                return period + 1;
            }
        }
        return 0;
    }

    private Context parse(String buffer, int cursor){

        return null;
    }

    private static interface Context {

    }

    private static class ObjectContext implements Context {
                 
    }


}
