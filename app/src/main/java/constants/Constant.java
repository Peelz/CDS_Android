package constants;

/**
 * Created by gutte on 4/8/2017.
 */

public class Constant {

    //
    public static String HOST = "192.168.100.1" ;

    public static String CAMERA_ADD = "http://"+HOST+":8080?action=stream" ;

    final static int SOCKET_CMD_PORT = 7769 ;

    final static int SOCKET_DRIVER_PORT = 7789 ;

    final static int SOCKET_CAMERA_CONTROL_PORT = 7779 ;

    //
    public final static int DEFAULT_CONTROL_MODE = 0 ;

    public final static int PHONE_CONTROL = 0 ;

    public final static int SIMSET_CONTROL = 1 ;

    public final static String PHONE_CONTROL_STRING = "PH";

    public final static String SIMSET_CONTROL_STRING = "SIM";

    //
    public final static String GEAR_N_TEXT = "N" ;

    public final static String GEAR_P_TEXT = "P" ;

    public final static String GEAR_R_TEXT = "R" ;

    public final static String GEAR_D_TEXT = "D" ;

    //
    public static final String CMD_CHANGE_GEAR = "-cg" ;

    public static final String CMD_CHANGE_MODE = "-cm" ;

    public static final String CMD_STATUS_CAR = "-s" ;

    //

    public static final String STATUS_NO_SIM = "0" ;

    public static final String STATUS_HAVE_SIM = "1" ;

    public static String getCameraAddr(){

        return "http://"+HOST+":8080?action=stream" ;
    }


}
