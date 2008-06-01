package thebeast.util;

/**
 * Contiguous multidimensional array implementations designed to be fast. For best performance
 * access the data field directly and access sequentially.
 *
 * @author Sebastian Riedel
 */
public class FastArrays {


    public static class Int1D {

        /**
         * data[i = array(i)
         */
        public final int[] data;

        public Int1D(int dim1) {
            data = new int[dim1];
        }

        public int get(int i) {
            return data[i];
        }

        public void set(int i, int value) {
            data[i] = value;
        }

    }

    public static class Int2D {
        public final int off1;

        public final int[] data;

        public Int2D(int dim1, int dim2) {
            this.off1 = dim1;
            data = new int[dim1 * dim2];
        }

        public int get(int i, int j) {
            return data[i * off1 + j];
        }

        public void set(int i, int j, int value) {
            data[i * off1 + j] = value;
        }

    }

    public static class Int3D {
        public final int off1;
        public final int off2;

        /**
         * data[i * off1 + j * off1 + k] = array(i,j,k)
         */
        public final int[] data;

        public Int3D(int dim1, int dim2, int dim3) {
            this.off1 = dim1 * dim2;
            this.off2 = dim2;
            data = new int[dim1 * dim2 * dim3];
        }

        public int get(int i, int j, int k) {
            return data[i * off1 + j * off2 + k];
        }

        public void set(int i, int j, int k, int value) {
            data[i * off1 + j * off2 + k] = value;
        }
    }

    public static class Int4D {
        public final int off1;
        public final int off2;
        public final int off3;

        /**
         * data[i * off1 + j * off1 + k * off3 + l] = array(i,j,k,l)
         */
        public final int[] data;

        public Int4D(int dim1, int dim2, int dim3, int dim4) {
            this.off1 = dim1 * dim2 * dim3;
            this.off2 = dim2 * dim3;
            this.off3 = dim3;
            data = new int[dim1 * dim2 * dim3 * dim4];
        }

        public int get(int i, int j, int k, int l) {
            return data[i * off1 + j * off2 + k * off3 + l];
        }

        public void set(int i, int j, int k, int l, int value) {
            data[i * off1 + j * off2 + k * off3 + l] = value;
        }
    }

    public static class Int5D {
        public final int off1;
        public final int off2;
        public final int off3;
        public final int off4;

        /**
         * data[i * off1 + j * off1 + k * off3 + l * off4 + m] = array(i,j,k,l,m)
         */
        public final int[] data;

        public Int5D(int dim1, int dim2, int dim3, int dim4, int dim5) {
            this.off1 = dim1 * dim2 * dim3 * dim4;
            this.off2 = dim2 * dim3 * dim4;
            this.off3 = dim3 * dim4;
            this.off4 = dim4;
            data = new int[dim1 * dim2 * dim3 * dim4 * dim5];
        }

        public int get(int i, int j, int k, int l, int m) {
            return data[i * off1 + j * off2 + k * off3 + l * off4 + m];
        }

        public void set(int i, int j, int k, int l, int m, int value) {
            data[i * off1 + j * off2 + k * off3 + l * off4 + m] = value;
        }
    }

    public static class Boolean1D {

        /**
         * data[i] = array(i)
         */
        public final boolean[] data;

        public Boolean1D(int dim1) {
            data = new boolean[dim1];
        }

        public boolean get(int i) {
            return data[i];
        }

        public void set(int i, boolean value) {
            data[i] = value;
        }

    }


    public static class Boolean2D {
        public final int off1;

        /**
         * data[i * off1 + j] = array(i,j)
         */
        public final boolean[] data;

        public Boolean2D(int dim1, int dim2) {
            this.off1 = dim1;
            data = new boolean[dim1 * dim2];
        }

        public boolean get(int i, int j) {
            return data[i * off1 + j];
        }

        public void set(int i, int j, boolean value) {
            data[i * off1 + j] = value;
        }

    }

    public static class Boolean3D {
        public final int off1;
        public final int off2;

        /**
         * data[i * off1 + j * off1 + k] = array(i,j,k)
         */
        public final boolean[] data;

        public Boolean3D(int dim1, int dim2, int dim3) {
            this.off1 = dim1 * dim2;
            this.off2 = dim2;
            data = new boolean[dim1 * dim2 * dim3];
        }

        public boolean get(int i, int j, int k) {
            return data[i * off1 + j * off2 + k];
        }

        public void set(int i, int j, int k, boolean value) {
            data[i * off1 + j * off2 + k] = value;
        }
    }

    public static class Boolean4D {
        public final int off1;
        public final int off2;
        public final int off3;

