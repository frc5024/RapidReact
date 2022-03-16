package frc.robot.subsystem;

import io.github.frc5024.lib5k.hardware.limelightvision.products.LimeLight2;

public class Vision {
	private static Vision instance = null;

	private LimeLight2 limeLight;

	public static Vision getInstance(){
		if(instance == null){
			instance = new Vision();
		}

		return instance;
	}

	private Vision(){
		//limeLight = new LimeLight2()
	}

	
	
}
