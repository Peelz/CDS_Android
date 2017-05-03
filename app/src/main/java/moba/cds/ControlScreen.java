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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;

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
    private Sensor mHeadTracking ;

    //
    private int tmpAcc = 0 ;

    //Gear Button
    public Button gearP, gearR, gearN, gearD, activeGear ;

    //Driving Control button
    Button btnSpeed,btnBreak ;

    //Switch
    ToggleButton controlModeSwitch ;

    //Camera Display
    MjpegView cameraView;

    RelativeLayout mainLayout ;
        RelativeLayout gearGroupLayout ;
        RelativeLayout driverControlLayout ;


    SeekBar seekBarCam;

    //Begin Speed and Break position AxisY
    private float start_distance = 0 ;

    private float max_distance ;

    //Background service
    private BackgroundTask backgroundTask ;

    // time rad/s
    private static final float NS2S = 1.0f / 1000000000.0f;
    private long timestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.control_screen);
        Log.d(TAG, "Start Control Screen") ;

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

        this.controlModeSwitch = (ToggleButton)findViewById(R.id.switch_control_mode);

        this.seekBarCam = (SeekBar)findViewById(R.id.cam_pos_seeker) ;
        //Background Thread
        backgroundTask = new BackgroundTask(ControlScreen.this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initial() ;

    }

    void setCameraView(){

//        Test Url
//        String my_url = "http://192.168.100.1:8080?action=stream";
//        String test_url1 = "http://plazacam.studentaffairs.duke.edu/axis-cgi/mjpg/video.cgi?resolution=320x240" ;
//        String test_url2 = "http://plazacam.studentaffairs.duke.edu/mjpg/video.mjpg";

        cameraView.setDispWidth(960);
        cameraView.setDispHeight(544);
        cameraView.setSource(Constant.getCameraAddr());

    }

    void initial(){

        //init
        this.max_distance = getScreenHeight()/2 ;

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mHeadTracking = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        setCurrentUIGear(gearN);
        setCameraView();

        this.seekBarCam.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                backgroundTask.sendCommandData("-cc "+progress+" ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        this.controlModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if( AppSystem.ALLOW_CHANGE_MODE == false ){

                buttonView.setChecked(!isChecked);
//                Log.d(TAG, "Not change mode "+AppSystem.ALLOW_CHANGE_MODE);

            }else {

                String msg = (isChecked ? Constant.PHONE_CONTROL_STRING : Constant.SIMSET_CONTROL_STRING);
                backgroundTask.sendCommandData("-cm "+ msg);
//                Log.d(TAG, "Send command " + msg);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backgroundTask.stop() ;
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mHeadTracking, SensorManager.SENSOR_DELAY_GAME);
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

        switch (AppSystem.CONTROL_MODE){

            case Constant.PHONE_CONTROL :
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float aX= event.values[0];
                    float aY= event.values[1];
                    int angle = (int) (Math.atan2(aY,aX)/(Math.PI/180)*1.333) ;


                    int value = setAngle(angle+90) ;
                    if (value != tmpAcc){
                        //        Background Thread Send Rotate Control data
                        //         Log.d(TAG, header+value);
                        backgroundTask.sendDriverControlData("t"+value);
                    }
                }
                break;

            case Constant.SIMSET_CONTROL :
                if (event.sensor.getType()== Sensor.TYPE_GYROSCOPE){
                    if (timestamp != 0) {
                        final float dT = (event.timestamp - timestamp) * NS2S;
                        float x = event.values[0];

                        if (x != 0.0 ){
                            backgroundTask.sendCommandData("-cc "+String.format("%.3f",x*dT)+" ");
//                            Log.d(TAG, String.format("%.3f",x*dT));
                        }
                    }
                    timestamp= event.timestamp;
                }

                break ;
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

    public void setControlMode(String mode){
        //Phone Control Mode
        if(mode.equals(Constant.PHONE_CONTROL_STRING)){

            AppSystem.CONTROL_MODE = 0 ;
            this.gearGroupLayout.setVisibility(View.VISIBLE);
            this.driverControlLayout.setVisibility(View.VISIBLE);
            this.seekBarCam.setVisibility(View.VISIBLE);
            this.controlModeSwitch.setChecked(true);
        }
        //Sim control mode
        else if(mode.equals(Constant.SIMSET_CONTROL_STRING)){

            AppSystem.CONTROL_MODE = 1 ;
            this.gearGroupLayout.setVisibility(View.GONE);
            this.driverControlLayout.setVisibility(View.GONE);
            this.seekBarCam.setVisibility(View.GONE);
            this.controlModeSwitch.setChecked(false);
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


