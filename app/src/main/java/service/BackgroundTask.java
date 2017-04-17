package service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import moba.cds.ControlScreen;
import moba.cds.R;
import model.Constant;
import socket.CommandSocket;
import socket.DriverSocket;

/**
 * Created by gutte on 4/9/2017.
 */

public class BackgroundTask {

    ControlScreen activity ;

    DriverSocket driverSocket;
    CommandSocket commandSocket;

    Handler mHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Button button = (Button) msg.obj;
            activity.setCurrentUIGear(button);
        }
    };


    public BackgroundTask(ControlScreen activity){

        this.activity = activity;
        driverSocket = new DriverSocket();
        commandSocket = new CommandSocket(BackgroundTask.this);

        new Thread(driverSocket).start() ;
        new Thread(commandSocket).start() ;

    }

    void CommadTask(Command command){


    }

    public  void commandDecoder(String message){
        String[] str = message.split(" ");
        switch (str[0]){
            case Constant.CMD_CHANGE_GEAR:
                Log.d("Task Decode", str[0]+str[1]);
                changeGear(str[1]) ;
                break;
            case  Constant.CMD_CHANGE_MODE:
                break;
        }

    }

    void changeGear(String gear){
        Button btn = null;
        Message msg ;
        switch (gear){
            case Constant.GEAR_D_TEXT:
                btn = (Button)activity.findViewById(R.id.button_gear_D);
                break;
            case Constant.GEAR_N_TEXT:
                btn = (Button)activity.findViewById(R.id.button_gear_N);
                break ;

            case Constant.GEAR_P_TEXT:
                btn = (Button)activity.findViewById(R.id.button_gear_P);
                break ;
            case Constant.GEAR_R_TEXT:
                btn = (Button)activity.findViewById(R.id.button_gear_R);
                break;
        }

        msg = mHandler.obtainMessage(1 ,btn);
        msg.sendToTarget();
    }

    public void changeMode(String mode){

    }

    public void sendDriverControlData(String str){

        driverSocket.sendStringData(str);
    }

    public void sendCommandData(String cmd){

        commandSocket.sendStringData(cmd);
    }
}
