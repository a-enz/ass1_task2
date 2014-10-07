package ch.ethz.inf.vs.a1.aenz.antitheft;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public abstract class AbstractMovementDetector implements SensorEventListener {
	protected AbstractAntiTheftService antiTheftService;
	private static final String ACTIVITY_TAG = "### AbsMV ###";

	public void setCallbackService(AbstractAntiTheftService service) {
		antiTheftService = service;
	}

	/**
	 * Takes the sensor values, pass them to doAlarmLogic() to check if the
	 * alarm should be fired, then calls antiTheftService.startAlarm() if a
	 * deliberate movement is detected.
	 */
	
	@Override
	public final void onSensorChanged(SensorEvent event) {
		Log.d(ACTIVITY_TAG, "SENSOR event ");
		boolean isAlarm = false;
		
		
// Extract values here
		if(event.sensor.TYPE_ACCELEROMETER == Sensor.TYPE_ACCELEROMETER) {
			isAlarm = doAlarmLogic(event.values);
		}
		if (isAlarm) {
			((AbstractAntiTheftService) antiTheftService).startAlarm();
		}
	}

	/**
	 * Implements the sensor logic that is needed to trigger the alarm.
	 * 
	 * @param values
	 *            : the sensor values detected by the service.
	 * @return true if the service should start the alarm, false otherwise.
	 */
	protected abstract boolean doAlarmLogic(float[] values);

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do Nothing
	}
}
