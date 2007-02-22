package thebeast.util;

/**
 * @author Sebastian Riedel
 */
public class CycleFinder {

  public static int[][] findCycleVertices(int[][] edges, int vertexCount) {
    boolean[][] connected = new boolean[vertexCount][vertexCount];
    int[][] incoming = new int[vertexCount][vertexCount];
    int[] incomingCount = new int[vertexCount];
    int[][] nextVertices = new int[vertexCount][vertexCount];
    int[] nextVerticesCount = new int[vertexCount];
    int[][] outgoing = new int[vertexCount][vertexCount];
    int[] outgoingCount = new int[vertexCount];
    for (int[] edge : edges) {
      int from = edge[0];
      int to = edge[1];
      connected[from][to] = true;
      boolean contained = false;
      for (int i = 0; i < incomingCount[to] && !contained; ++i) contained = incoming[to][i] == from;
      if (!contained) incoming[to][incomingCount[to]++] = from;
      contained = false;
      for (int i = 0; i < outgoingCount[from] && !contained; ++i) contained = outgoing[from][i] == to;
      if (!contained) outgoing[from][outgoingCount[from]++] = to;
      contained = false;
      for (int i = 0; i < nextVerticesCount[from] && !contained; ++i) contained = nextVertices[from][i] == to;
      if (!contained) nextVertices[from][nextVerticesCount[from]++] = to;
      //connect everything before "from" to "to"
      for (int i = 0; i < incomingCount[from]; ++i) {
        int src = incoming[from][i];
        if (!connected[src][to]) {
          incoming[to][incomingCount[to]++] = src;
          outgoing[src][outgoingCount[src]++] = to;
          connected[src][to] = true;
        }
      }
      //connect everything after "to" to "from"
      for (int j = 0; j < outgoingCount[to]; ++j) {
        int tgt = outgoing[to][j];
        if (!connected[from][tgt]) {
          incoming[tgt][incomingCount[tgt]++] = from;
          outgoing[from][outgoingCount[from]++] = tgt;
          connected[from][tgt] = true;
        }
      }
      //connect everything after "to" to everything before "from"
      for (int i = 0; i < incomingCount[from]; ++i) {
        int src = incoming[from][i];
        for (int j = 0; j < outgoingCount[to]; ++j) {
          int tgt = outgoing[to][j];
          connected[src][tgt] = true;
          if (!connected[src][tgt]) {
            incoming[tgt][incomingCount[tgt]++] = src;
            outgoing[src][outgoingCount[src]++] = tgt;
            connected[src][tgt] = true;
          }
        }
      }
    }

    int cycleCount = 0;
    boolean[] inCycle = new boolean[vertexCount];
    int[] cycleSize = new int[vertexCount];
    int[][] cycles = new int[vertexCount][vertexCount * 2];
    boolean[][] wentThere = new boolean[vertexCount][vertexCount];
    for (int v = 0; v < vertexCount; ++v)
      if (connected[v][v] && !inCycle[v]) {
        cycles[cycleCount][cycleSize[cycleCount]++] = v;
        int next = v;
        for (int i = 0; i < nextVerticesCount[v]; ++i) {
          next = nextVertices[v][i];
          if (connected[v][next] && connected[next][v] && !wentThere[v][next]) break;
        }
        inCycle[next] = true;
        wentThere[v][next] = true;
        while (next != v) {
          cycles[cycleCount][cycleSize[cycleCount]++] = next;
          int tgt = next;
          for (int i = 0; i < nextVerticesCount[next]; ++i) {
            tgt = nextVertices[next][i];
            if (connected[v][tgt] && connected[tgt][v] & !wentThere[next][tgt]) break;
          }
          wentThere[next][tgt] = true;
          next = tgt;
          inCycle[next] = true;
        }
        ++cycleCount;
      }
    int[][] result = new int[cycleCount][];
    for (int i = 0; i < cycleCount; ++i) {
      result[i] = new int[cycleSize[i]];
      System.arraycopy(cycles[i], 0, result[i], 0, cycleSize[i]);
    }
    return result;
  }


  private static int min
          (
                  int arg1,
                  int arg2) {
    return arg1;
    //return arg1 < arg2 ? arg1 : arg2;
  }
}
