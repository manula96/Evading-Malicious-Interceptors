import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.net.ServerSocket;

/**
 * The server, it will look for a client, receive a message and send one back.
 * It is current not secure, so to fix that, set up the SSL certificates and
 * add the following lines to the code in the correct places:
 * 
 * System.setProperty("javax.net.ssl.keyStore", "keystoreFile");
 * System.setProperty("javax.net.ssl.keyStorePassword", "password");
 * SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
 * SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(port);
 * SSLSocket socket = (SSLSocket) serverSocket.accept();
 * socket.setTcpNoDelay(true);
 * socket.startHandshake();
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
        int port = 2250;  // Arbitrary non-privileged port
        System.out.format("Listening for a client on port %d\n", port);

        try (
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.format(
                "Connected by %s:%d\n",
                socket.getInetAddress().toString(),
                socket.getPort()
            );

            // Receive a message from the client
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte data[] = new byte[1024];
            baos.write(data, 0, in.read(data));
            System.out.format("Client -> Server: %s\n", new String(data));

            // Send a message to the client
            out.write("I'll do the best I can. Let's get moving.".getBytes());
        } catch (Exception e) {
            // TODO: Add some better error handling
            e.printStackTrace();
        }
    }
}