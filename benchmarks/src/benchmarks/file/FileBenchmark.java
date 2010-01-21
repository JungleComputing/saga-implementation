package benchmarks.file;

public interface FileBenchmark {
    
    /**
     * Size of the big file to create (in bytes)
     */
    public static final int BIG_FILE_SIZE = 100 * 1024 * 1024;  // bytes
    
    /**
     * Size of the data buffer used for writing the big file
     */
    public static final int WRITE_BUF_SIZE = 1024 * 32;          // bytes
    
    /**
     * Size of the data buffer used for copying the big file (if applicable)
     */
    public static final int COPY_BUF_SIZE = 1024 * 32;           // bytes

    /**
     * Size of the data buffer used for reading the big file
     */
    public static final int READ_BUF_SIZE = 1024 * 32;           // bytes

}
