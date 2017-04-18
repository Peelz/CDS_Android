package moba.cds;

import android.app.Activity;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.camera.MjpegView;

import constants.AppSystem;
import constants.Constant;
import service.BackgroundTask;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class ControlScreen extends Activity implements SensorEventListener{


    //Gear Button
    Button gearP, gearR, gearN, gearD, activeGear ;

    //Driving Control button
    Button btnSpeed,btnBreak ;

    //Switch
    Switch controlModeSwitch ;

    //Camera Display
    MjpegView cameraView;

    RelativeLayout mainLayout ;
        RelativeLayout gearGroupLayout ;
        GridLayout driverControlLayout ;

    //Begin Speed and Break position AxisY
    private float start_distance = 0 ;

    private float max_distance ;

    //Background service
    private BackgroundTask backgroundTask ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.control_screen);

        this.mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        this.gearGroupLayout = (RelativeLayout) findViewById(R.id.gear_layout);
        this.driverControlLayout = (GridLayout) findViewById(R.id.driver_control_layout);

        this.gearP = (Button) findViewById(R.id.button_gear_P) ;
        this.gearR = (Button) findViewById(R.id.button_gear_R) ;
        this.gearN = (Button) findViewById(R.id.button_gear_N) ;
        this.gearD = (Button) findViewById(R.id.button_gear_D) ;

        this.btnSpeed = (Button) findViewById(R.id.Speed_button);
        this.btnBreak = (Button) findViewById(R.id.Break_button);

        this.btnBreak.setOnTouchListener(ButtonSliderControl);
        this.btnSpeed.setOnTouchListener(ButtonSliderControl);

        this.cameraView = (MjpegView) findViewById(R.id.cameraView);

        this.controlModeSwitch = (Switch)findViewById(R.id.switch_control_mode);

        initial() ;
        setCameraView();
    }

    void setCameraView(){
        String my_url = "http://192.168.137.97:8080?action=stream";
        String test_url1 = "http://plazacam.studentaffairs.duke.edu/axis-cgi/mjpg/video.cgi?resolution=320x240" ;
        String test_url2 = "http://plazacam.studentaffairs.duke.edu/mjpg/video.mjpg";

        cameraView.setDispWidth(960);
        cameraView.setDispHeight(544);
        cameraView.setSource(test_url1);

    }

    void initial(){
        //init
        this.max_distance = getScreenHeight()/2 ;
        this.activeGear = gearN ;
        gearN.setBackgroundResource(R.drawable.active_gear_button) ;

        // Get an instance of the SensorManager
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);

        controlModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked){
                setControlMode(1);
                controlModeSwitch.setText("Simulator");
            }else {
                setControlMode(0);
                controlModeSwitch.setText("Phone");
            }
        });

        //Background Thread
//        backgroundTask = new BackgroundTask(ControlScreen.this);
        String streaming_url = "http://www.bmatraffic.com/PlayVideo.aspx?ID=250" ;

    }

    public void setControlMode(int mode){
        if(mode== Constant.PHONE_CONTROL){
            AppSystem.CONTROL_MODE = 0 ;
            gearGroupLayout.setVisibility(View.VISIBLE);
            driverControlLayout.setVisibility(View.VISIBLE);

        }
        if(mode == Constant.SIMSET_CONTROL){
            AppSystem.CONTROL_MODE = 1 ;
            gearGroupLayout.setVisibility(View.GONE);
            driverControlLayout.setVisibility(View.GONE);

        }
    }

    //Rotation Sensor
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        float aX= event.values[0];
        float aY= event.values[1];
        int angle = (int) (Math.atan2(aY,aX)/(Math.PI/180)) ;

        String header = "t";

        int value = setAngle(angle+90) ;

//        Background Thread Send Rotate Control data
//        backgroundTask.sendDriverControlData(header+value);
    }


    public int setAngle(int value){
        if(value <0 ){
            return 0 ;
        }else if (value > 180 ){
            return 180 ;
        }else{
            return value ;
        }
    }

    public void setCurrentUIGear(Button button){

        if (button != null){
            reSetActiveGear();
            button.setBackgroundResource(R.drawable.active_gear_button) ;
            this.activeGear = button ;
            Log.d("Change Gear", button.getText().toString());
        }
    }


    public void gearHandler(View view){

        Button btn = (Button) findViewById(view.getId());
        String value = btn.getText().toString();

        if (btn != this.activeGear){
//            backgroundTask.sendCommandData(new Command().setChangeGearRequest(value) );
        }
    }

    public void reSetActiveGear(){
        if(this.activeGear != null){
            this.activeGear.setBackgroundResource(R.drawable.gear_button);
        }
    }


    View.OnTouchListener ButtonSliderControl = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            ButtonSliderHandle(view, event);
            return true;
        }
    };

    public String setHeaderValueByView(View v){
        if(v == btnSpeed){
            return "a";
        }else if(v == btnBreak){
            return "b";
        }
        return null;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void ButtonSliderHandle(View view, MotionEvent event){
        float currY = event.getRawY();
        String header = null;
        int value = 0;

        switch (event.getAction()){
            case ACTION_MOVE:
                if(currY >= start_distance){
                    view.setY(start_distance) ;
                    currY = start_distance ;
                }
                if(currY <= max_distance){
                    view.setY(max_distance);
                    currY = max_distance ;
                }
                view.setY(currY);
                header = setHeaderValueByView(view) ;
                value = setActiveValueByUI(currY) ;

                break;
            case ACTION_DOWN:
                if(start_distance == 0 ){
                    start_distance = view.getY();
                }
                header = setHeaderValueByView(view);
                value = setActiveValueByUI(currY);
                break;
            case ACTION_UP:
                view.setY(start_distance);
                currY = start_distance ;
                header = setHeaderValueByView(view);
                value = setActiveValueByUI(currY);
                break;
        }
//        Background Thread Send Speed/Brake Control data
//        backgroundTask.sendDriverControlData(header + value);

    }

    int setActiveValueByUI(float param){
        int result = (int) (Math.abs(start_distance - param)*100/Math.abs(start_distance - max_distance));
        return result;
    }



}


