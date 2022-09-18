import com.google.gson.Gson;
import org.jgroups.*;
import org.jgroups.blocks.cs.ReceiverAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Executable {
    public static final int PORT = 3191;
    public static void main(String[] args) throws Exception {

        String masterKey = generateHex(1);
        int idr = 1; // TODO: to be replaced by IDs from file
        String idrString = Integer.toString(idr);
        String driverKey = hash(masterKey.concat(idrString),"SHA-1");
        driverKey = driverKey.substring(0, 16);
        byte[] ATU = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        String userID = "00000001";
        String vehicleID = "00000001";
        char it = 'S';
        int noe = 50;
        SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd/HH/mm");
        Date today = new Date();


        fillAtu(ATU,userID,vehicleID,it,noe,formatter,today);
        String s = new String(ATU);
        String hatu = hash(s,"SHA-256");
        String userKey = hash(driverKey.concat(hatu),"SHA-1");
        userKey= userKey.substring(0, 16);

        AppParameters appParameters = new AppParameters();
        ObuParameters obuParameters = new ObuParameters();

        appParameters.setATU(ATU);
        appParameters.setUserKey(userKey);
        appParameters.setHatu(hatu);

        obuParameters.setIdr(idr);
        obuParameters.setDriverKey(driverKey);

        ///Jsonig
        var gson = new Gson();
        var appParametersJson = gson.toJson(appParameters);
        var obuParametersJson = gson.toJson(obuParameters);

        System.out.println("Master key KM: " + masterKey);
        System.out.println("IDr: " + idr);
        System.out.println("Driver key KR: " + driverKey);
        System.out.println("ATU: " + s);
        System.out.println("HATU: " + hatu);
        System.out.println("User key KU: " + userKey);
        System.out.println(appParametersJson);
        System.out.println(obuParametersJson);

        //// Sockets
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is up and running on port: " + PORT);
        Socket socket = serverSocket.accept();

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        AppParameters recAppParams = (AppParameters) objectInputStream.readObject();
        System.out.println(recAppParams.message);

        if (recAppParams.message.equals("Hello from App!")){
            appParameters.message = "Hi! - from the server!";
            objectOutputStream.writeObject(appParameters);
        }
        else if (recAppParams.message.equals("Hello from OBU!")){
            obuParameters.message = "Hi! - from the server!";
            objectOutputStream.writeObject(obuParameters);
        }

        serverSocket.close();
    }

    public static String generateHex(int option) {
        String hexadecimal = "";

        // Random instance
        for (int i=0;i<(option+1);i++){
            Random r = new Random();
            int n = r.nextInt();
            String temporary = Integer.toHexString(n);
            hexadecimal = hexadecimal.concat(temporary);
        }

        return hexadecimal;
    }

    public static String hash(String input, String hashType)
    {
        try {
            // getInstance() method is called with algorithm SHA-1
            MessageDigest md = MessageDigest.getInstance(hashType);

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            /*while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }*///

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fillAtu(byte[] ATU,String userID, String vehicleID, char it, int noe, SimpleDateFormat formatter, Date today){

        byte[] userIdBytes = userID.getBytes();
        byte[] vehicleIdBytes = vehicleID.getBytes();
        for(int i = 0;i<8;i++){
            ATU[i] = userIdBytes[i];
        }

        ATU[8]= (byte) it;
        ATU[27] = (byte) noe;

        for(int i = 9;i<17;i++){
            ATU[i] = vehicleIdBytes[i-9];
        }

        byte[] todayBytes = formatter.format(today).getBytes(); //TODO: převést na String, potom ze substringu vytáhnout int a naplnit ATU
        String todayString = new String(formatter.format(today));

        int todayYear = Integer.parseInt(todayString.substring(0,2));
        int todayMonth = Integer.parseInt(todayString.substring(3,5));
        int todayDay = Integer.parseInt(todayString.substring(6,8));
        int todayHour = Integer.parseInt(todayString.substring(9,11));
        int todayMinute = Integer.parseInt(todayString.substring(12,14));

        ATU[17] = (byte) todayMinute;
        ATU[18] = (byte) todayHour;
        ATU[19] = (byte) todayDay;
        ATU[20] = (byte) todayMonth;
        ATU[21] = (byte) todayYear;
        ATU[22] = 59;
        ATU[23] = 23;
        ATU[24] = 1;
        ATU[25] = 1;
        ATU[26] = 25;
    }
}


