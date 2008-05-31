package thebeast.util;

import java.util.HashMap;
import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class StopWatch {

    private long last;
    private long stopped;
    private HashMap<String, Long> times = new HashMap<String, Long>();
    private String name;

    public void start(){
        last = System.currentTimeMillis();
    }


    public StopWatch(String name) {
        this.name = name;
    }


    public StopWatch() {
        this("No name");
    }

    public long stopAndContinue(){
        long current = System.currentTimeMillis();
        stopped = current - last;
        last = current;
        return stopped;
    }

    public void stopAs(String name){
        times.put(name,stopAndContinue());
    }

    public void stopAddAs(String name){
        Long old = times.get(name);
        times.put(name, old == null ? stopAndContinue() : old + stopAndContinue());
    }

    public long getTime(String name){
        return times.get(name);
    }

    public void printTimes(PrintStream out){
        out.println(name);
        for (int i = 0; i < 28; ++i) out.print("=");
        out.println();
        for (String name : times.keySet()){
            out.printf("%-20s%6dms\n",name,times.get(name));
        }
    }

    public void clearTimes(){
        times.clear();
    }

}
