package com.example.doprava_bp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private AppParameters appParameters;
    public static void main(String[] args) throws Exception {
        new Client();
    }

    public Client() throws Exception{
        Socket socket = new Socket("192.168.56.1", Executable.PORT);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        AppParameters helloMessage = new AppParameters("Hello from App!");
        objectOutputStream.writeObject(helloMessage);

        appParameters = (AppParameters) objectInputStream.readObject();

        System.out.println(appParameters.message);
        System.out.println(appParameters.getATU());
        System.out.println(appParameters.getHatu());
        System.out.println(appParameters.getUserKey());



        objectOutputStream.close();
        socket.close();
    }
}
