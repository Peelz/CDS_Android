package moba.cds;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import constants.Constant;

/**
 * Created by gutte on 4/10/2017.
 */

public class CommandSocketTest {
    String IP = Constant.HOST ;
    Socket socket ;
    byte[] buffer = new byte[1024];
    @Test
    public void sender(){

        try {
            socket = new Socket(IP,7769);
//            System.out.println( "sleep" );
//            Thread.sleep(1000);
//            System.out.println( "Go" );

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.print("-a PHONE");
            out.flush();

            InputStream is = socket.getInputStream();
            int read ;
            while( (read = is.read(buffer)) != -1 ){
                System.out.println( new String(buffer,0,read) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//

    }

}
