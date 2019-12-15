package com.example.littlecar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button1 = (Button) findViewById(R.id.Button01);
		Button button2 = (Button) findViewById(R.id.Button02);
		Button button3 = (Button) findViewById(R.id.Button03);
		Button button4 = (Button) findViewById(R.id.Button04);
		Button button6 = (Button) findViewById(R.id.Button06);
		Button button7 = (Button) findViewById(R.id.Button07);
		button1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, BasicModelActivity.class);
				startActivity(intent);
			}
		});
		button2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, GravityModelActivity.class);
				startActivity(intent);
			}
		});
		button3.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, RoadConditionActivity.class);
				startActivity(intent);
			}
		});
		button4.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		button6.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, TouchModelActivity.class);
				startActivity(intent);
			}
		});
		button7.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, VoiceActivity.class);
				startActivity(intent);
			}
		});
	}


}
