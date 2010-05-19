package test.misc;

import java.util.ArrayList;

public class AdaptorTestResult {
    private String adaptor;

    private String host;
    
    private ArrayList<String> keys = new ArrayList<String>();
    
    private ArrayList<AdaptorTestResultEntry> results = new ArrayList<AdaptorTestResultEntry>();

    public AdaptorTestResult(String adaptor, String host) {
        this.adaptor = adaptor;
        this.host = host;

    }

    public void put(String key, AdaptorTestResultEntry testResultEntry) {
        keys.add(key);
        results.add(testResultEntry);
    }

    public void print() {
        System.out.println("*** general results ***");
        System.out.println("adaptor:    " + adaptor);
        System.out.println("host:       " + host);
        System.out.println("total time: " + getTotalRunTime() + " msec");
        System.out.println("avg time  : " + getAverageRunTime() + " msec");
        System.out.println("*** method results  ***");
        
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            AdaptorTestResultEntry entry = results.get(i);
            Throwable t = entry.getThrowable();
            System.out.print(key);
            if (entry.getResult()) {
                System.out.print("\t SUCCESS \t" + entry.getTime() + " msec");
                if (t != null) {
                    System.out.println("\t" + t);
                } else {
                    System.out.println();
                }
            } else {
                System.out.println("\t FAILURE \t" + t);
                if (t != null) {
                    t.printStackTrace();
                }
            }
        }
    }

    public long getTotalRunTime() {
        long result = 0L;
        for (AdaptorTestResultEntry testResultEntry : results) {
            if (testResultEntry.getResult()) {
                result += testResultEntry.getTime();
            }
        }
        return result;
    }

    public long getAverageRunTime() {
        long result = 0L;
        int i = 0;
        for (AdaptorTestResultEntry testResultEntry : results) {
            if (testResultEntry.getResult()) {
                result += testResultEntry.getTime();
                i++;
            }
        }
        if (i == 0) {
            return 0L;
        }
        return result / i;
    }

    public String getAdaptor() {
        return adaptor;
    }

}
