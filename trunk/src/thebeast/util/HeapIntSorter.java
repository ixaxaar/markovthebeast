package thebeast.util;

/**
 * @author Sebastian Riedel
 */
public class HeapIntSorter {
  private int[] a;
  private int n;
  private int[] indices;

  public void sort(int[] values) {
    a = values;
    n = a.length;
    heapsort();
    indices = null;
  }

  public void sort(int[] values, int[] indices) {
    a = values;
    n = a.length;
    this.indices = indices;
    heapsort();
  }


  private void heapsort() {
    buildheap();
    while (n > 1) {
      n--;
      exchange(0, n);
      downheap(0);
    }
  }


  private void buildheap() {
    for (int v = n / 2 - 1; v >= 0; v--)
      downheap(v);
  }


  private void downheap(int v) {
    int w = 2 * v + 1;    // first descendant of v
    while (w < n) {
      if (w + 1 < n)    // is there a second descendant?
        if (a[w + 1] < a[w]) w++;
      // w is the descendant of v with maximum label

      if (a[v] <= a[w]) return;  // v has heap property
      // otherwise
      exchange(v, w);  // exchange labels of v and w
      v = w;        // continue
      w = 2 * v + 1;
    }
  }


  private void exchange(int i, int j) {
    int t = a[i];
    a[i] = a[j];
    a[j] = t;
    if (indices != null) {
      int t_i = indices[i];
      indices[i] = indices[j];
      indices[j] = t_i;
    }
  }

}    // end class HeapSorter
