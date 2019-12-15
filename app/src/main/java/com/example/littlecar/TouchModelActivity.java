package com.example.littlecar;

import java.net.ServerSocket;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

class BallSurfaceView extends SurfaceView
implements SurfaceHolder.Callback{
	private Bitmap ballBitmap;
	private Bitmap circleBitmap;
	private Rect ballBitmapSrc;
	private Rect circleBitmapSrc;
	private int ballR, ballX, ballY, circleR;
	private SurfaceHolder holder;
	private Handler handler = null;
	public int midW;
	public int midH;
	public BallSurfaceView(Context context, AttributeSet attrs) {
		super(context,attrs);
		setZOrderOnTop(true);   
		holder = this.getHolder();
		holder.setFormat(PixelFormat.TRANSLUCENT); 
		holder.addCallback(this);
		ballBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ball);
		circleBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.circle);
		ballR = 50;
		circleR = 200;
		ballBitmapSrc = new Rect(0, 0, ballBitmap.getWidth(), ballBitmap.getHeight());
		circleBitmapSrc = new Rect(0, 0, circleBitmap.getWidth(), circleBitmap.getHeight());
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		RectF dst;
		if(canvas == null) canvas = holder.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);  
		dst = new RectF(midW - circleR,midH - circleR,midW + circleR,midH + circleR);
		canvas.drawBitmap(circleBitmap,circleBitmapSrc,dst,null);		
		dst = new RectF(ballX - ballR,ballY - ballR,ballX + ballR,ballY + ballR);
		canvas.drawBitmap(ballBitmap,ballBitmapSrc,dst,null);
		holder.unlockCanvasAndPost(canvas);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		new RefreshThread().start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		ballX = midW = w / 2;
		ballY = midH = h / 2;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		double len = 0;
		double maxlen = Math.sqrt((circleR-ballR)*(circleR-ballR));
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			ballX = (int) event.getX();
			ballY = (int) event.getY();
			len = Math.sqrt((ballX-midW)*(ballX-midW)+(ballY-midH)*(ballY-midH));
			if (len > maxlen){
				ballX = midW + (int) ((ballX - midW) / len * maxlen);
				ballY = midH + (int) ((ballY - midH) / len * maxlen);
				len = maxlen;
			}
			break;
		case MotionEvent.ACTION_UP:
			ballX = midW;
			ballY = midH;
			len = 0;
			break;
		}
		if (handler != null){
			Message msg = handler.obtainMessage();
	        Bundle bundle = new Bundle();
	        bundle.putString("X", Integer.toString(ballX - midW));
	        bundle.putString("Y", Integer.toString(ballY - midH));
	        bundle.putString("Percent", Double.toString(len / maxlen));
	        msg.setData(bundle);
	        handler.sendMessage(msg);	
		}
        return true;
	}
	
	private class RefreshThread extends Thread{

		@Override
		public void run() { 
			while(true){
				Canvas canvas = null;
				try{
					onDraw(canvas);
				}catch(Exception e){
					e.printStackTrace();
				}                                    
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public void setHandler(Handler h){
		handler = h;
	}
        
}

public class TouchModelActivity extends Activity {
	
    private BallSurfaceView bsv ;
	private TextView textView1;
	protected int x;
	protected int y;
	protected double percent;
	private SurfaceView surfaceView1;
	private ServerSocket serverSocket; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touch_model);
        bsv = (BallSurfaceView) findViewById(R.id.ballSurfaceView);
        bsv.setHandler(mHandler);
        textView1 = (TextView) findViewById(R.id.textView1);
        
		surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
		Thread th = new ServerThread(serverSocket, surfaceView1);
		th.start();
	}
	
    private void sdMessage(String message) {
        if (LoginActivity.mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            LoginActivity.mChatService.write(send);
        }
    }

    private final Handler mHandler = new Handler() {

		private Calendar mCalendar;
		private long lasttimestamp = 0;
		private String last_s = "ֹͣ";

		@Override
        public void handleMessage(Message msg) {
        	x = Integer.parseInt(msg.getData().getString("X"));
        	y = Integer.parseInt(msg.getData().getString("Y"));
        	percent = Double.parseDouble(msg.getData().getString("Percent"));
        	int per = (int) (percent * 100);
        	int angle = (int)(Math.abs(y) / Math.sqrt(x * x + y * y) * 100);

        	mCalendar = Calendar.getInstance();  
            long stamp = mCalendar.getTimeInMillis() / 1000l; 
            if ((stamp - lasttimestamp ) > 0) {  
                lasttimestamp = stamp;  
                String s = "";
                if (per < 25)
                	s = "停止";
                else if (y < 0 && angle > 75 && angle <= 100)
                	s = "前进";
                else if (y >= 0 && angle > 75 && angle <= 100)
                   	s = "后退";
                else if (x < 0 && angle >= 0 && angle < 25)
                    s = "向左";
                else if (x >= 0 && angle >= 0 && angle < 25)
                    s = "向右";
                else if (x < 0 && y < 0)
                    s = "向左前进";
                else if (x >= 0 && y < 0)
                    s = "向右前进";
                else if (x < 0 && y >= 0)
                    s = "向左后退";
                else if (x >= 0 && y >= 0)
                    s = "向右后退";
               
                
                textView1.setText("当前状态："+s);

				if (last_s==s)
					return;
				if (s.compareTo("前进")==0)
					//sendMessage("A");
					sdMessage("$1,0,0,0,0,0,0,0,0,0#");
				if (s.compareTo("向左")==0){
					//sendMessage("C0.");
					//sendMessage("L");
					sdMessage("$3,0,0,0,0,0,0,0,0,0#");}
				if (s.compareTo("向右")==0){
					//sendMessage("C0.");
					//sendMessage("R");
					sdMessage("$4,0,0,0,0,0,0,0,0,0#");}
				if (s.compareTo("后退")==0)
					//sendMessage("B");
					sdMessage("$2,0,0,0,0,0,0,0,0,0#");
				if (s.compareTo("停止")==0)
					//sendMessage("P");
					sdMessage("$0,0,0,0,0,0,0,0,0,0#");
				if (s.indexOf("前进")!=-1 & s.indexOf("向左")!=-1){
					sdMessage("$3,0,0,0,0,0,0,0,0,0#");
					sdMessage("$1,0,0,0,0,0,0,0,0,0#");}
				if (s.indexOf("前进")!=-1 & s.indexOf("向右")!=-1){
					sdMessage("$4,0,0,0,0,0,0,0,0,0#");
					sdMessage("$1,0,0,0,0,0,0,0,0,0#");}
				if (s.indexOf("后退")!=-1 & s.indexOf("向左")!=-1){
					sdMessage("$3,0,0,0,0,0,0,0,0,0#");
					sdMessage("$2,0,0,0,0,0,0,0,0,0#");}
				if (s.indexOf("后退")!=-1 & s.indexOf("向右")!=-1){
					sdMessage("$4,0,0,0,0,0,0,0,0,0#");
					sdMessage("$2,0,0,0,0,0,0,0,0,0#");}
                last_s  = s;
            }  
		}
    };
}



