package test.stream;

import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamFactory;
import org.ogf.saga.stream.StreamService;

public class StreamServer {

	public static void main(String[] args) {

//		Logger lger = Logger.getLogger(SAGAEngine.class);
//		lger.setLevel(Level.DEBUG);

		System.setProperty("log4j.configuration", "log4j.properties");
		
		System.setProperty("saga.factory",
				"org.ogf.saga.impl.bootstrap.MetaFactory");
		System.setProperty("saga.adaptor.path",
				"../SagaAdaptors/lib");
		System.setProperty("gat.adaptor.path",
				"../SagaAdaptors/external/JavaGAT/adaptors");
		
		try {
			StreamService service = StreamFactory.createStreamService(new URL("advert://server"));

			for (int i = 0; i < 20; i++) {
				Stream stream = service.serve();
				Buffer buffer = org.ogf.saga.impl.buffer.BufferFactory
						.createBuffer();
				buffer.setData(new byte[100]);
				int bytesCnt = stream.read(buffer, buffer.getSize());
				System.out.println("Read " + bytesCnt + " bytes");
				System.out.println("Message content:");
				System.out.println(new String(buffer.getData()).trim());
				stream.close();
			}
			service.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} 

	}

}
