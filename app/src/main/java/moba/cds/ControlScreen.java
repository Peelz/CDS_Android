package moba.cds;

import android.app.Activity;
import android.content.res.Resources;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import controller.DataSender;
import model.DataBlock;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static moba.cds.R.id.imageView;

public class ControlScreen extends Activity implements SensorEventListener {


    //Gear Button
    Button gearP,gearR,gearN,gearD ;
    View ActiveGear ;

    int speedValue, breakValue, RotateValue ;

    //Driving Control button
    Button btnSpeed,btnBreak ;

    //Begin Speed and Break position AxisY
    private float start_distance;

    private float max_distance ;


    TextView SpeedText,BreakText,RotationText ;


    Thread DataTransmission;

    private DataBlock data  ;

    DataSender DataSenderService ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_screen);
        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setImageResource(R.drawable.bg);
        gearP = (Button) findViewById(R.id.button_gear_P) ;
        gearR = (Button) findViewById(R.id.button_gear_R) ;
        gearN = (Button) findViewById(R.id.button_gear_N) ;
        gearD = (Button) findViewById(R.id.button_gear_D) ;

        btnSpeed = (Button) findViewById(R.id.Speed_button);
        btnBreak = (Button) findViewById(R.id.Break_button);



        max_distance = getScreenHeight()/2 ;

        btnBreak.setOnTouchListener(ButtonSliderControl);
        btnSpeed.setOnTouchListener(ButtonSliderControl);

        // Get an instance of the SensorManager
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);

        //init
        start_distance = 0 ;
        setActiveGear(gearN);

        DataSenderService = new DataSender();

        DataTransmission = new Thread( DataSenderService );
        DataTransmission.start();

    }

    //Rotation Sensor
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        float aX= event.values[0];
        float aY= event.values[1];
        int angle = (int) (Math.atan2(aY,aX)/(Math.PI/180));

        String header = "t";

        int value = setAngle(angle+90) ;
        DataBlock data = new DataBlock(header, value);


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
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }
    /* Set new Active gear
     *
     */
    public void setActiveGear(View view){
        //Determine Button
        Button btn = (Button) findViewById(view.getId()) ;
        // Reset Active Gear
        reSetActiveGear();
        //set New Active Gear
        this.ActiveGear = btn ;
        btn.setBackgroundResource(R.drawable.active_gear_button) ;

        String str = btn.getText().toString();

    }

    /* set new Active Gear
    *  @param Button
    *  @return void
    **/
    public void setActiveGear(Button btn){
        this.ActiveGear = btn ;
        btn.setBackgroundResource(R.drawable.active_gear_button) ;
    }

    /* reset gear actived
     *
     */
    public void reSetActiveGear(){
        this.ActiveGear.setBackgroundResource(R.drawable.gear_button);
    }


    View.OnTouchListener ButtonSliderControl = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            float currY = 0;
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

    //Trait
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
                value = setVolumnByUI(currY) ;

                break;
            case ACTION_DOWN:
                if(start_distance == 0 ){
                    start_distance = view.getY();
                }
                header = setHeaderValueByView(view);
                value = setVolumnByUI(currY);
                break;
            case ACTION_UP:
                view.setY(start_distance);
                currY = start_distance ;
                header = setHeaderValueByView(view);
                value = setVolumnByUI(currY);
                break;
        }

        DataBlock data = new DataBlock(header, value);
        DataSenderService.sendData(data);

//        Log.d("Log",data.getDataToString());


    }

    int setVolumnByUI(float param){
        int result = (int) (Math.abs(start_distance - param)*100/Math.abs(start_distance - max_distance));
        this.speedValue = result ;

        return result;
    }


    String getSpeedValueToString(){
        return String.valueOf(this.speedValue);
    }
    String getBreakValueToString(){
        return String.valueOf(this.breakValue);
    }
    void SpeedListener(String str){
        SpeedText.setText(str);
    }
    void BreakListener(String str){
        BreakText.setText(str);
    }



}
