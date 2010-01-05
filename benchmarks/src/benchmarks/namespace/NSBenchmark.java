package benchmarks.namespace;

public interface NSBenchmark {
    
    public static final int BIGFILE_SIZE = 100 * 1024 * 1024;   // bytes
    public static final int DUMMY_DATA_SIZE = 1024 * 8;         // bytes
    public static final int DIR_COUNT = 10;       // <= 1000
    public static final int SUBDIR_COUNT = 10;    // per directory, <= 1000
    public static final int FILE_COUNT = 10;      // per subdirectory, <= 1000

}
