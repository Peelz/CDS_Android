package socket;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import service.BackgroundTask;

import static constants.Constant.HOST;

/**
 * Created by gutte on 4/9/2017.
 */

public class CommandSocket implements Runnable {

    private final BackgroundTask task;
    Socket socket ;
    byte[] buffer = new byte[1024];



    public CommandSocket(BackgroundTask task){
        this.task = task ;
    }

    @Override
    public void run() {

        try {
            socket = new Socket(HOST, 7769);
//            System.out.println( "sleep" );
//            Thread.sleep(1000);
//            System.out.println( "Go" );

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.print("-a PHONE");
            out.flush();

            InputStream is = socket.getInputStream();
            int read ;
            while( (read = is.read(buffer)) != -1 ){
                String str = new String(buffer, 0,read) ;
                task.commandDecoder(str);
                Log.d("Command Socket", "Receive "+str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendStringData(String data){
        try {
            DataOutputStream dOut = new DataOutputStream( socket.getOutputStream());
            dOut.writeBytes(data);

        } catch (IOException e) {
            Log.d("Driver socket", "Data not sent");
        }
    }
}
