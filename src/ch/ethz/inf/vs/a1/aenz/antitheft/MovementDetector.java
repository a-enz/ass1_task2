package ch.ethz.inf.vs.a1.aenz.antitheft;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class MovementDetector extends AbstractMovementDetector {


	private int nShakes = 0;
	private int currentVal = 0;
	private int nextVal;
	
	@Override
	protected boolean doAlarmLogic(float[] values) {
		
		return false;
	}
}
