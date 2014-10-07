package ch.ethz.inf.vs.a1.aenz.antitheft;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AntiTheftServiceImpl extends AbstractAntiTheftService{

	public static final String ACTIVITY_TAG = "### AntiTheft ###";
	
	private MediaPlayer mp;
	
	private SensorManager sensMan;
	//private MotionListener motionListener;
	private Sensor sSensor;
	private Sensor aSensor;
	private Handler handler;
	private boolean codeRed;
	private List<Float> dataSamples;
	private AbstractAntiTheftService thisLis;
	
	private AbstractMovementDetector aMotionListener = new AbstractMovementDetector() {

		
//		@Override
//		public void onAccuracyChanged(Sensor arg0, int arg1) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void onSensorChanged(SensorEvent event) {
//			float[] data = event.values;
//			float amp;
//			if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//				amp = data[0]*data[0] + data[1]*data[1] + data[2]*data[2];
//				dataSamples.add(amp);
//			}
//		}

		@Override
		protected boolean doAlarmLogic(float[] values) {
			return values[0]*values[0] + values[1]*values[1] + values[2]*values[2] > 100;
		}
		
	};
	
	private TriggerEventListener sMotionListener = new TriggerEventListener() {
		public void onTrigger(TriggerEvent event) {
			if(!codeRed && event.sensor.getType() == Sensor.TYPE_SIGNIFICANT_MOTION) {
				
				codeRed = true;
				handler = new Handler();
				dataSamples = new ArrayList<Float>();
				aSensor = sensMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				aMotionListener.setCallbackService(thisLis);
				sensMan.registerListener(aMotionListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						sensMan.unregisterListener(aMotionListener);
						codeRed = false;
					}
					
				}, 5000);
			}
		}
	};
	


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		sensMan = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sSensor = sensMan.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
//		motionListener = new MotionListener();
		thisLis = this;
		sensMan.requestTriggerSensor(sMotionListener, sSensor);
		
		Log.d(ACTIVITY_TAG, "onStart");

		Intent goToIntent = new Intent(this, MainActivity.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(goToIntent);
		
		PendingIntent goToPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		
		
		Log.d(ACTIVITY_TAG, "Builder done");

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("Theft Alarm");
		mBuilder.setContentText("it appears i'm beeing stolen");
		mBuilder.setContentIntent(goToPendingIntent);
		
		
		long[] pattern = {0,500};
		mBuilder.setVibrate(pattern);
		
		
		
		int nID = 007;
		Notification note = mBuilder.build();
		note.flags |= Notification.FLAG_NO_CLEAR;
		note.flags |= Notification.FLAG_FOREGROUND_SERVICE;

		Log.d(ACTIVITY_TAG, "flags Set");

		//startForeground(nID, note);

		Log.d(ACTIVITY_TAG, "foreground");
		
		
		/*
		NotificationManager nMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nMan.notify(nID, note);
		*/
		
		return START_REDELIVER_INTENT;
	}

	@Override
	public void startAlarm() {
		if(mp == null) {
			mp = MediaPlayer.create(this, R.raw.alarm);
			mp.setVolume(1.0f, 1.0f);
			mp.setLooping(true);
			mp.start();
		}
	}
	
	@Override
	public void onDestroy() {
		if(mp != null && mp.isPlaying()) {
			mp.stop();
			mp.release();
		}
		try {sensMan.cancelTriggerSensor(sMotionListener, sSensor);} catch (Exception e){};
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public float[] box(List<Float> arg){
		float[] res = new float[0];
		int i = 0;
		if(arg != null) {
			res = new float[arg.size()];
			for(Float f : arg) {
				res[i] = f;
				i++;
			}
		}
		return res;
	}

}
