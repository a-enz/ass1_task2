package ch.ethz.inf.vs.a1.aenz.antitheft;

import java.net.ContentHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AntiTheftServiceImpl extends AbstractAntiTheftService {

	public static final String ACTIVITY_TAG = "### AntiTheft ###";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		
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
		MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);
		mp.setVolume(0.1f, 0.1f);
		mp.setLooping(true);
		mp.start();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
