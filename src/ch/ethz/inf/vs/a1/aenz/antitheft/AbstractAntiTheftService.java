package ch.ethz.inf.vs.a1.aenz.antitheft;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

public abstract class AbstractAntiTheftService extends Service {

	protected AbstractMovementDetector listener;
	private SensorManager sMan;
	private Sensor sSMotion;
	private static final String ACTIVITY_TAG = "### AbstractAntiTheft ###";

	
	@Override
	public void onCreate() {
		listener = new MovementDetector();
		listener.setCallbackService(this);
		Log.d(ACTIVITY_TAG, "created");
		
		sMan = (SensorManager)getSystemService(SENSOR_SERVICE);
		sSMotion = sMan.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
		
		//register listener to accelerator
		//last value is the sensor delay in microseconds
		sMan.registerListener(listener, sSMotion, 10^6);
	}

	/**
	 * Starts the alarm when a deliberate move is detected.
	 */
	public abstract void startAlarm();
}
