package socket;

import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import constants.Constant;
import moba.cds.ControlScreen;

/**
 * Created by gutte on 11/25/2016.
 */

public class SocketThread implements Runnable{


    private String SERVER_ADDR = Constant.HOST ;
    private int SERVER_DRIVER_PORT = 7789 ;
    private int SERVER_CMD_PORT = 7769 ;

    ControlScreen activity ;

    Thread CommandRecevierSocketThread ;
    Thread DriverSocketThread ;


    Socket DRIVER_SOCKET;
    Socket CMD_SOCKET ;

//    public void sendDriverData(String data){
//        try {
//            DataOutputStream dataOut = new DataOutputStream( this.DRIVER_SOCKET.getOutputStream());
//            dataOut.writeBytes(data);
//        } catch (IOException e) {
//            Log.e("Socket Error",e.getMessage());
//        }
//    }
//
//    public void sendCmdData(String data){
//        try {
//            DataOutputStream dataOut = new DataOutputStream( this.CMD_SOCKET.getOutputStream());
//            dataOut.writeBytes(data);
//
//        } catch (IOException e) {
//            Log.e("Socket Error",e.getMessage());
//        }
//    }

    @Override
    public void run(){
        Looper.prepare();
        try{
            this.DRIVER_SOCKET = new Socket(SERVER_ADDR, SERVER_DRIVER_PORT);
            this.CMD_SOCKET = new Socket(SERVER_ADDR, SERVER_CMD_PORT);

        }catch (SocketTimeoutException e){
//            this.activity.showDialog() ;
            Log.e("Socket Error","Timeout");
        }
        catch (UnknownHostException e) {
//            this.activity.showDialog() ;
            Log.e("Socket Error","Unknown Host");
        } catch (IOException e) {
//            this.activity.showDialog() ;
            Log.e("Socket Error",e.getMessage());
        }
    }
}
