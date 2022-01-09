package frc.robot.subsystem;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.libkontrol.statemachines.StateMachine;


/**
 * Subsystem for controlling the drivetrain
 */
public class DriveTrain extends SubsystemBase{

	// A variable for storing the instance
	private static DriveTrain mInstance = null;


	/**
	 * Gets the instance for the drivtrain
	 * creates it if it didn't exist
	 * 
	 * @return DriveTrain instance
	 */
	public static DriveTrain getInstance(){
		if(mInstance == null){
			mInstance = new DriveTrain();
		}

		return mInstance;
	}


	/**
	 * Constructor for the drivetrain
	 */
	private DriveTrain(){





	}

	/**
	 * Periodic function that runs periodically
	 * subsystem must be registered in robot.java
	 */
	@Override
	public void periodic() {
		
	}






}
