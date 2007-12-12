package test.stream;

import org.ogf.saga.URL;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamFactory;
import org.ogf.saga.stream.StreamService;

public class StreamTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            
            Stream s = StreamFactory.createStream(new URL("bla"));
            StreamService ss = StreamFactory.createStreamService();
            ss = (StreamService) ss.clone();
            s = (Stream) s.clone();
        } catch (Throwable t) {
            System.out.println("ouch..." + t);
            t.printStackTrace();
        }
    } 
}
