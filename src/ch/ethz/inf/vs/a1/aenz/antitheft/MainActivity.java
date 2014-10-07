package ch.ethz.inf.vs.a1.aenz.antitheft;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.LocalServerSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;


public class MainActivity extends ActionBarActivity {
	
	private static final String ACTIVITY_TAG = "### Main ###";
	private NotificationManager mNM;
	int mId = 007;
	ToggleButton tb;
	private SharedPreferences mPrefs;
	final String PREF_NAME = "preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tb = (ToggleButton) findViewById(R.id.btn_toggleAlarm);

        tb.setTextOff(this.getString(R.string.toggle_stopped));;
        tb.setTextOn(this.getString(R.string.toggle_running));
        
        //initialize state of toggle button/application

	    mPrefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
   		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean("servicesRunning", false);
		editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    }
    
    @Override
    public void onResume(){
    	super.onResume();

	    mPrefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
	    boolean tgpref = mPrefs.getBoolean("servicesRunning", false);
	    tb = (ToggleButton) findViewById(R.id.btn_toggleAlarm);
		
		Log.d(ACTIVITY_TAG, "resuming....");
		
	    if(tgpref) {
			Log.d(ACTIVITY_TAG, "Hey I am on");
	    	tb.setChecked(true);
	    }
	    else {
			Log.d(ACTIVITY_TAG, "What? I am out");
	    	tb.setChecked(false);
	    }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
    public void onClickActivate(View v){
    	tb = (ToggleButton) v;
    	
    	if(tb.isChecked()) {
    		
    		Intent bgAntiTheft = new Intent(this,AntiTheftServiceImpl.class);
    		
    		this.startService(bgAntiTheft);
    		
    		//send notification
    		
    		NotificationCompat.Builder mBuilder =
    		        new NotificationCompat.Builder(this)
    		        .setSmallIcon(android.R.drawable.ic_dialog_alert)
    		        .setContentTitle("AntiTheft")
    		        .setContentText("This device is protected");
    		// Creates an explicit intent for an Activity in your app
    		Intent resultIntent = new Intent(this, MainActivity.class);

    		// The stack builder object will contain an artificial back stack for the
    		// started Activity.
    		// This ensures that navigating backward from the Activity leads out of
    		// your application to the Home screen.
    		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    		// Adds the back stack for the Intent (but not the Intent itself)
    		stackBuilder.addParentStack(MainActivity.class);
    		// Adds the Intent that starts the Activity to the top of the stack
    		stackBuilder.addNextIntent(resultIntent);
    		PendingIntent resultPendingIntent =
    		        stackBuilder.getPendingIntent(
    		            0,
    		            PendingIntent.FLAG_UPDATE_CURRENT
    		        );
    		mBuilder.setContentIntent(resultPendingIntent);

    		
    		mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    		// mId allows you to update the notification later on.
    		Notification note = mBuilder.build();
    		note.flags |= Notification.FLAG_NO_CLEAR;
    		note.flags |= Notification.FLAG_ONGOING_EVENT;
    		
    		mNM.notify(mId, note);
    		
    		//safe state of toggle button

    		Log.d(ACTIVITY_TAG, "We are setting the servicesRunning to TRUE");
    	    mPrefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
    		SharedPreferences.Editor editor = mPrefs.edit();
    		editor.putBoolean("servicesRunning", true);
    		editor.commit();
    		Log.d(ACTIVITY_TAG, "The persistent boolean is now TRUE");
    		
    	} else {
    		
    		mNM.cancel(mId);
    		
    		//safe state of toggle button

    	    mPrefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
       		SharedPreferences.Editor editor = mPrefs.edit();
    		editor.putBoolean("servicesRunning", false);
    		editor.commit();
    	}
    }
}
