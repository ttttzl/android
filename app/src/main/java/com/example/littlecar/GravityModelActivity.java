package com.example.littlecar;

import java.net.ServerSocket;
import java.util.Calendar;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GravityModelActivity extends Activity implements SensorEventListener {  
	  
    private static final String TAG = MainActivity.class.getSimpleName();  
    private SensorManager mSensorManager;  
    private Sensor mSensor;   
    private TextView textview4;  
    private Button button1;
    private Button button2;
    String last_s = "停止"; 
    
    private int start;
    private long lasttimestamp = 0;  
    Calendar mCalendar;
	private SurfaceView surfaceView1;
	private ServerSocket serverSocket;  
  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity_model);  
        textview4 = (TextView) findViewById(R.id.myTextView4);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        
		button1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				start = 1;
			}
		});
		
		button2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				start = 0;
			}
		});
		
        start = 0;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY  
        if (null == mSensorManager) {  
            Log.d(TAG, "deveice not support SensorManager");  
        }  
        mSensorManager.registerListener(this, mSensor,  
                SensorManager.SENSOR_DELAY_NORMAL);
        
		surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
		Thread th = new ServerThread(serverSocket, surfaceView1);
		th.start();
		Log.d("test","CREATE");
    }  
    
    @Override
    protected void onStop() {
    	super.onStop();
    	mSensorManager.unregisterListener(this);
    }
    
    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {  
  
    }  
  
    public void onSensorChanged(SensorEvent event) {  
        if (event.sensor == null) {  
            return;  
        }  
  
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {  
            int x = (int) event.values[0];  
            int y = (int) event.values[1];  
            int z = (int) event.values[2];
            
            if (start == 0)
            	x = y = z = 0;
            
            mCalendar = Calendar.getInstance();  
            long stamp = mCalendar.getTimeInMillis() / 1000l; 
            if ((stamp - lasttimestamp) > 0) {  
                lasttimestamp = stamp;  
                String s = "";
                if(x > 0)
                	s += "向左";
                	else if(x < 0)
                		s += "向右";
                if(y < 0)
                	s += "前进";
                	else if(y > 0)
                		s += "后退";
                if(s == "")
                	s = "停止";
                textview4.setText("当前状态:"+s);  
                
                if (last_s==s) 
                	return;
                if (s.compareTo("前进")==0)
                	//sendMessage("A");
                    sendMessage("$1,0,0,0,0,0,0,0,0,0#");
                if (s.compareTo("向左")==0){
                	//sendMessage("C0.");
                	//sendMessage("L");
                    sendMessage("$3,0,0,0,0,0,0,0,0,0#");}
                if (s.compareTo("向右")==0){
                	//sendMessage("C0.");
                	//sendMessage("R");
                    sendMessage("$4,0,0,0,0,0,0,0,0,0#");}
                if (s.compareTo("后退")==0)
                	//sendMessage("B");
                    sendMessage("$2,0,0,0,0,0,0,0,0,0#");
                if (s.compareTo("停止")==0)
                	//sendMessage("P");
                    sendMessage("$0,0,0,0,0,0,0,0,0,0#");
                if (s.indexOf("前进")!=-1 & s.indexOf("向左")!=-1){
                    sendMessage("$1,0,0,0,0,0,0,0,0,0#");
                    sendMessage("$3,0,0,0,0,0,0,0,0,0#");}
                if (s.indexOf("前进")!=-1 & s.indexOf("向右")!=-1){
                    sendMessage("$1,0,0,0,0,0,0,0,0,0#");
                    sendMessage("$4,0,0,0,0,0,0,0,0,0#");}
                if (s.indexOf("后退")!=-1 & s.indexOf("向左")!=-1){
                    sendMessage("$2,0,0,0,0,0,0,0,0,0#");
                    sendMessage("$3,0,0,0,0,0,0,0,0,0#");}
                if (s.indexOf("后退")!=-1 & s.indexOf("向右")!=-1){
                    sendMessage("$2,0,0,0,0,0,0,0,0,0#");
                    sendMessage("$4,0,0,0,0,0,0,0,0,0#");
                }
                last_s = s;
            }  
 
            	
        }  
        
    }  
    
    private void sendMessage(String message) {
        if (LoginActivity.mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            LoginActivity.mChatService.write(send);
        }
    }
}
