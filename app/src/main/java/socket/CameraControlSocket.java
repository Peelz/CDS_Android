package socket;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static constants.Constant.HOST;

/**
 * Created by gutte on 4/28/2017.
 */

public class CameraControlSocket implements Runnable {

    String TAG = "CameraControlSocket" ;
    Socket socket ;

    @Override
    public void run() {
        try{
            socket = new Socket(HOST, 7779);

        }catch(IOException e){
            Log.d(TAG, e.toString()) ;
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
