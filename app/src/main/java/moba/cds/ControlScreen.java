package moba.cds;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.camera.MjpegView;

import service.BackgroundTask;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class ControlScreen extends Activity implements SensorEventListener{


    //Gear Button
    Button gearP,gearR,gearN,gearD ;
    Button activeGear;

    MjpegView cameraView;

    RelativeLayout mainLayout ;

    //Driving Control button
    Button btnSpeed,btnBreak ;

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

        this.gearP = (Button) findViewById(R.id.button_gear_P) ;
        this.gearR = (Button) findViewById(R.id.button_gear_R) ;
        this.gearN = (Button) findViewById(R.id.button_gear_N) ;
        this.gearD = (Button) findViewById(R.id.button_gear_D) ;

        this.btnSpeed = (Button) findViewById(R.id.Speed_button);
        this.btnBreak = (Button) findViewById(R.id.Break_button);

        this.btnBreak.setOnTouchListener(ButtonSliderControl);
        this.btnSpeed.setOnTouchListener(ButtonSliderControl);

        this.cameraView = (MjpegView) findViewById(R.id.cameraView);

        initial() ;
        setCameraView();




    }

    void setCameraView(){
        String my_url = "http://192.168.137.97:8080?action=stream";

        cameraView.setDispWidth(960);
        cameraView.setDispHeight(544);
        cameraView.setDisplayMode(4);
        cameraView.setSource(my_url);

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

        //Background Thread
//        backgroundTask = new BackgroundTask(ControlScreen.this);
        String streaming_url = "http://www.bmatraffic.com/PlayVideo.aspx?ID=250" ;


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


    public void showDialog(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setMessage("รับขนมจีบซาลาเปาเพิ่มมั้ยครับ?");
        builder.setPositiveButton("รับ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(),
                        "ขอบคุณครับ", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("ไม่รับ", null);
        builder.create();

        // สุดท้ายอย่าลืม show() ด้วย
        builder.show();
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


