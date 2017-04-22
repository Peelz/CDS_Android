package moba.cds;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import constants.Constant;

public class HomeScreen extends Activity {

    String TAG = "HomeScreen";

    Button btnSearch ;

    TextView txtResponse ;
    private EditText IpText;

    class ConnectToServer extends AsyncTask<Void, Void, Void> {

        Socket server = null ;// new Socket(Constant.HOST, 7769) ;
        boolean exist = false ;
        Activity context ;

        public ConnectToServer(Activity context){

            this.context = context ;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "Doing background");
            try {
                Socket socket =  new Socket() ;
                InetSocketAddress mInet = new InetSocketAddress(Constant.HOST, 7769);
                socket.connect(mInet, 500);

                if(socket.isConnected()){
                    Log.d(TAG,"Socket connected");
                    socket.close();
                    exist = true ;
                }else{
                    exist = false ;
                }

            }catch (IOException e){
                Log.d(TAG,"Server Not Found");
            }
            return null ;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(exist){
                Intent intent = new Intent(context, ControlScreen.class);
                startActivity(intent);

            }else{
                txtResponse.setText("Connection Failure");
                Toast.makeText(context, "Host not found, please check your device ", Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        txtResponse = (TextView)findViewById(R.id.txt_response) ;
        IpText = (EditText)findViewById(R.id.ip_text) ;
        btnSearch= (Button)findViewById(R.id.btn_connect_to_server);


        IpText.setText(Constant.HOST);
        btnSearch.setOnClickListener(view ->
                new  ConnectToServer(HomeScreen.this).execute()
        );

        IpText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d(TAG, "Text Change");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Constant.HOST = s.toString() ;
//                Log.d(TAG, "Change host ot "+Constant.HOST);

            }
        });
    }


}


