package socket;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import constants.Constant;

/**
 * Created by gutte on 4/10/2017.
 */

public class DriverSocket implements Runnable {
    Socket socket ;
    String TAG = "DriverSocket" ;

    @Override
    public void run() {
        try {
            socket = new Socket(Constant.HOST, 7789);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.print("PHONE");
            out.flush();
            Log.d(TAG,"Send Auth message");
        } catch (IOException e) {
            Log.d(TAG,"Socket Cannt connect") ;
            e.printStackTrace();
        }
    }

    public void sendStringData(String data){

        try {

            DataOutputStream dOut = new DataOutputStream( socket.getOutputStream());
            dOut.writeBytes(data);
//            dOut.flush();
        } catch (IOException e) {
            Log.d("Driver socket", "Data "+data+" not sent "+e.getMessage());
        }
    }
}
