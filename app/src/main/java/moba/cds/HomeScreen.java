package moba.cds;

import android.app.Activity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;

public class HomeScreen extends Activity {

    Button btnSearch ;

    TextView txt_response ;


//    class Connect extends AsyncTask<Void, Void, Void> {
//
//        SocketThread server = new SocketThread() ;
//        boolean exist = false ;
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                Socket socket = new Socket(server.getServerAddr(), server.getServerPort());
//                socket.setSoTimeout(500);
//                if(socket.isConnected()){
//                    Log.d("SocketConnection", "Connect");
//                    socket.close();
//                    exist = true ;
//                }else{
//                    exist = false ;
//                }
//
//            }catch (IOException e){
//                Log.d("Socket",e.getMessage());
//            }
//            return null ;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if(exist){
//                Intent i = new Intent(HomeScreen.this, ControlScreen.class);
//
//                startActivity(i);
//
//            }else{
//                txt_response.setText("Connection Failure");
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        txt_response = (TextView)findViewById(R.id.txt_response) ;
        btnSearch= (Button)findViewById(R.id.btn_connect_to_server);



//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//
//            public void onClick(View view) {
//
//                Connect task = new Connect() ;
//                task.execute();
//
//            }
//        });
    }


}


