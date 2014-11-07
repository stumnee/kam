package com.example.kam;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final static String DEBUG_TAG = "MakePhotoActivity";
	private Camera camera;
	private int cameraId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
		} else {
			cameraId = findFrontFacingCamera();
			if (cameraId < 0) {
				Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
			} else {
				takePictureContinuously();
			}
		}
	}
	
	public void onStart(View view) throws IOException {
		if (cameraId >= 0) {
			camera = Camera.open(cameraId);
			
			
			//((Button)findViewById(R.id.stopCapture)).setVisibility(View.VISIBLE);
			//((Button)findViewById(R.id.startCapture)).setVisibility(View.INVISIBLE);
		}
	}
	
	public void onStop(View view) {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
			//((Button)findViewById(R.id.stopCapture)).setVisibility(View.INVISIBLE);
			//((Button)findViewById(R.id.startCapture)).setVisibility(View.VISIBLE);
		}
	}
	
	private void takePicture() throws Exception {
		if(camera == null)
		{
			//Toast.makeText(getApplicationContext(), "No camera", Toast.LENGTH_LONG).show();
		} else {
			Log.d("take pic", camera.toString());
			
			SurfaceTexture surfaceTexture = new SurfaceTexture(10);
			
			camera.setPreviewTexture(surfaceTexture);
			
		    camera.startPreview();
			camera.takePicture(null, null,null, new PhotoHandler(getApplicationContext()));
			
		}
	}
	
	private void takePictureContinuously () {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				try {
					if (camera != null) {
						takePicture();
					}
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
			}
		}, 0, 1000);
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		
		int numberOfCameras = Camera.getNumberOfCameras();
		
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo cameraInfo = new CameraInfo();
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
				Log.d(DEBUG_TAG, "Camera found " + i);
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}
	
	@Override
	protected void onPause() {
		Log.d(DEBUG_TAG, "pause");
		if (camera != null) {
			//camera.release();
			//camera = null;
		}
		super.onPause();
	}
}
