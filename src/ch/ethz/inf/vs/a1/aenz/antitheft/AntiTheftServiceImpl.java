package ch.ethz.inf.vs.a1.aenz.antitheft;

import java.net.ContentHandler;

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
	
	private TriggerEventListener sMotionListener = new TriggerEventListener() {
		public void onTrigger(TriggerEvent event) {
			if(event.sensor.getType() == Sensor.TYPE_SIGNIFICANT_MOTION) {
				startAlarm();
				
			}
			
		}
	};
	
	private SensorEventListener aMotionListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		sensMan = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sSensor = sensMan.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
//		motionListener = new MotionListener();
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
		mp = MediaPlayer.create(this, R.raw.alarm);
		mp.setVolume(0.005f, 0.005f);
		mp.setLooping(true);
		mp.start();
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

}