        /**
         * data[i * off1 + j * off1 + k * off3 + l] = array(i,j,k,l)
         */
        public final boolean[] data;

        public Boolean4D(int dim1, int dim2, int dim3, int dim4) {
            this.off1 = dim1 * dim2 * dim3;
            this.off2 = dim2 * dim3;
            this.off3 = dim3;
            data = new boolean[dim1 * dim2 * dim3 * dim4];
        }

        public boolean get(int i, int j, int k, int l) {
            return data[i * off1 + j * off2 + k * off3 + l];
        }

        public void set(int i, int j, int k, int l, boolean value) {
            data[i * off1 + j * off2 + k * off3 + l] = value;
        }
    }

    public static class Boolean5D {
        public final int off1;
        public final int off2;
        public final int off3;
        public final int off4;

        /**
         * data[i * off1 + j * off1 + k * off3 + l * off4 + m] = array(i,j,k,l,m)
         */
        public final boolean[] data;

        public Boolean5D(int dim1, int dim2, int dim3, int dim4, int dim5) {
            this.off1 = dim1 * dim2 * dim3 * dim4;
            this.off2 = dim2 * dim3 * dim4;
            this.off3 = dim3 * dim4;
            this.off4 = dim4;
            data = new boolean[dim1 * dim2 * dim3 * dim4 * dim5];
        }

        public boolean get(int i, int j, int k, int l, int m) {
            return data[i * off1 + j * off2 + k * off3 + l * off4 + m];
        }

        public void set(int i, int j, int k, int l, int m, boolean value) {
            data[i * off1 + j * off2 + k * off3 + l * off4 + m] = value;
        }
    }

    public static class Double1D {

        /**
         * data[i] = array(i)
         */
        public final double[] data;

        public Double1D(int dim1) {
            data = new double[dim1];
        }

        public double get(int i) {
            return data[i];
        }

        public void set(int i, double value) {
            data[i] = value;
        }

    }

    public static class Double2D {
        public final int off1;

        /**
         * data[i * off1 + j] = array(i,j)
         */
        public final double[] data;

        public Double2D(int dim1, int dim2) {
            this.off1 = dim1;
            data = new double[dim1 * dim2];
        }

        public double get(int i, int j) {
            return data[i * off1 + j];
        }

        public void set(int i, int j, double value) {
            data[i * off1 + j] = value;
        }

    }

    public static class Double3D {
        public final int off1;
        public final int off2;

        /**
         * data[i * off1 + j * off1 + k] = array(i,j,k)
         */
        public final double[] data;

        public Double3D(int dim1, int dim2, int dim3) {
            this.off1 = dim1 * dim2;
            this.off2 = dim2;
            data = new double[dim1 * dim2 * dim3];
        }

        public double get(int i, int j, int k) {
            return data[i * off1 + j * off2 + k];
        }

        public void set(int i, int j, int k, double value) {
            data[i * off1 + j * off2 + k] = value;
        }
    }

    public static class Double4D {
        public final int off1;
        public final int off2;
        public final int off3;

        /**
         * data[i * off1 + j * off1 + k * off3 + l] = array(i,j,k,l)
         */
        public final double[] data;

        public Double4D(int dim1, int dim2, int dim3, int dim4) {
            this.off1 = dim1 * dim2 * dim3;
            this.off2 = dim2 * dim3;
            this.off3 = dim3;
            data = new double[dim1 * dim2 * dim3 * dim4];
        }

        public double get(int i, int j, int k, int l) {
            return data[i * off1 + j * off2 + k * off3 + l];
        }

        public void set(int i, int j, int k, int l, double value) {
            data[i * off1 + j * off2 + k * off3 + l] = value;
        }
    }

    public static class Double5D {
        public final int off1;
        public final int off2;
        public final int off3;
        public final int off4;

        /**
         * data[i * off1 + j * off1 + k * off3 + l * off4 + m] = array(i,j,k,l,m)
         */
        public final double[] data;

        public Double5D(int dim1, int dim2, int dim3, int dim4, int dim5) {
            this.off1 = dim1 * dim2 * dim3 * dim4;
            this.off2 = dim2 * dim3 * dim4;
            this.off3 = dim3 * dim4;
            this.off4 = dim4;
            data = new double[dim1 * dim2 * dim3 * dim4 * dim5];
        }

        public double get(int i, int j, int k, int l, int m) {
            return data[i * off1 + j * off2 + k * off3 + l * off4 + m];
        }

        public void set(int i, int j, int k, int l, int m, double value) {
            data[i * off1 + j * off2 + k * off3 + l * off4 + m] = value;
        }
    }


}
