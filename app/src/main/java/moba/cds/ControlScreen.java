package moba.cds;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.camera.MjpegView;

import constants.AppSystem;
import constants.Constant;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class ControlScreen extends Activity implements SensorEventListener{


    private String TAG = "CONTROL_SCREEN";
    // Sensor
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    //
    private int tmpAcc = 0 ;

    //Gear Button
    public Button gearP, gearR, gearN, gearD, activeGear ;

    //Driving Control button
    Button btnSpeed,btnBreak ;

    //Switch
    Switch controlModeSwitch ;

    //Camera Display
    MjpegView cameraView;

    RelativeLayout mainLayout ;
        RelativeLayout gearGroupLayout ;
        RelativeLayout driverControlLayout ;

    //Begin Speed and Break position AxisY
    private float start_distance = 0 ;

    private float max_distance ;

    //Background service
    private BackgroundTask backgroundTask ;
    public boolean ALLOW_CHANGE_MODE = false ;
    private PowerManager.WakeLock mWakeLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.control_screen);

        this.mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        this.gearGroupLayout = (RelativeLayout) findViewById(R.id.gear_layout);
        this.driverControlLayout = (RelativeLayout) findViewById(R.id.driver_control_layout);

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

        //Background Thread
        backgroundTask = new BackgroundTask(ControlScreen.this);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();


        initial() ;


    }

    void setCameraView(){
        String my_url = "http://192.168.100.1:8080?action=stream";
        String test_url1 = "http://plazacam.studentaffairs.duke.edu/axis-cgi/mjpg/video.cgi?resolution=320x240" ;
        String test_url2 = "http://plazacam.studentaffairs.duke.edu/mjpg/video.mjpg";


        cameraView.setDispWidth(960);
        cameraView.setDispHeight(544);
        cameraView.setSource(Constant.CAMERA_ADD);

    }

    void initial(){
        //init
        this.max_distance = getScreenHeight()/2 ;

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        setCurrentUIGear(gearN);
        setCameraView();
        setSwitchHandler();


    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }


    //Rotation Sensor
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float aX= event.values[0];
            float aY= event.values[1];
            int angle = (int) (Math.atan2(aY,aX)/(Math.PI/180)) ;

            String header = "t";

            int value = setAngle(angle+90) ;
            if (value != tmpAcc){
    //        Background Thread Send Rotate Control data
    //         Log.d(TAG, header+value);
                 backgroundTask.sendDriverControlData(header+value);
            }

        }

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

    // Bind switch to change mode control
    void setSwitchHandler(){
        controlModeSwitch.setOnClickListener((View) ->{
            String head = Constant.CMD_CHANGE_MODE ;
            String arg = "" ;

            if(ALLOW_CHANGE_MODE){
                if( controlModeSwitch.isChecked() ){
                    arg = Constant.PHONE_CONTROL_STRING ;
                }else{
                    arg =Constant.SIMSET_CONTROL_STRING ;
                }
                backgroundTask.sendCommandData(head+" "+arg);

                Log.d(TAG, "Change mode from ui "+ALLOW_CHANGE_MODE);

            }else{
                Log.d(TAG, "Change mode from ui "+ALLOW_CHANGE_MODE);

            }
        });

    }

    public void setControlMode(String mode){
        //Phone Control Mode
        if(mode.equals(Constant.PHONE_CONTROL_STRING)){

            AppSystem.CONTROL_MODE = 0 ;
            gearGroupLayout.setVisibility(View.VISIBLE);
            driverControlLayout.setVisibility(View.VISIBLE);
            controlModeSwitch.setChecked(true);
            controlModeSwitch.setText("Phone");
        }
        //Sim control mode
        else if(mode.equals(Constant.SIMSET_CONTROL_STRING)){

            AppSystem.CONTROL_MODE = 1 ;
            gearGroupLayout.setVisibility(View.GONE);
            driverControlLayout.setVisibility(View.GONE);
            controlModeSwitch.setChecked(false);
            controlModeSwitch.setText("Simulator");
        }
        Log.d(TAG,"Set Mode "+mode);

    }
    // Gear Handler
    public void gearHandler(View view){

        Button btn = (Button) findViewById(view.getId());
        String value = btn.getText().toString();
        String head = Constant.CMD_CHANGE_GEAR ;

        if (btn != this.activeGear){
            backgroundTask.sendCommandData(head+" "+value);
        }
    }

    public void setCurrentUIGear(Button button){

        if (button != null){
            reSetActiveGear();
            button.setBackgroundResource(R.drawable.active_gear_button) ;
            this.activeGear = button ;
            Log.d(TAG, "Change Gear"+button.getText().toString());
        }
//        Log.d(TAG, "Change Gear Arg is Null");
    }

    public void reSetActiveGear(){
        if(this.activeGear != null){
            this.activeGear.setBackgroundResource(R.drawable.gear_button);
        }
    }


    View.OnTouchListener ButtonSliderControl = (view, event) -> {
        ButtonSliderHandle(view, event);
        return true;
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
                value = setValueByUiPosition(currY) ;

                break;
            case ACTION_DOWN:
                if(start_distance == 0 ){
                    start_distance = view.getY();
                }
                header = setHeaderValueByView(view);
                value = setValueByUiPosition(currY);
                break;
            case ACTION_UP:
                view.setY(start_distance);
                currY = start_distance ;
                header = setHeaderValueByView(view);
                value = setValueByUiPosition(currY);
                break;
        }
//        Background Thread Send Speed/Brake Control data
        backgroundTask.sendDriverControlData(header + value);

    }

    int setValueByUiPosition(float param){
        int result = (int) (Math.abs(start_distance - param)*100/Math.abs(start_distance - max_distance));
        return result;
    }
}


