import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        new Client();
    }

    public Client() throws Exception{
        Socket socket = new Socket("127.0.0.1", Executable.PORT);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        AppParameters helloMessage = new AppParameters("Hello from App!");
        objectOutputStream.writeObject(helloMessage);

        AppParameters appParameters = (AppParameters) objectInputStream.readObject();
        System.out.println(appParameters.message);
        System.out.println(appParameters.getATU());
        System.out.println(appParameters.getHatu());
        System.out.println(appParameters.getUserKey());

        objectOutputStream.close();
        socket.close();
    }
}
