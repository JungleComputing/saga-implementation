package demo.rpc;

import org.ogf.saga.URL;
import org.ogf.saga.rpc.IOMode;
import org.ogf.saga.rpc.Parameter;
import org.ogf.saga.rpc.RPC;
import org.ogf.saga.rpc.RPCFactory;

public class CallAdder {
    public static void main(String[] args) {
        try {
            URL url = new URL("any://titan.cs.vu.nl:8080/Calculator.add");
            RPC rpc = RPCFactory.createRPC(url);
            Parameter param1 = RPCFactory.createParameter(new Integer(10));
            Parameter param2 = RPCFactory.createParameter(new Integer(20));
            Parameter result = RPCFactory.createParameter(IOMode.OUT);
            
            rpc.call(param1, param2, result);
            System.out.println("Result (should be 30) = " + result.getData());
        } catch (Throwable t) {
            System.out.println("ouch..." + t);
            t.printStackTrace();
        }
    }
}
