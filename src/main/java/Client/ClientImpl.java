package Client;

import Common.Status;
import Common.TypeOfRequest;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kostya on 14.10.2016.
 */
public class ClientImpl implements Client {
    private Socket clientSocket;
    @Override
    public int connect(String host, int port) {
        try {
            clientSocket = new Socket(host, port);
        } catch (IOException e) {
            return Status.NOT_OK;
        }
        return Status.OK;
    }

    @Override
    public int disconnect() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            return Status.NOT_OK;
        }
        clientSocket = null;
        return Status.OK;
    }

    @Override
    public List<MyFile> executeList(String dirPath) {
        if (clientSocket == null) {
            return null;
        }

        try {
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeInt(TypeOfRequest.LIST);
            dataOutputStream.writeUTF(dirPath);
            dataOutputStream.flush();
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            int size = dataInputStream.readInt();
            List<MyFile> result = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                String filename =  dataInputStream.readUTF();
                boolean isDir = dataInputStream.readBoolean();
                result.add(new MyFile(filename, isDir));
            }
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public byte[] executeGet(String filePath) {
        if (clientSocket == null) {
            return null;
        }

        try {
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeInt(TypeOfRequest.GET);
            dataOutputStream.writeUTF(filePath);
            dataOutputStream.flush();
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            long size = dataInputStream.readLong();
            long reads = 0;
            byte buffer[] = new byte[1024];
            int read;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while(reads < size){
                read = dataInputStream.read(buffer);
                if (read > 0) {
                    byteArrayOutputStream.write(buffer, 0, read);
                    reads += read;
                }
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
