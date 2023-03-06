package com.example.doprava_bp;

import com.google.gson.Gson;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Executable {
    public static final int PORT = 10001;
    public static int keyLenghts = 256;
    public static void main(String[] args) throws Exception {


        //System.out.println("Pokus: " + generateHex(7));
        //System.out.println("Pokus hash: " + hash("1","SHA-256"));


        /*String masterKey = generateHex((keyLenghts / 32) - 1);
        try (FileWriter writer = new FileWriter("masterkey.txt")) {
            writer.write(masterKey);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }*/

        StringBuilder data = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("masterkey.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the file: " + e.getMessage());
        }
        String masterKey = data.toString();

        int idr = 1;
        String idrString = Integer.toString(idr);
        String driverKey = hash(masterKey.concat(idrString), "SHA-1");
        if(keyLenghts == 128) {
            driverKey = hash(masterKey.concat(idrString), "SHA-1");
        }
        else if(keyLenghts == 256){
            driverKey = hash(masterKey.concat(idrString), "SHA-256");
        }
        driverKey = driverKey.substring(0, (keyLenghts/4));
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
        if(keyLenghts == 128){
            hatu = hash(s,"SHA-256");
        }
        else if(keyLenghts == 256){
            hatu = hash(s,"SHA-512");
        }
        hatu = hatu.substring(0,(keyLenghts/4));
        String userKey = hash(driverKey.concat(hatu),"SHA-1");
        if(keyLenghts == 128){
            userKey = hash(driverKey.concat(hatu),"SHA-1");
        }
        else if(keyLenghts == 256){
            userKey = hash(driverKey.concat(hatu),"SHA-256");
        }
        userKey= userKey.substring(0, (keyLenghts/4));

        AppParameters appParameters = new AppParameters();
        ObuParameters obuParameters = new ObuParameters();

        appParameters.setATU(ATU);
        appParameters.setUserKey(userKey);
        appParameters.setHatu(hatu);
        appParameters.setKeyLengths(keyLenghts);

        obuParameters.setIdr(idr);
        obuParameters.setDriverKey(driverKey);
        obuParameters.setKeyLengths(keyLenghts);

        ///Jsonig
        //var gson = new Gson();
        //var appParametersJson = gson.toJson(appParameters);
        //var obuParametersJson = gson.toJson(obuParameters);

        System.out.println("Master key KM: " + masterKey);
        System.out.println("IDr: " + idr);
        System.out.println("Driver key KR: " + driverKey);
        System.out.println("ATU: " + s);
        System.out.println("HATU: " + hatu);
        System.out.println("User key KU: " + userKey);
        //System.out.println(appParametersJson);
        //System.out.println(obuParametersJson);

        //// Sockets
        for(int i = 0;i<=1;i++) {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is up and running on ip " + serverSocket.getInetAddress().getLocalHost().getHostAddress() + " port: " + PORT);
            Socket socket = serverSocket.accept();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            switch (i) {

                case 0:

                ObuParameters recObuParams = (ObuParameters) objectInputStream.readObject();
                System.out.println(recObuParams.message);

                if (recObuParams.message.equals("Hello from OBU!")) {
                    obuParameters.message = "Hi! - from the server!";
                    objectOutputStream.writeObject(obuParameters);
                }
                    serverSocket.close();
                break;
                case 1:

                AppParameters recAppParams = (AppParameters) objectInputStream.readObject();
                System.out.println(recAppParams.message);

                if (recAppParams.message.equals("Hello from App!")) {
                    appParameters.message = "Hi! - from the server!";
                    objectOutputStream.writeObject(appParameters);
                }
                    serverSocket.close();
                break;

                //serverSocket.close();
            }
        }
    }

    public static String generateHex(int option) {
        //option 3 = 128 bit key
        //option -- = 192 bit key
        //option 7 = 256 bit key
        //B374A26A71490437AA024E4FADD5B497FDFF1A8EA6FF12F6FB65AF2720B59CCF
        //1a2b3c4d5e6f7f8a9b0c1d2e3f4a5b67
        //GWS3eDKYYoaZISBxbUINjvhreiiYHSAg
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


