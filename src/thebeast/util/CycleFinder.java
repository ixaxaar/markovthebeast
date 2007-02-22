package thebeast.util;

import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class CycleFinder {

  public static int[][] findCycleVertices(int[][] edges, int vertexCount) {
    boolean[][] connected = new boolean[vertexCount][vertexCount];
    int[][] distances = new int[vertexCount][vertexCount];
    for (int i = 0; i < vertexCount; ++i)
      Arrays.fill(distances[i], Integer.MAX_VALUE);
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
      distances[from][to] = 1;
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
        if (distances[src][to] == Integer.MAX_VALUE) {
          incoming[to][incomingCount[to]++] = src;
          outgoing[src][outgoingCount[src]++] = to;
        }
        distances[src][to] = min(distances[src][to], distances[src][from] + 1);
      }
      //connect everything after "to" to "from"
      for (int j = 0; j < outgoingCount[to]; ++j) {
        int tgt = outgoing[to][j];
        if (distances[from][tgt] == Integer.MAX_VALUE) {
          incoming[tgt][incomingCount[tgt]++] = from;
          outgoing[from][outgoingCount[from]++] = tgt;
        }
        distances[from][tgt] = min(distances[from][tgt], distances[to][tgt] + 1);
      }
      //connect everything after "to" to everything before "from"
      for (int i = 0; i < incomingCount[from]; ++i) {
        int src = incoming[from][i];
        for (int j = 0; j < outgoingCount[to]; ++j) {
          int tgt = outgoing[to][j];
          if (distances[src][tgt] == Integer.MAX_VALUE) {
            incoming[tgt][incomingCount[tgt]++] = src;
            outgoing[src][outgoingCount[src]++] = tgt;
          }
          distances[src][tgt] = min(distances[src][tgt], distances[src][from] + distances[to][tgt] + 1);
        }
      }
    }

    int cycleCount = 0;
    boolean[] inCycle = new boolean[vertexCount];
    int[] cycleSize = new int[vertexCount];
    int[][] cycles = new int[vertexCount][vertexCount];
    boolean[][] wentThere = new boolean[vertexCount][vertexCount];

    for (int start = 0; start < vertexCount; ++start) {
      if (distances[start][start] < Integer.MAX_VALUE && !inCycle[start]) {
        cycles[cycleCount][cycleSize[cycleCount]++] = start;
        int next = start;
        search:
        do {
          int min = Integer.MAX_VALUE;
          int best = -1;
          for (int i = 0; i < nextVerticesCount[next]; ++i) {
            int vertex = nextVertices[next][i];
            if (vertex == start) break search;
            if (distances[vertex][start] < min && !wentThere[next][vertex]) {
              min = distances[vertex][start];
              best = vertex;
            }
          }
          wentThere[next][best] = true;
          next = best;
          inCycle[next] = true;
          cycles[cycleCount][cycleSize[cycleCount]++] = next;
        } while (next != start);
        //clear wenttheres
        for (int v = 0; v < cycleSize[cycleCount] - 1; ++v)
          wentThere[cycles[cycleCount][v]][cycles[cycleCount][v + 1]] = false;
        wentThere[cycles[cycleCount][cycleSize[cycleCount] - 1]][cycles[cycleCount][0]] = false;
        ++cycleCount;
      }
    }
    int[][] result = new int[cycleCount][];
    for (int i = 0; i < cycleCount; ++i) {
      result[i] = new int[cycleSize[i]];
      System.arraycopy(cycles[i], 0, result[i], 0, cycleSize[i]);
    }
    return result;
  }


  private static int min(int arg1, int arg2) {
    return arg1 < arg2 ? arg1 : arg2;
  }
}
