package thebeast.util;

import java.util.*;

/**
 * Hierachical Profiler.
 *
 * @author Sebastian Riedel
 */
public class TreeProfiler implements Profiler {

  private ProfileNode root = new ProfileNode("root", null);
  private ProfileNode current = root;

  private class ProfileNode implements Comparable<ProfileNode> {
    int count;
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
      return (double) totalTime / (double) count;
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

    public void print(StringBuffer buffer, int indent) {
      appendWhiteSpace(buffer,indent);
      if (parent == null) {
        buffer.append(name).append("\n");
      } else {
        Formatter formatter = new Formatter();
        formatter.format("%-10s %-3.2fms (%d calls)\n", name, average(), count);
        buffer.append(formatter.toString());
      }
      for (ProfileNode node : getSortedChildren()) {
        node.print(buffer, indent + 3);
      }
    }

    private void appendWhiteSpace(StringBuffer buffer, int howmany){
      for (int i = 0; i < howmany; ++i) buffer.append(" ");
    }

    public String toString() {
      return name + ": " + average() + "ms (" + count + " calls)";
    }

  }

  public void start(String operation) {
    start(operation, current.children.size());
  }

  public void start(String operation, int order) {
    ProfileNode node = current.getNode(operation, order);
    node.startedAt = System.currentTimeMillis();
    current = node;
  }

  public void end() {
    current.totalTime += System.currentTimeMillis() - current.startedAt;
    ++current.count;
    current = current.parent;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    root.print(buffer, 0);
    return buffer.toString();
  }

  @SuppressWarnings({"UnusedAssignment", "UnusedDeclaration"})
  public static void main(String[] args) {
    TreeProfiler profiler = new TreeProfiler();
    profiler.start("test");
    for (int i = 0; i < 10000; ++i) {
      profiler.start("inner");
      profiler.start("post", 0);
      int counter = 0;
      for (int j = 0; j < 400; ++j)
        counter++;
      profiler.end();
      profiler.start("pre", 1);
      for (int j = 0; j < 400; ++j)
        ++counter; 
      profiler.end();
      profiler.start("array", 1);
      for (int j = 0; j < 400; ++j){
        int array[] = new int[20];
      }
      profiler.end();
      if (i % 2 == 0) {
        profiler.start("add", 2);
        counter += 1;
        profiler.end();
      }
      profiler.end();
    }
    profiler.end();
    System.out.println(profiler);

  }


}
