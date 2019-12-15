package com.example.littlecar;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.littlecar.VoiceActivity;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;


public class MyRecognizerDialogLister implements RecognizerDialogListener {
	private Context context;
	private Handler handler;

	public MyRecognizerDialogLister(Context context, Handler h) {
		this.context = context;
		handler = h;
	}


	@Override
	public void onResult(RecognizerResult results, boolean isLast) {
		// TODO Auto-generated method stub
		String text = JsonParser.parseIatResult(results.getResultString());
		System.out.println(text);
		Message msg = handler.obtainMessage();
		Bundle bundle = new Bundle();
		if (text.indexOf("前") != -1) {
			text = "前进";
			Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			bundle.putString("text", text);
			msg.setData(bundle);
			handler.sendMessage(msg);
			sendMessage("$1,0,0,0,0,0,0,0,0,0#");
		} else if (text.indexOf("后") != -1) {
			text = "后退";
			Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			bundle.putString("text", text);
			msg.setData(bundle);
			handler.sendMessage(msg);
			sendMessage("$2,0,0,0,0,0,0,0,0,0#");
		} else if (text.indexOf("左") != -1) {
			text ="左转";
			Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			bundle.putString("text", text);
			msg.setData(bundle);
			handler.sendMessage(msg);
			sendMessage("$3,0,0,0,0,0,0,0,0,0#");
		} else if (text.indexOf("右") != -1) {
			text = "右转";
			Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			bundle.putString("text", text);
			msg.setData(bundle);
			handler.sendMessage(msg);
			sendMessage("$4,0,0,0,0,0,0,0,0,0#");
		} else if (text.indexOf("停") != -1) {
			text = "停止ֹ";
			Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			bundle.putString("text", text);
			msg.setData(bundle);
			handler.sendMessage(msg);
			sendMessage("$0,0,0,0,0,0,0,0,0,0#");
		}

	}

	@Override
	public void onError(SpeechError error) {
		// TODO Auto-generated method stub
		int errorCoder = error.getErrorCode();
		switch (errorCoder) {
		case 10118:
			System.out.println("user don't speak anything");
			break;
		case 10204:
			System.out.println("can't connect to internet");
			break;
		default:
			break;
		}
	}

	private void sendMessage(String message) {
        if (LoginActivity.mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            LoginActivity.mChatService.write(send);
        }
    }
}

