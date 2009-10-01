package test.misc;

import java.util.HashMap;
import java.util.Map;

public class AdaptorTestResult {
    private String adaptor;

    private String host;

    private Map<String, AdaptorTestResultEntry> testResultEntries = new HashMap<String, AdaptorTestResultEntry>();

    public AdaptorTestResult(String adaptor, String host) {
        this.adaptor = adaptor;
        this.host = host;

    }

    public void put(String key, AdaptorTestResultEntry testResultEntry) {
        testResultEntries.put(key, testResultEntry);
    }

    public void print() {
        System.out.println("*** general results ***");
        System.out.println("adaptor:    " + adaptor);
        System.out.println("host:       " + host);
        System.out.println("total time: " + getTotalRunTime() + " msec");
        System.out.println("avg time  : " + getAverageRunTime() + " msec");
        System.out.println("*** method results  ***");

        for (String key : testResultEntries.keySet()) {
            AdaptorTestResultEntry entry = testResultEntries.get(key);
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
        for (AdaptorTestResultEntry testResultEntry : testResultEntries
                .values()) {
            if (testResultEntry.getResult()) {
                result += testResultEntry.getTime();
            }
        }
        return result;
    }

    public long getAverageRunTime() {
        long result = 0L;
        int i = 0;
        for (AdaptorTestResultEntry testResultEntry : testResultEntries
                .values()) {
            if (testResultEntry.getResult()) {
                result += testResultEntry.getTime();
                i++;
            }
        }
        if (i == 0) {
            return 0L;
        } else {
            return result / i;
        }
    }

    public String getAdaptor() {
        return adaptor;
    }

    public Map<String, AdaptorTestResultEntry> getTestResultEntries() {
        return testResultEntries;
    }

}
