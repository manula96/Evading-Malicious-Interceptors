import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.security.cert.X509Certificate;

/**
 * The client, it will connect to the server, send a message and receive one back.
 * It is currently not secure, so to fix that, set up the SSL certificates and
 * add the following lines to the code where appropriate:
 * 
 * System.setProperty("javax.net.ssl.keyStore", "keystoreFile");
 * System.setProperty("javax.net.ssl.keyStorePassword", "password");
 * SSLContext sc = SSLContext.getInstance("SSL");
 * sc.init(null, trustAllCerts, new java.security.SecureRandom());
 * SSLSocketFactory factory = (SSLSocketFactory) sc.getSocketFactory();
 * SSLSocket s = (SSLSocket) factory.createSocket(hostname, port);
 * s.setTcpNoDelay(true);
 * s.startHandshake();
 * 
 * For further information refer to https://docs.oracle.com/en/java/javase/17/docs/api/java.base/javax/net/ssl/SSLSocket.html
 * 
 * In addition to the additional code, you will need to add the certificates to a keystore file via the 
 * keytool command, you will need the following commands:
 * 
 * keytool -keystore keystore -genkeypair -keyalg rsa
 * keytool -importkeystore -srckeystore keystore -destkeystore keystore.p12 -deststoretype PKCS12
 * keytool -import -trustcacerts -keystore test -file Certificate.crt -alias cert1
 * 
 * The first command creates a private and public key, the second exports the private key to be used by XCA,
 * and the third is used after creating the certificates using XCA and imports them into the keystore. The
 * third command must be run for each certificate created and exported from XCA, each time with a different
 * alias.
 */

public class Client {
    public static void main(String[] args) {
        String hostname = "localhost";  // Server hostname or IP
        int port = 2250; // The same port as used by the server
        System.out.format("Connecting to the server at %s:%d\n", hostname, port);

        // Blindly trust all root CAs
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {} 
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            } 
        };

        try {
            Socket s = new Socket(hostname, port);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            DataInputStream in = new DataInputStream(s.getInputStream());

            // Send a message to the server
            out.write("Be careful. There's no telling what tricks they have planned.".getBytes());
        
            // Receive a message from the server
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte data[] = new byte[1024];
            baos.write(data, 0, in.read(data));
            System.out.format("Client <- Server: %s\n", new String(data));

            // Close all streams
            in.close();
            out.close();
            s.close();
        } catch (Exception e) {
            // TODO: Add some better error handling
            e.printStackTrace();
        }        
    }
}