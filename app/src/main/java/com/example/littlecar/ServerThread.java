package com.example.littlecar;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ServerThread extends Thread {
	private ServerSocket serverSocket;
	private Canvas canvas;
	private Bitmap btp;
	private Matrix matrix;
	private SurfaceView surfaceView1;
	private SurfaceHolder surfaceHolder;

	public ServerThread(ServerSocket ss, SurfaceView sv) {
		surfaceView1 = sv;
		serverSocket = ss;
		matrix = new Matrix();
		matrix.reset();
		matrix.postRotate(90);
		LayoutParams params = surfaceView1.getLayoutParams();
		params.width = 480;
		params.height = 640;
		surfaceView1.setLayoutParams(params);
		surfaceHolder = surfaceView1.getHolder();
		surfaceHolder.addCallback(new Callback() {
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}

			public void surfaceCreated(SurfaceHolder holder) {
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				if (serverSocket != null)
					try {
						serverSocket.close();
						serverSocket = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		});
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		try {
			serverSocket = new ServerSocket(6000);
			SocketAddress address = null;
			if (!serverSocket.isBound())
				serverSocket.bind(address, 6000);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public void run() {
		try {
			while (true) {
				Socket s = serverSocket.accept();
				if (s != null) {
					InputStream in = s.getInputStream();
					btp = BitmapFactory.decodeStream(in);
					in.close();
					s.close();
					btp = Bitmap.createBitmap(btp, 0, 0, btp.getWidth(),
							btp.getHeight(), matrix, true);
					canvas = surfaceHolder.lockCanvas();
					canvas.drawBitmap(btp, 0, 0, null);
					surfaceHolder.unlockCanvasAndPost(canvas);
					btp.recycle();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}