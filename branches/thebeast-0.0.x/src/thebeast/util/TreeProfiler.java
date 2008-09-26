package thebeast.util;

import thebeast.pml.PropertyName;

import java.util.*;

/**
 * Hierachical Profiler.
 *
 * @author Sebastian Riedel
 */
public class TreeProfiler implements Profiler {

  protected ProfileNode root = new ProfileNode("root", null);
  protected ProfileNode current = root;
  private int maxPrintDepth = Integer.MAX_VALUE;

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("reset") && (Boolean) value)
      root = new ProfileNode("root", null);
    if (name.getHead().equals("maxDepth"))
      maxPrintDepth = (Integer) value;
  }

  public Object getProperty(PropertyName name) {
    if (name.getHead().equals("calls")) {
      return getCalls(name.getTail().toString());
    } else if (name.getHead().equals("total")) {
      return getTotalTime(name.getTail().toString());
    } else if (name.getHead().equals("average")) {
      return getAverageTime(name.getTail().toString());
    }
    return null;
  }

  protected class ProfileNode implements Comparable<ProfileNode> {
    int calls;
    long totalTime;
    long startedAt;
    String name;
    HashMap<String, ProfileNode> children = new HashMap<String, ProfileNode>();
    int order;
    ProfileNode parent;

    public ProfileNode(String name, ProfileNode parent) {
      this.name = name;
      this.parent = parent;
    }

    public double average() {
      return (double) totalTime / (double) calls;
    }

    public ProfileNode(String name, ProfileNode parent, int order) {
      this.name = name;
      this.order = order;
      this.parent = parent;
    }

    public ProfileNode getNode(String name) {
      ProfileNode result = children.get(name);
      if (result == null) {
        result = new ProfileNode(name, this);
        children.put(name, result);
      }
      return result;
    }

    public ProfileNode getQualifiedNode(String qualifiedName) {
      if (qualifiedName.contains(".")) {
        int index = qualifiedName.indexOf('.');
        String prefix = qualifiedName.substring(0, index);
        ProfileNode node = getNode(prefix);
        return node.getQualifiedNode(qualifiedName.substring(index+1));
      } else
        return getNode(qualifiedName);
    }

    public ProfileNode getNode(String name, int order) {
      ProfileNode result = children.get(name);
      if (result == null) {
        result = new ProfileNode(name, this, order);
        children.put(name, result);
      }
      return result;
    }


    public int compareTo(ProfileNode profileNode) {
      return order - profileNode.order;
    }

    public List<ProfileNode> getSortedChildren() {
      ArrayList<ProfileNode> result = new ArrayList<ProfileNode>(children.values());
      Collections.sort(result);
      return result;
    }

    public void print(StringBuffer buffer, int indent, int depth) {
      if (depth >= maxPrintDepth) return;
      appendWhiteSpace(buffer, indent);
      if (parent == null) {
        buffer.append(name).append("\n");
      } else {
        Formatter formatter = new Formatter();
        formatter.format("%-10s %-3.2fms (%d calls)\n", name, average(), calls);
        buffer.append(formatter.toString());
      }
      for (ProfileNode node : getSortedChildren()) {
        node.print(buffer, indent + 3, depth + 1);
      }
    }

    private void appendWhiteSpace(StringBuffer buffer, int howmany) {
      for (int i = 0; i < howmany; ++i) buffer.append(" ");
    }

    public String toString() {
      return name + ": " + average() + "ms (" + calls + " calls)";
    }

  }

  public void start(String operation) {
    start(operation, current.children.size());
  }


  private void start(String operation, int order) {
    ProfileNode node = current.getNode(operation, order);
    node.startedAt = System.currentTimeMillis();
    current = node;
  }

  public double getAverageTime(String operation) {
    ProfileNode node = root.getQualifiedNode(operation);
    return node.average();
  }

  public long getTotalTime(String operation) {
    ProfileNode node = root.getQualifiedNode(operation);
    return node.totalTime;
  }

  public double getCalls(String operation) {
    ProfileNode node = root.getQualifiedNode(operation);
    return node.calls;
  }

  public Profiler end() {
    current.totalTime += System.currentTimeMillis() - current.startedAt;
    ++current.calls;
    current = current.parent;
    return this;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    root.print(buffer, 0, 0);
    return buffer.toString();
  }

  @SuppressWarnings({"UnusedAssignment", "UnusedDeclaration"})
  public static void main(String[] args) {
    TreeProfiler profiler = new TreeProfiler();
    Op op = new Op();
    profiler.start("test");

    int result;
    int maxIterations = 200000000;
    profiler.start("static");
    for (int i = 0; i < maxIterations; ++i)
      result = op(i, i);
    profiler.end();
    profiler.start("inline");
    for (int i = 0; i < maxIterations; ++i)
      result = i + i;
    profiler.end();
    profiler.start("virtual");
    for (int i = 0; i < maxIterations; ++i)
      result = op.add(i, i);
    profiler.end();


    profiler.end();
    System.out.println(profiler);

  }

  private static class Op {
    int add(int arg1, int arg2) {
      return arg1 + arg2;
    }
  }

  private static int op(int arg1, int arg2) {
    return arg1 + arg2;
  }

  public static Profiler createProfiler(String name){
    if (name.equals("tree")) return new TreeProfiler();
    if (name.equals("null")) return new NullProfiler();
    if (name.equals("live")) return new LiveProfiler(System.out);
    return new NullProfiler();
  }

  

}
