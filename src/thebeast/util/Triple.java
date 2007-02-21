package thebeast.util;

/**
 * Created by IntelliJ IDEA.
 * User: srriedel
 * Date: Sep 21, 2006
 * Time: 11:56:32 PM
 */
public class Triple<A1, A2, A3> {
    public final A1 arg1;
    public final A2 arg2;
    public final A3 arg3;


    public Triple(A1 arg1, A2 arg2, A3 arg3) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple triple = (Triple) o;

        if (arg1 != null ? !arg1.equals(triple.arg1) : triple.arg1 != null) return false;
        if (arg2 != null ? !arg2.equals(triple.arg2) : triple.arg2 != null) return false;
        if (arg3 != null ? !arg3.equals(triple.arg3) : triple.arg3 != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (arg1 != null ? arg1.hashCode() : 0);
        result = 31 * result + (arg2 != null ? arg2.hashCode() : 0);
        result = 31 * result + (arg3 != null ? arg3.hashCode() : 0);
        return result;
    }
}
