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
	private Intent bgAntiTheft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tb = (ToggleButton) findViewById(R.id.btn_toggleAlarm);

        tb.setTextOff(this.getString(R.string.toggle_stopped));
        tb.setTextOn(this.getString(R.string.toggle_running));
        
        
		mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //initialize state of toggle button/application

	    mPrefs = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
   		SharedPreferences.Editor editor = mPrefs.edit();
   		if (mPrefs.contains("servicesRunning")) {
   			// do nothing
   		} else {
   			Log.d(ACTIVITY_TAG, "Initial persistance set!");
   			editor.putBoolean("servicesRunning", false);
   			editor.commit();
   		}
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

	    boolean tgpref = mPrefs.getBoolean("servicesRunning", true);
	    tb = (ToggleButton) findViewById(R.id.btn_toggleAlarm);
		
		Log.d(ACTIVITY_TAG, "resuming....");
		Boolean tmp = mPrefs.contains("servicesRunning");
		Log.d(ACTIVITY_TAG, "was the boolean found? " + tmp.toString());
		
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
		bgAntiTheft = new Intent(this,AntiTheftServiceImpl.class);
    	
    	if(tb.isChecked()) {
    		
    		
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

    		
    		// mId allows you to update the notification later on.
    		Notification note = mBuilder.build();
    		//note.flags |= Notification.FLAG_NO_CLEAR;
    		//note.flags |= Notification.FLAG_ONGOING_EVENT;
    		
    		mNM.notify(mId, note);
    		
    		//safe state of toggle button

    		Log.d(ACTIVITY_TAG, "We are setting the servicesRunning to TRUE");

    		SharedPreferences.Editor editor = mPrefs.edit();
    		editor.putBoolean("servicesRunning", true);
    		
    		Boolean tmp = editor.commit();
    		
    		Log.d(ACTIVITY_TAG, "The persistent boolean is now TRUE");
    		
    		Log.d(ACTIVITY_TAG, "Commit success: " + tmp.toString());
    		
    	} else {
    		
    		mNM.cancel(mId);
    		
    		//safe state of toggle button
    		
    		Log.d(ACTIVITY_TAG, "should i be here?");

    		// Undo persistance
       		SharedPreferences.Editor editor = mPrefs.edit();
    		editor.remove("servicesRunning");
    		editor.commit();
    		// Destroy Service
    		try {stopService(bgAntiTheft);} catch(IllegalStateException e){}
    		

    	}
    }
}
