package moba.cds;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gutte on 4/12/2017.
 */

public class ImageToByteTest {


    @Test
    public void tester(){
        String path = "C:\\Users\\gutte\\OneDrive\\Pictures\\to_be_TI20.jpg" ;

        try {
            URL image = new URL("http://192.168.137.51:8080/?action=stream");
            InputStream ins = new BufferedInputStream(image.openStream());
            ByteArrayOutputStream bins = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];

            int n = 0;
            while ( -1!=(n=ins.read(buf)) ){
                bins.write(buf,0 , n);
            }
            bins.close();
            ins.close();
            byte[] response = bins.toByteArray();

            FileOutputStream fos = new FileOutputStream( "C:\\Users\\gutte\\OneDrive\\Pictures\\Test_CDS_01.mjpg");
            fos.write(response);
            fos.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
