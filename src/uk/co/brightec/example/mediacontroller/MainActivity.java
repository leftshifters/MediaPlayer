package uk.co.brightec.example.mediacontroller;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.MediaController.MediaPlayerControl;

public class MainActivity extends Activity implements SurfaceHolder.Callback,
		MediaPlayer.OnPreparedListener {
	private static final String TAG = "Main";
	private SurfaceHolder holder;
	private MediaPlayer mediaPlayer;
	private SurfaceView surfaceView;
	private int currentTime = 0;
	private CheckBox playPause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_main);
		surfaceView = (SurfaceView) findViewById(R.id.surface);
		playPause = (CheckBox) findViewById(R.id.playPause);
		holder = surfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(this);
		String path = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
		try {
			mediaPlayer.setDataSource(path);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mediaPlayer.prepareAsync();

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i(TAG, "surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surface created");
		mediaPlayer.setDisplay(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surface destroyed");
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// Get the dimensions of the video
		int videoWidth = mp.getVideoWidth();
		int videoHeight = mp.getVideoHeight();

		// Get the width of the screen
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		Log.i(TAG, "screen width " + screenWidth);

		// Get the SurfaceView layout parameters
		android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

		// Set the width of the SurfaceView to the width of the screen
		lp.width = screenWidth;

		// Set the height of the SurfaceView to match the aspect ratio of the
		// video
		// be sure to cast these as floats otherwise the calculation will likely
		// be 0
		lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);

		// Commit the layout parameters
		surfaceView.setLayoutParams(lp);
		play();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "current time on resume " + currentTime);
		super.onResume();
	}

	@Override
	protected void onPause() {
		pause();
		super.onPause();
	}

	private void play() {
		playPause.setEnabled(true);
		playPause.setChecked(true);
		mediaPlayer.seekTo(currentTime);
		mediaPlayer.start();
	}

	private void pause() {
		if (mediaPlayer.isPlaying()) {
			playPause.setChecked(false);
			currentTime = mediaPlayer.getCurrentPosition();
			Log.i(TAG, "current time on pause " + currentTime);
			mediaPlayer.pause();

		}
	}

	public void playPause(View view) {
		CheckBox playPause = (CheckBox) view;
		if (playPause.isChecked()) {
			try {
				play();
			} catch (IllegalStateException e) {
				Log.e(TAG, "Illegal State Exception", e);
			}
		} else {
			if (mediaPlayer.isPlaying()) {
				pause();
			}
		}
	}

	@Override
	protected void onDestroy() {
		try {
			mediaPlayer.release();
			mediaPlayer = null;
		} catch (Exception e) {
		}
		super.onDestroy();
	}
}
