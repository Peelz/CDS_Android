package constants;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by gutte on 4/8/2017.
 */

public class Constant {

    //
    public static String HOST = "192.168.100.1" ;

    public static String CAMERA_ADD = "http://"+HOST+":8080?action=stream" ;

    final static int SOCKET_CMD_PORT = 7769 ;

    final static int SOCKET_DRIVER_PORT = 7789 ;

    //
    final static int DEFAULT_CONTROL_MODE = 0 ;

    public final static int PHONE_CONTROL = 0 ;

    public final static int SIMSET_CONTROL = 1 ;

    //
    public final static String GEAR_N_TEXT = "N" ;

    public final static String GEAR_P_TEXT = "P" ;

    public final static String GEAR_R_TEXT = "R" ;

    public final static String GEAR_D_TEXT = "D" ;

    //
    public static final String CMD_CHANGE_GEAR = "-cg" ;

    public static final String CMD_CHANGE_MODE = "-cm" ;

    public static Socket SOCKET_CMD ;

    public static Socket SOCKET_DRIVER ;

    Constant(){
        try {
            SOCKET_CMD = new Socket(HOST, SOCKET_CMD_PORT);
            SOCKET_DRIVER = new Socket(HOST, SOCKET_DRIVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
