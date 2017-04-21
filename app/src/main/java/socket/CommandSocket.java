package socket;

import android.os.Message;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import moba.cds.CommandBlock;
import moba.cds.BackgroundTask;

import static constants.Constant.HOST;

/**
 * Created by gutte on 4/9/2017.
 */

public class CommandSocket implements Runnable {

    private final BackgroundTask task;

    String TAG = "CommandSocket" ;
    Socket socket ;

    byte[] buffer = new byte[512];

    boolean isConnect = false ;



    public CommandSocket(BackgroundTask task){
        this.task = task ;
    }

    @Override
    public void run() {

        try {
            socket = new Socket(HOST, 7769);

            if ( auth() ){
                isConnect = true ;
                recv();
            }else{
                isConnect = false ;
            }

        } catch (IOException e) {
            this.socket = null ;
            e.printStackTrace();
        }

    }

    public boolean auth(){

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.print("-a PHONE");
            out.flush();
            return true ;

        } catch (IOException e) {
//            e.printStackTrace();
            return false ;
        }

    }

    public void recv() throws IOException {
        InputStream is = socket.getInputStream();
        int read ;
        while( (read = is.read(buffer)) != -1 ){
            String str = new String(buffer, 0,read) ;
            CommandBlock cmd = new CommandBlock(str);

            Message msg = task.mHandler.obtainMessage(1, cmd);
            msg.sendToTarget();

            Log.d(TAG, "Receive "+str);
        }

    }

    public void sendStringData(String data){
        try {
            DataOutputStream dOut = new DataOutputStream( socket.getOutputStream());
            Log.d(TAG, "Sent "+dOut);
            dOut.writeBytes(data);


        } catch (IOException e) {
            Log.d("Driver socket", "Data not sent");
        }
    }

    public boolean isConnect(){
        return isConnect ;
    }
}
