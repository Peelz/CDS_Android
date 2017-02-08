package controller;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import model.Server;
import model.DataBlock;

/**
 * Created by gutte on 11/26/2016.
 */

public class DataSender implements Runnable {


    private String data ;

    Server server = new Server() ;


    //192.168.100.1
    Socket socket ;
    DataOutputStream dataOutput ;

//    DataBlock data ;
    public DataSender(){

    }
    public DataSender(DataBlock data){

        this.data = data.getDataToString() ;
    }

    public String getData(){
        return this.data ;
    }


    @Override
    public void run() {

        try {
            socket = new Socket(server.getServerAddr(), server.getServerPort());
            socket.setSoTimeout(1000);
            Log.d("Connection Success", "Connected");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Connection Failure", e.getMessage());
        }


    }
    public void sendData(DataBlock data)
    {
        try {

            dataOutput = new DataOutputStream(socket.getOutputStream());
            dataOutput.writeBytes(data.getDataToString());
            Log.d("Datasender Success", data.getDataToString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Datasender Failure", e.getMessage());

        }

    }


}
