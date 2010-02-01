package benchmarks;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trilead.ssh2.Connection;

public class Connecter {

    private static Logger logger = LoggerFactory.getLogger(Connecter.class);
    
    private static final String[] client2server = {
        "aes256-ctr",
        "aes192-ctr",
        "aes128-ctr",
        "blowfish-ctr",
        "aes256-cbc",
        "aes192-cbc",
        "aes128-cbc",
        "blowfish-cbc"
    };
    
    private static final String[] server2client = {
        "aes256-ctr",
        "aes192-ctr",
        "aes128-ctr",
        "blowfish-ctr",
        "aes256-cbc",
        "aes192-cbc",
        "aes128-cbc",
        "blowfish-cbc"
    };
    
    public static Connection getConnection(String host, int port,
            HostKeyVerifier verifier, boolean nodelay) throws Exception {

        logger.info("getting connection for host: " + host);

        Connection newConnection = new Connection(host, port);
        newConnection.setClient2ServerCiphers(client2server);
        newConnection.setServer2ClientCiphers(server2client);
        newConnection.setTCPNoDelay(nodelay);

        newConnection.connect(verifier, 0, 0);

        String username = System.getProperty("user.name");
        java.io.File keyFile = getDefaultPrivateKeyfile();

        boolean connected = false;

        if (username != null && keyFile != null) {
            try {
                connected = newConnection.authenticateWithPublicKey(
                        username, keyFile, null);
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger
                    .debug("exception caught during authentication with public key: ",
                            e);
                }
            }
            if (logger.isDebugEnabled()) {
                logger
                .debug("authentication with public key: "
                        + connected);
            }
        }
        if (!connected && username != null) {
            try {
                connected = newConnection.authenticateWithNone(username);
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger
                    .debug("exception caught during authentication with username: "
                            + e);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("authentication with username: " + connected);
            }
        }
        if (!connected) {
            throw new Exception("unable to authenticate");
        }
        if (logger.isInfoEnabled()) {
            long start = System.currentTimeMillis();
            try {
                newConnection.ping();
                logger.info("ping connection: "
                        + (System.currentTimeMillis() - start) + " ms");
            } catch (Exception e) {
                logger.info("ping failed: " + e);
            }

        }
        return newConnection;
    }
    
    public static java.io.File getDefaultPrivateKeyfile() {
        String keyfile = null;

        // no key file given, try id_dsa and id_rsa
        String home = System.getProperty("user.home");
        String fileSep = System.getProperty("file.separator");

        if (home == null) {
            home = "";
        } else {
            home += fileSep;
        }

        keyfile = home + ".ssh" + fileSep + "id_dsa";

        java.io.File keyf = new java.io.File(keyfile);

        if (!keyf.exists()) {
            keyfile = home + ".ssh" + fileSep + "id_rsa";
            keyf = new java.io.File(keyfile);

            if (!keyf.exists()) {
                keyfile = home + ".ssh" + fileSep + "identity";
                keyf = new java.io.File(keyfile);

                if (!keyf.exists()) {
                    keyfile = home + "ssh" + fileSep + "id_dsa";
                    keyf = new java.io.File(keyfile);

                    if (!keyf.exists()) {
                        keyfile = home + "ssh" + fileSep + "id_rsa";
                        keyf = new java.io.File(keyfile);

                        if (!keyf.exists()) {
                            keyfile = home + "ssh" + fileSep + "identity";
                            keyf = new java.io.File(keyfile);

                            if (!keyf.exists()) {
                                return null;
                            }
                        }
                    }
                }
            }
        }

        return keyf;
    }
    
    public static void disConnect(Connection c) {
        
    }

}
