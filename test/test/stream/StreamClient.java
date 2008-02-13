package test.stream;

import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamFactory;

public class StreamClient {

    public static void main(String[] args) {

        System.setProperty("saga.factory",
                "org.ogf.saga.impl.bootstrap.MetaFactory");
        System.setProperty("saga.adaptor.path", "lib");
        System.setProperty("gat.adaptor.path", "external/JavaGAT/adaptors");

        try {
            Stream stream = StreamFactory.createStream(new URL(
                    "advert://server"));
            stream.connect();

            Buffer buffer = org.ogf.saga.impl.buffer.BufferFactory
                    .createBuffer();
            buffer.setData("Hello World".getBytes());
            int bytesCnt = stream.write(buffer, buffer.getSize());
            System.out.println("Wrote " + bytesCnt + " bytes");
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
