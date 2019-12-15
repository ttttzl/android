package com.example.littlecar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

class LFFragmentPagerAdapter extends FragmentPagerAdapter {
	Fragment[] fragmentArray;

	public LFFragmentPagerAdapter(FragmentManager fm, Fragment[] fragmentArray2) {
		super(fm);
		if (null == fragmentArray2) {
			this.fragmentArray = new Fragment[] {};
		} else {
			this.fragmentArray = fragmentArray2;
		}
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragmentArray[arg0];
	}

	@Override
	public int getCount() {
		return fragmentArray.length;
	}

}

class FirstFragment extends Fragment {
	private EditText editText1;
	private Button button2;
	private TextView textView2;
	private SurfaceView surfaceView1;
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private boolean isPreview = false;
	private boolean isConnect = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_first, container,
				false);
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		editText1 = (EditText) getView().findViewById(R.id.editText1);
		button2 = (Button) getView().findViewById(R.id.button2);
		button2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!isConnect) {
					camera.startPreview();
					camera.setPreviewCallback(new StreamIt(editText1.getText()
							.toString()));
					isConnect = true;
					textView2.setText("已连接");
					button2.setText("停止传输");
				} else {
					camera.stopPreview();
					camera.setPreviewCallback(null);
					isConnect = false;
					textView2.setText("未连接");
					button2.setText("传输图像");
				}
			}
		});
		textView2 = (TextView) getView().findViewById(R.id.textView2);

		surfaceView1 = (SurfaceView) getView().findViewById(R.id.surfaceView1);
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
				initCamera();
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				if (camera != null) {
					camera.setPreviewCallback(null);
					camera.stopPreview();
					camera.release();
					camera = null;
				}
			}
		});
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private void initCamera() {
		if (!isPreview)
			camera = Camera.open();
		if (camera != null && !isPreview) {
			try {
				Camera.Parameters parameters = camera.getParameters();
				parameters.setPreviewSize(640, 480);
				parameters.setPictureFormat(ImageFormat.NV21);
				parameters.setPictureSize(640, 480);
				camera.setParameters(parameters);
				camera.setDisplayOrientation(90);
				camera.setPreviewDisplay(surfaceHolder);
			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
		}
	}

	private class StreamIt implements Camera.PreviewCallback {
		private String ipname;
		private long lastTime;

		public StreamIt(String ip) {
			ipname = ip;
			lastTime = -1;
		}

		public void onPreviewFrame(byte[] data, Camera camera) {
			long curTime = (new Date(System.currentTimeMillis())).getTime();
			if (curTime - lastTime < 100)
				return;
			else
				lastTime = curTime;
			Size size = camera.getParameters().getPreviewSize();
			try {
				YuvImage image = new YuvImage(data, ImageFormat.NV21,
						size.width, size.height, null);
				if (image != null) {
					ByteArrayOutputStream outstream = new ByteArrayOutputStream();
					image.compressToJpeg(
							new Rect(0, 0, size.width, size.height), 80,
							outstream);
					outstream.flush();
					Thread th = new ClientThread(outstream, ipname);
					th.start();
				}
			} catch (Exception ex) {
				Log.e("Sys", "Error:" + ex.getMessage());
			}
		}
	}

	class ClientThread extends Thread {
		private byte byteBuffer[] = new byte[1024];
		private OutputStream outsocket;
		private ByteArrayOutputStream myoutputstream;
		private String ipname;

		public ClientThread(ByteArrayOutputStream myoutputstream, String ipname) {
			this.myoutputstream = myoutputstream;
			this.ipname = ipname;
			try {
				myoutputstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				Socket tempSocket = new Socket(ipname, 6000);
				if (tempSocket != null) {
					outsocket = tempSocket.getOutputStream();
					if (outsocket != null) {
						ByteArrayInputStream inputstream = new ByteArrayInputStream(
								myoutputstream.toByteArray());
						int amount;
						while ((amount = inputstream.read(byteBuffer)) != -1) {
							outsocket.write(byteBuffer, 0, amount);
						}
						outsocket.close();
					}
					myoutputstream.flush();
					myoutputstream.close();
					tempSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class SecondFragment extends Fragment {
	private TextView textView1;
	private TextView textView2;
	private Button button1;
	private SurfaceView surfaceView1;
	private ServerSocket serverSocket;
	private boolean isConnect = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_second, container,
				false);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		textView1 = (TextView) getView().findViewById(R.id.textView1);
		textView2 = (TextView) getView().findViewById(R.id.textView2);

		surfaceView1 = (SurfaceView) getView().findViewById(R.id.surfaceView1);

		button1 = (Button) getView().findViewById(R.id.button1);
		button1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!isConnect) {
					textView1.setText("可用IP\n" + getLocalIpAddress());
					textView2.setText("已打开连接");
					button1.setText("停止连接");
					isConnect = true;
					Thread th = new ServerThread(serverSocket, surfaceView1);
					th.start();

				} else {
					try {
						serverSocket.close();
						serverSocket = null;
						isConnect = false;
						textView1.setText("可用IP:");
						textView2.setText("已关闭连接");
						button1.setText("开始连接");
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		});
	}

	private String getLocalIpAddress() {
		String recvMessageServer = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					{
						if (inetAddress.getHostAddress().indexOf(".") != -1) {
							recvMessageServer += "请连接IP"
									+ inetAddress.getHostAddress() + "\n";
						}
					}
				}
			}
		} catch (SocketException ex) {
			recvMessageServer = "获取IP地址异常" + "\n";
		}
		return recvMessageServer;
	}

}

public class RoadConditionActivity extends FragmentActivity {

	private ViewPager viewPager;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_road_condition);
		setTitle("实时路况");
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		Fragment fragment1 = new FirstFragment();
		Fragment fragment2 = new SecondFragment();
		Fragment[] fragmentArray = new Fragment[] { fragment1, fragment2 };
		LFFragmentPagerAdapter adapter = new LFFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentArray);

		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int arg0) {
				System.out.println("arg0:" + arg0);
				actionBar.setSelectedNavigationItem(arg0);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			public void onPageScrollStateChanged(int arg0) {

			}
		});

		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tab1 = actionBar.newTab().setText("发送端")
				.setIcon(android.R.drawable.ic_menu_agenda)
				.setTabListener(new ActionTabListener(fragment1));

		Tab tab2 = actionBar.newTab().setText("接收端")
				.setIcon(android.R.drawable.ic_menu_agenda)
				.setTabListener(new ActionTabListener(fragment2));

		actionBar.addTab(tab1);
		actionBar.addTab(tab2);

	}

	class ActionTabListener implements ActionBar.TabListener {

		private Fragment fragment;

		public ActionTabListener(Fragment fragment) {
			this.fragment = fragment;
		}

		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {

		}

		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			viewPager.setCurrentItem(tab.getPosition());
		}

		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {

		}

	}

}
