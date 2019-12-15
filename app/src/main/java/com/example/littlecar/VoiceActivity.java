package com.example.littlecar;

import java.net.ServerSocket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.littlecar.VoiceToWord;

public class VoiceActivity extends Activity{
	Button but = null;
	TextView textview;
	private SurfaceView surfaceView1;
	private ServerSocket serverSocket;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_mode);
		but = (Button) findViewById(R.id.button1);
		textview = (TextView) findViewById(R.id.textView2);
		but.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				VoiceToWord voice = new VoiceToWord(VoiceActivity.this,"534e3fe2");
				voice.setHandler(mHandler);
				voice.GetWordFromVoice();
			}
		});
		surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
		Thread th = new ServerThread(serverSocket, surfaceView1);
		th.start();
	}
    private final Handler mHandler = new Handler() {

		@Override
        public void handleMessage(Message msg) {
        	textview.setText("当前状态"+msg.getData().getString("text"));
        }
    };
}