package ch.ethz.inf.vs.a1.aenz.antitheft;

import android.hardware.Sensor;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;

public class MotionListener extends TriggerEventListener{

	
	public MotionListener(){
		
	}
	
	public void onTrigger(TriggerEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_SIGNIFICANT_MOTION) {
			
		}
		
	}
}
