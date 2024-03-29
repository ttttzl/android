package com.example.littlecar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import com.iflytek.cloud.speech.DataDownloader;
import com.iflytek.cloud.speech.DataUploader;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class VoiceToWord extends Activity{
	private Context context;
	private Toast mToast;
	private RecognizerDialog iatDialog;
	private SpeechRecognizer iatRecognizer;
	private SharedPreferences mSharedPreferences;
	private RecognizerDialogListener recognizerDialogListener = null;
	private Handler handler;
	
	public VoiceToWord(Context context,String APP_ID) {
		// TODO Auto-generated constructor stub
		this.context = context;
		SpeechUser.getUser().login(context, null, null
						, "appid=" + APP_ID, listener);

		iatDialog =new RecognizerDialog(context);
		mToast = Toast.makeText(context, "", Toast.LENGTH_LONG);

		iatDialog =new RecognizerDialog(context);

		mSharedPreferences = context.getSharedPreferences(context.getPackageName(),MODE_PRIVATE);
	}
	public void setHandler(Handler h){
		handler=h;	
	}
	
	public VoiceToWord(Context context,String APP_ID,RecognizerDialogListener recognizerDialogListener)
	{
		this.context = context;
		SpeechUser.getUser().login(context, null, null
						, "appid=" + APP_ID, listener);

		iatDialog =new RecognizerDialog(context);
		mToast = Toast.makeText(context, "", Toast.LENGTH_LONG);

		iatDialog =new RecognizerDialog(context);

		mSharedPreferences = context.getSharedPreferences(context.getPackageName(),MODE_PRIVATE);
		this.recognizerDialogListener = recognizerDialogListener;
	}
	
	public void GetWordFromVoice()
	{
		boolean isShowDialog = mSharedPreferences.getBoolean("iat_show",true);
		if (isShowDialog) {

			showIatDialog();
		} else {
			if(null == iatRecognizer) {
			iatRecognizer=SpeechRecognizer.createRecognizer(this);
			}
			if(iatRecognizer.isListening()) {
				iatRecognizer.stopListening();
//				((Button) findViewById(android.R.id.button1)).setEnabled(false);
			} else {
			}
		}
	}
	
	
	private void showTip(String str)
	{
		if(!TextUtils.isEmpty(str))
		{
			mToast.setText(str);
			mToast.show();
		}
	}

	public void showIatDialog()
	{
		if(null == iatDialog) {

		iatDialog =new RecognizerDialog(this);
		}


		String engine = mSharedPreferences.getString(
				"iat_engine",
				"iat");
				

		iatDialog.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);

		iatDialog.setParameter(SpeechConstant.DOMAIN, engine);

		String rate = mSharedPreferences.getString(
				"sf",
				"sf");
		if(rate.equals("rate8k"))
		{
			iatDialog.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
		}
		else 
		{
			iatDialog.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
		}
		if(recognizerDialogListener == null)
		{
			getRecognizerDialogListener();
		}

		iatDialog.setListener(recognizerDialogListener);
		iatDialog.show();
	}
	private void getRecognizerDialogListener()
	{

		recognizerDialogListener=new MyRecognizerDialogLister(context,handler);
	}
	

	private SpeechListener listener = new SpeechListener()
	{

		@Override
		public void onData(byte[] arg0) {
		}

		@Override
		public void onCompleted(SpeechError error) {
			if(error != null) {
				System.out.println("user login success");
			}			
		}

		@Override
		public void onEvent(int arg0, Bundle arg1) {
		}		
	};
}
