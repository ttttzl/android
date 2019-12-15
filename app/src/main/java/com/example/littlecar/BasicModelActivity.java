package com.example.littlecar;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BasicModelActivity extends Activity {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private TextView textView2;
	private SurfaceView surfaceView1;
	private ServerSocket serverSocket;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic_model);
		
		button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        textView2 = (TextView) findViewById(R.id.textView2);
        
		button1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				sendMessage("$1,0,0,0,0,0,0,0,0,0#");
				textView2.setText("\n前进");
			}
		});
		
		button2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				sendMessage("$3,0,0,0,0,0,0,0,0,0#");
				//sdMessage("L");
				textView2.setText("\n左转");
			}
		});
		
		button3.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				sendMessage("$0,0,0,0,0,0,0,0,0,0#");
				textView2.setText("\n停止ֹ");
			}
		});
		
		button4.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				sendMessage("$4,0,0,0,0,0,0,0,0,0#");
				//sdMessage("R");
				textView2.setText("\n右转");
			}
		});
		
		button5.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				sendMessage("$2,0,0,0,0,0,0,0,0,0#");
				textView2.setText("\n后退");
			}
		});
		
		surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
		Thread th = new ServerThread(serverSocket, surfaceView1);
		th.start();
	}
	
    private void sendMessage(String message) {
        if (LoginActivity.mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            LoginActivity.mChatService.write(send);
        }
    }
}