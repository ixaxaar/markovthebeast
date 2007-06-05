package thebeast.pml.corpora;

import thebeast.util.HashMultiMap;
import thebeast.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class CoraNFold {

  static class Cluster implements Comparable {
    HashSet<String> bibs = new HashSet<String>();
    LinkedList<Cluster> children = new LinkedList<Cluster>();
    final int index;
    int count;

    public Cluster(int index, String... bibs) {
      this.index = index;
      for (String bib : bibs) this.bibs.add(bib);
      count = bibs.length;
    }

    public int compareTo(Object o) {
      return -((Cluster) o).count - count;
      //return -((Cluster) o).index + index;
    }

    public void addAllBibs(Collection<String> dst) {
      dst.addAll(bibs);
      for (Cluster child : children)
        child.addAllBibs(dst);
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 3){
      System.out.println("usage: <folds> <outputprefix> <inputfile>");
      System.exit(0);
    }

    int folds = Integer.parseInt(args[0]);
    String prefix = args[1];
    //get record clusters

    HashMap<String, String> bib2title = new HashMap<String, String>();
    HashMultiMap<String, String> bib2author = new HashMultiMap<String, String>();
    HashMap<String, String> bib2venue = new HashMap<String, String>();

    HashMap<String, Cluster> bib2cluster = new HashMap<String, Cluster>();

    HashMultiMap<Pair<String, String>, String> pairs2lines = new HashMultiMap<Pair<String, String>, String>();

    BufferedReader reader = new BufferedReader(new FileReader(args[2]));
    System.out.println("Collecting clusters");
    HashSet<String> bibs = new HashSet<String>();
    HashSet<String> clusterSet = new HashSet<String>();
    int index = 0;
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.equals("")) continue;
      //System.out.print("");
      //System.out.println(line);
      String[] split = line.split("[(,)]");
      //System.out.println(Arrays.toString(split));
      String pred = split[0].trim();
      String arg1 = split[1].trim();
      String arg2 = split[2].trim();
      if (pred.equals("SameBib")) {
        bibs.add(arg1);
        bibs.add(arg2);
        Cluster cluster1 = bib2cluster.get(arg1);
        Cluster cluster2 = bib2cluster.get(arg2);
        if (cluster1 != null && cluster2 != null) {
          if (cluster1 != cluster2) {
            cluster1.children.add(cluster2);
            bib2cluster.put(arg2, cluster1);
          }
        } else if (cluster1 != null) {
          cluster1.bibs.add(arg2);
          cluster1.count++;
          bib2cluster.put(arg2, cluster1);
        } else if (cluster2 != null) {
          cluster2.bibs.add(arg1);
          cluster2.count++;
          bib2cluster.put(arg1, cluster2);
        } else {
          Cluster cluster = new Cluster(index++, arg1, arg2);
          bib2cluster.put(arg1, cluster);
          bib2cluster.put(arg2, cluster);
        }
        Pair<String, String> pair = new Pair<String, String>(arg1, arg2);
        pairs2lines.add(pair, line);
      } else if (pred.equals("Author")) {
        bib2author.add(arg1, arg2);
      } else if (pred.equals("Venue")) {
        bib2venue.put(arg1, arg2);
      } else if (pred.equals("Title")) {
        bib2title.put(arg1, arg2);
      } else {
        Pair<String, String> pair = new Pair<String, String>(arg1, arg2);
        pairs2lines.add(pair, line);
      }
    }

    

    ArrayList<Cluster> clusters = new ArrayList<Cluster>(new HashSet<Cluster>(bib2cluster.values()));
    Collections.sort(clusters);

    System.out.printf("%-20s %-6d\n", "# Clusters:", clusters.size());
    System.out.printf("%-20s %-6d\n", "# Records:", bibs.size());

    int areaSize = clusters.size() / folds;

    int recordCount = bib2author.keySet().size();
    int recordsPerFold = recordCount / folds;

    LinkedList<String> members = new LinkedList<String>();
    for (Cluster cluster : clusters) {
      cluster.addAllBibs(members);
    }
    //create folds
    HashSet<String> all = new HashSet<String>(members);
    HashSet<String> fold = new HashSet<String>();
    int foldNr = 0;
    for (String member: members){
      fold.add(member);
      if (fold.size() == recordsPerFold){
        System.out.println("Fold with " + fold.size() + " records");
        HashSet<String> rest = new HashSet<String>(all);
        rest.removeAll(fold);

        HashSet<String> restBuffer = buildFold(rest, bib2title, bib2venue, bib2author, pairs2lines);
        ArrayList<String> sorted = new ArrayList<String>(restBuffer);
        Collections.sort(sorted);

        PrintStream out = new PrintStream(prefix + "rest-"+ foldNr + ".db");
        for (String line : sorted) out.println(line);
        out.close();

        HashSet<String> foldBuffer = buildFold(fold, bib2title, bib2venue, bib2author, pairs2lines);
        sorted = new ArrayList<String>(foldBuffer);
        Collections.sort(sorted);

        out = new PrintStream(prefix + "fold-"+ foldNr + ".db");
        for (String line : sorted) out.println(line);
        out.close();


        fold.clear();
        ++foldNr;
      }
    }

  }

  private static HashSet<String> buildFold(HashSet<String> fold, HashMap<String, String> bib2title, HashMap<String, String> bib2venue, HashMultiMap<String, String> bib2author, HashMultiMap<Pair<String, String>, String> pairs2lines) {
    HashSet<String> buffer = new HashSet<String>();
    for (String bib1 : fold) {
      String title1 = bib2title.get(bib1);
      String venue1 = bib2venue.get(bib1);
      buffer.add("Title(" + bib1 + "," + title1 + ")");
      buffer.add("Venue(" + bib1 + "," + venue1 + ")");
      for (String author1 : bib2author.get(bib1))
        buffer.add("Author(" + bib1 + "," + author1 + ")");
      for (String bib2 : fold) {
        String title2 = bib2title.get(bib2);
        String venue2 = bib2venue.get(bib2);
        for (String line : pairs2lines.get(new Pair<String, String>(bib1, bib2)))
          buffer.add(line);
        for (String line : pairs2lines.get(new Pair<String, String>(title1, title2)))
          buffer.add(line);
        for (String line : pairs2lines.get(new Pair<String, String>(venue1, venue2)))
          buffer.add(line);
        for (String author1 : bib2author.get(bib1))
          for (String author2 : bib2author.get(bib2))
            for (String line : pairs2lines.get(new Pair<String, String>(author1, author2)))
              buffer.add(line);
      }
    }
    return buffer;
  }

}
