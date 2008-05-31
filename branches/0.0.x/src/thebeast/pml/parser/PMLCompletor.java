package thebeast.pml.parser;

import jline.Completor;

import java.util.*;
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
    return 0;
  }

  private int complete(String buffer, int cursor, List<String> candidates, Object oContext, Class<?> tContext) {
    return 0;
  }

  private abstract class Rule {

    public final static int NOPARSE = -1;
    public final static int INPARSE = -2;

    /**
     * @param buffer       the buffer to parse
     * @param from         from where should we pass
     * @param candidates   add candidate strings to complete this rule
     * @param classContext class the expression needs to have
     * @return -1 if rule can't parse the input, "from" if it can parse the input but is not finished (and puts adds
     *         candidates) and the position after the end of this rule if it could fully parse it.
     */
    public abstract int parse(String buffer, int from, List<String> candidates, Class<?> classContext);

  }

  private class Term extends Rule {

    private Call call = new Call();
    private ObjectName objectName = new ObjectName();

    public int parse(String buffer, int from, List<String> candidates, Class<?> classContext) {
      int next = call.parse(buffer, from, candidates, classContext);
      if (next == INPARSE) return INPARSE;
      if (next != NOPARSE) return next;
      next = objectName.parse(buffer, from, candidates, classContext);
      if (next == INPARSE) return INPARSE;
      return next;
    }
  }

  private class Call extends Rule {

    private ObjectName var = new ObjectName();
    private Terminal
            period = new Terminal('.'),
            lparen = new Terminal('('),
            rparen = new Terminal(')');
    private MethodName methodName = new MethodName();
    private MethodArgs args = new MethodArgs();
    private Term term = new Term();

    public Call() {

    }

    public int parse(String buffer, int from, List<String> candidates, Class<?> classContext) {
      int next = var.parse(buffer, from, candidates, null);
      if (next == NOPARSE) return NOPARSE;
      if (next == INPARSE) return INPARSE;
      next = period.parse(buffer, next, candidates, null);
      if (next == NOPARSE) return NOPARSE;
      methodName.setClazz(var.getObject().getClass());
      next = methodName.parse(buffer, next, candidates, classContext);
      if (next == NOPARSE) return NOPARSE;
      if (next == INPARSE) return INPARSE;
      next = lparen.parse(buffer, next, candidates, null);
      if (next == NOPARSE) return NOPARSE;
      for (Method m: methodName.getMethods()){
        args.setMethod(m);
        next = args.parse(buffer, next, candidates, null);
        if (next == INPARSE || next == NOPARSE) continue;
        break;
      }
      if (next == NOPARSE) return NOPARSE;
      if (next == INPARSE) return INPARSE;      
      next = rparen.parse(buffer, next, candidates, null);
      return next;
    }

  }


  private class MethodName extends Rule {

    private Class clazz;
    private HashSet<Method> methods = new HashSet<Method>();

    public void setClazz(Class clazz) {
      this.clazz = clazz;
    }

    public int parse(String buffer, int from, List<String> candidates, Class<?> classContext) {
      from = scanWhiteSpace(buffer, from);
      String begin = buffer.substring(from);
      int winningLength = 0;
      String winner = null;
      LinkedList<String> tmp = new LinkedList<String>();
      for (Method m : scope.getUsableMethods(clazz, classContext)){
        String name = m.getName();
        if (begin.startsWith(name)) {
          if (name.length() > winningLength) {
            winningLength = name.length();
            winner = name;
          }
        } else if (name.startsWith(begin)) {
          tmp.add(name);
        }
      }
      if (winner == null) {
        if (tmp.size() > 0) {
          candidates.addAll(tmp);
          return INPARSE;
        } else
          return NOPARSE;
      }
      //object = scope.get(winner);
      return from + winner.length();
    }

    public Collection<Method> getMethods() {
      return null;
    }
  }

  private class ObjectName extends Rule {

    private Object object;

    public int parse(String buffer, int from, List<String> candidates, Class<?> classContext) {
      from = scanWhiteSpace(buffer, from);
      String begin = buffer.substring(from);
      int winningLength = 0;
      String winner = null;
      LinkedList<String> tmp = new LinkedList<String>();
      for (String name : scope.getObjectsByClass(classContext)) {
        if (begin.startsWith(name)) {
          if (name.length() > winningLength) {
            winningLength = name.length();
            winner = name;
          }
        } else if (name.startsWith(begin)) {
          tmp.add(name);
        }
      }
      if (winner == null) {
        if (tmp.size() > 0) {
          candidates.addAll(tmp);
          return INPARSE;
        } else
          return NOPARSE;
      }
      object = scope.get(winner);
      return from + winner.length();
    }

    public Object getObject() {
      return object;
    }
  }

  private class Terminal extends Rule {

    private char terminal;

    public Terminal(char terminal) {
      this.terminal = terminal;
    }

    public int parse(String buffer, int from, List<String> candidates, Class<?> classContext) {
      if (from == buffer.length()){
        candidates.add(new String(new char[]{terminal}));
        return INPARSE;
      }
      from = scanWhiteSpace(buffer, from);
      if (from > buffer.length() || buffer.charAt(from) != terminal) return NOPARSE;
      return from + 1;
    }

  }

  private class MethodArgs extends Rule {

    private Method method;

    public void setMethod(Method method) {
      this.method = method;
    }

    public int parse(String buffer, int from, List<String> candidates, Class<?> classContext) {
      return 0;
    }
  }


  static int scanWhiteSpace(String buffer, int from) {
    for (int i = from; i < buffer.length(); ++i) {
      if (buffer.charAt(i) != ' ' && buffer.charAt(i) != '\n' && buffer.charAt(i) != '\t') return i;
    }
    return buffer.length();
  }



}
