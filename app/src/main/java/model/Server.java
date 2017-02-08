package model;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * Created by gutte on 11/25/2016.
 */

public class Server {

//    private String SERVER_ADDR = "192.168.137.1" ;
//    private String SERVER_ADDR = "158.108.247.104";
//    private String SERVER_ADDR = "192.168.129.2"; //Virtual Mach
    private String SERVER_ADDR = "192.168.100.1";
    private int SERVER_PORT = 7789 ;

    public Server() {

    }

    public Server(String SERVER_ADDR) {
        setSERVER_ADDR(SERVER_ADDR);
    }

    public void setSERVER_ADDR(String SERVER_ADDR){
        this.SERVER_ADDR = SERVER_ADDR;
    }
    public String getServerAddr(){
        return this.SERVER_ADDR ;
    }

    public int getServerPort(){
        return this.SERVER_PORT ;
    }






}
