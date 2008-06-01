package thebeast.util;

import java.util.HashMap;
import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class MemoryMonitor {

    private HashMap<String,Long> deltas = new HashMap<String, Long>();
    private long lastFree;
    private long lastTotal;
    private String name;


    public MemoryMonitor(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void start() {
        lastFree = Runtime.getRuntime().freeMemory();
        lastTotal = Runtime.getRuntime().freeMemory();
    }

    public long stopAndContinue(){
        long currentFree = Runtime.getRuntime().freeMemory();
        long currentTotal = Runtime.getRuntime().totalMemory();
        long result = (currentTotal - currentFree) - (lastTotal - lastFree);
        lastFree = currentFree;
        lastTotal = currentTotal;
        return result;
    }

    public void setDelta(String name){
        deltas.put(name,stopAndContinue());
    }

    public long getDelta(String name){
        return deltas.get(name);
    }

    public void printDeltas(PrintStream out){
        for (String name : deltas.keySet()){
            out.printf("%-20s%8dk\n",name,deltas.get(name)/1024);
        }
    }

}
