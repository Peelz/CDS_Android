package moba.cds;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import constants.Constant;
import socket.CommandSocket;
import socket.DriverSocket;

/**
 * Created by gutte on 4/9/2017.
 */

public class BackgroundTask {

    private final String TAG = "BACKGROUND_TASK";

    ControlScreen controlScreen;

    HomeScreen homeScreen ;

    DriverSocket driverSocket = null ;
    CommandSocket commandSocket = null ;

    Thread driverSocketThread = null ;
    Thread commandSocketThread = null ;


    public Handler mHandler = new Handler(Looper.getMainLooper()){


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CommandBlock command = (CommandBlock) msg.obj ;

            switch (command.getHead()){
                case (Constant.CMD_CHANGE_GEAR):
                    changeGear(command.getArg()) ;
                    break;

                case (Constant.CMD_CHANGE_MODE):
                    changeMode(command.getArg());
                    break;
            }
//            Log.d(TAG, "Handler done");
        }
    };

    public BackgroundTask(){
        driverSocket = new DriverSocket();
        commandSocket = new CommandSocket(BackgroundTask.this);

        commandSocketThread = new Thread(commandSocket) ;
        driverSocketThread = new Thread(driverSocket) ;

    }

    public BackgroundTask(ControlScreen controlScreen){

        this.controlScreen = controlScreen;
        driverSocket = new DriverSocket();
        commandSocket = new CommandSocket(BackgroundTask.this);

    }

    public void changeGear(String gear){
        Button btn = null;
        switch (gear){
            case Constant.GEAR_D_TEXT:
                btn = (Button) controlScreen.findViewById(R.id.button_gear_D);
                break;
            case Constant.GEAR_N_TEXT:
                btn = (Button) controlScreen.findViewById(R.id.button_gear_N);
                break ;

            case Constant.GEAR_P_TEXT:
                btn = (Button) controlScreen.findViewById(R.id.button_gear_P);
                break ;
            case Constant.GEAR_R_TEXT:
                btn = (Button) controlScreen.findViewById(R.id.button_gear_R);
                break;
        }
        controlScreen.setCurrentUIGear(btn);

    }


    public void changeMode(String mode){
        Log.d(TAG, "Change Mode input "+mode);
        controlScreen.setControlMode(mode);

    }

    public void sendDriverControlData(String str){

        driverSocket.sendStringData(str);
    }

    public void sendCommandData(String cmd){

        commandSocket.sendStringData(cmd);
    }

    public void setControlScreen(Context controlScreen){
        this.controlScreen = (ControlScreen)controlScreen ;
    }

    public boolean isConnectionAlive(){
        return commandSocket.isConnect() ;
    }


}
