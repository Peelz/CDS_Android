package socket;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import model.Constant;

/**
 * Created by gutte on 4/10/2017.
 */

public class DriverSocket implements Runnable {
    Socket socket ;

    @Override
    public void run() {
        try {
            socket = new Socket(Constant.HOST, 7789);
            DataOutputStream dOut = new DataOutputStream( socket.getOutputStream());
            dOut.writeBytes("PHONE");
        } catch (IOException e) {
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
