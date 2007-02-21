package thebeast.util;

/**
 * @author Sebastian Riedel
 */
public class CycleFinder {

  public static int[][] findCycleVertices(int[][] edges, int vertexCount) {
    boolean[][] connected = new boolean[vertexCount][vertexCount];
    int[][] distances = new int[vertexCount][vertexCount];
    int[][] incoming = new int[vertexCount][vertexCount];
    int[] incomingCount = new int[vertexCount];
    int[][] outgoing = new int[vertexCount][vertexCount];
    int[] outgoingCount = new int[vertexCount];
    for (int[] edge : edges) {
      int from = edge[0];
      int to = edge[1];
      connected[from][to] = true;
      distances[from][to] = 1;
      boolean contained = false;
      for (int i =0; i < incomingCount[to] &&!contained; ++i) contained = incoming[to][i] == from;
      if (!contained)incoming[to][incomingCount[to]++] = from;
      contained = false;
      for (int i =0; i < outgoingCount[to] &&!contained; ++i) contained = outgoing[from][i] == to;
      if (!contained) outgoing[from][outgoingCount[from]++] = to;
      //connect everything before "from" to "to"
      for (int i = 0; i < incomingCount[from]; ++i) {
        int src = incoming[from][i];
        if (src == to){
          connected[to][to] = true;
          distances[to][to] = 0;
        }
        else if (!connected[src][to]) {
          incoming[to][incomingCount[to]++] = src;
          outgoing[src][outgoingCount[src]++] = to;
          connected[src][to] = true;
          distances[src][to] = distances[src][from] + 1;
        }
      }
      //connect everything after "to" to "from"
      for (int j = 0; j < outgoingCount[to]; ++j) {
        int tgt = outgoing[to][j];
        if (tgt == from){
          connected[from][from] = true;
          distances[from][from] = 0;
        }
        else if (!connected[from][tgt]) {
          incoming[tgt][incomingCount[tgt]++] = from;
          outgoing[from][outgoingCount[from]++] = tgt;
          connected[from][tgt] = true;
          distances[from][tgt] = distances[to][tgt] + 1;
        }
      }
      //connect everything after "to" to everything before "from"
      for (int i = 0; i < incomingCount[from]; ++i) {
        int src = incoming[from][i];
        for (int j = 0; j < outgoingCount[to]; ++j) {
          int tgt = outgoing[to][j];
          if (src == tgt){
            connected[src][src] = true;
            distances[src][src] = 0;
          }
          else if (!connected[src][tgt]) {
            incoming[tgt][incomingCount[tgt]++] = src;
            outgoing[src][outgoingCount[src]++] = tgt;
            connected[src][tgt] = true;
            distances[src][tgt] = distances[src][from] + 1 + distances[to][tgt];
          }
        }
      }
    }
    int cycleCount = 0;
    boolean[] inCycle = new boolean[vertexCount];
    boolean[] prototype = new boolean[vertexCount];
    int[] cycleSize = new int[vertexCount];
    int[][] cycles = new int[vertexCount][vertexCount * 2];
    for (int v = 0; v < vertexCount; ++v)
      if (connected[v][v] && !inCycle[v]) {
        cycles[v][cycleSize[v]++] = v;        
        for (int i = 0; i < incomingCount[v]; ++i) {
          int src = incoming[v][i];
          if (connected[v][src] && connected[src][v]) {
            inCycle[src] = true;
            cycles[v][cycleSize[v]++] = src;
          }
        }
        ++cycleCount;
        prototype[v] = true;
      }
    int[][] result = new int[cycleCount][];
    int index = 0;
    HeapDoubleSorter sorter = new HeapDoubleSorter();
    for (int v = 0; v < vertexCount; ++v) {
      if (prototype[v]) {
        result[index] = new int[cycleSize[v]];
        double[] position = new double[cycleSize[v]];
        for (int w = 0; w < cycleSize[v]; ++w) {
          int other = cycles[v][w];
          result[index][w] = other;
          position[w] = -distances[v][other];
        }
        sorter.sort(position,result[index]);
        //Utils.sortDescending(position, result[index]);
        //System.arraycopy(cycles[v],0,result[index],0,result[index].length);
        ++index;
      }

    }
    return result;
  }

}
