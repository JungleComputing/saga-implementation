package test.stream;

import org.ogf.saga.URL;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
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
        } catch (Throwable t) {
            System.out.println("ouch..." + t);
            t.printStackTrace();
        }
    } 
}
