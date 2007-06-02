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

    public Cluster(int index, String... bibs) {
      this.index = index;
      for (String bib : bibs) this.bibs.add(bib);
    }

    public int compareTo(Object o) {
      return -((Cluster) o).index + index;
    }

    public void addAllBibs(Collection<String> dst) {
      dst.addAll(bibs);
      for (Cluster child : children)
        child.addAllBibs(dst);
    }
  }

  public static void main(String[] args) throws IOException {
    int folds = Integer.parseInt(args[0]);
    String prefix = args[1];
    //get record clusters

    HashMap<String, String> bib2title = new HashMap<String, String>();
    HashMap<String, String> bib2author = new HashMap<String, String>();
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
          bib2cluster.put(arg2, cluster1);
        } else if (cluster2 != null) {
          cluster2.bibs.add(arg1);

          bib2cluster.put(arg1, cluster2);
        } else {
          Cluster cluster = new Cluster(index++, arg1, arg2);
          bib2cluster.put(arg1, cluster);
          bib2cluster.put(arg2, cluster);
        }
      } else if (pred.equals("Author")) {
        bib2author.put(arg1, arg2);
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

    ArrayList<Cluster> area = new ArrayList<Cluster>(areaSize + 10);


    ArrayList<ArrayList<String>> textFolds = new ArrayList<ArrayList<String>>(folds);
    for (Cluster cluster : clusters) {
      area.add(cluster);
      System.out.println(cluster.index);
      if (area.size() == areaSize) {
        HashSet<String> buffer = new HashSet<String>();
        LinkedList<String> members = new LinkedList<String>();
        for (Cluster inArea : area) {
          inArea.addAllBibs(members);
          LinkedList<String> debug= new LinkedList<String>();
          //inArea.addAllBibs(debug);
          //System.out.println(debug);
        }
        for (String bib1 : members) {
          String title1 = bib2title.get(bib1);
          String author1 = bib2author.get(bib1);
          String venue1 = bib2venue.get(bib1);
          buffer.add("Title(" + bib1 + "," + title1 + ")");
          buffer.add("Author(" + bib1 + "," + author1 + ")");
          buffer.add("Venue(" + bib1 + "," + venue1 + ")");
          for (String bib2 : members) {
            String title2 = bib2title.get(bib2);
            String author2 = bib2author.get(bib2);
            String venue2 = bib2venue.get(bib2);
            for (String line : pairs2lines.get(new Pair<String, String>(bib1, bib2)))
              buffer.add(line);
            for (String line : pairs2lines.get(new Pair<String, String>(title1, title2)))
              buffer.add(line);
            for (String line : pairs2lines.get(new Pair<String, String>(venue1, venue2)))
              buffer.add(line);
            for (String line : pairs2lines.get(new Pair<String, String>(author1, author2)))
              buffer.add(line);
          }
        }
        ArrayList<String> sorted = new ArrayList<String>(buffer);
        Collections.sort(sorted);
        textFolds.add(sorted);
        area.clear();
      }
    }

    System.out.println("Writing folds...");

    int foldNr = 0;
    for (ArrayList<String> fold : textFolds) {
      PrintStream out = new PrintStream(prefix + foldNr + "of" + folds + ".fold");
      for (String line : fold)
        out.println(line);
      out.close();
      out = new PrintStream(prefix + foldNr + "of" + folds + ".rest");
      HashSet<String> buffer = new HashSet<String>();
      for (int i = 0; i < textFolds.size(); ++i) {
        if (i != foldNr)
          for (String line : textFolds.get(i))
            buffer.add(line);
      }
      ArrayList<String> sorted = new ArrayList<String>(buffer);
      Collections.sort(sorted);
      for (String line : sorted)
        out.println(line);
      out.close();
      ++foldNr;
    }


  }

}
