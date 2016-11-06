import Client.Client;
import Client.MyFile;
import Client.ClientImpl;
import Common.SocketIOException;
import Server.Server;
import Server.ServerImpl;
import Server.RootDirectoryNotFound;

import java.io.File;
import java.util.List;

/**
 * Created by kostya on 14.10.2016.
 */
public class SimpleFtp {
    public static void main(String[] args) throws RootDirectoryNotFound, SocketIOException {
        Server server = new ServerImpl();
        Client client = new ClientImpl();
        final int PORT = 8888;
        server.start(PORT, new File("."));
        client.connect("localhost", PORT);
        List<MyFile> list = client.executeList(".");
        if (list != null) {
            list.forEach(System.out::println);
        }
    }
}
