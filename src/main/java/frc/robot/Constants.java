package frc.robot;

import io.github.frc5024.lib5k.hardware.ctre.motors.CTREConfig;

public final class Constants {

	// Should log to USB
	public static final boolean shouldLogToUSB = false;
	
	// Controller Constants
	public static final class Controllers{

		public static final int driverController = 0;
		public static final int operatorController = 1;

	}

	// TODO Correct these values
	// Drive Train Constants
	public static final class DriveTrain{

		// Right side TalonFX
		public static final int rightMaster = 1;
		public static final int rightSlave = 2;

		public static final boolean shouldInvertRight = true;

		public static final boolean setBreaks = false;

		public static final CTREConfig rightSideConfig = new CTREConfig(shouldInvertRight);

		public static final double rightSideGearRatio = 8.45/1;

		// Left side TalonFX
		public static final int leftMaster = 3;
		public static final int leftSlave = 4;

		public static final boolean shouldInvertLeft = false;

		public static final CTREConfig leftSideConfig = new CTREConfig(shouldInvertLeft);

		public static final double leftSideGearRatio = 8.45/1;


		// General Properties
		public static final int encoderTPR = 2048;

		// Physical Properties Measured in meters
		public static final double wheelDiameter = .15;

		public static final double driveTrainWidth = .71;





	}

	// Intake Constants
	public static final class Intake{

	}

	// Climb Constants
	public static final class Climb{
<<<<<<< Updated upstream

		// Create Climber Config and configured
		public static final CTREConfig climbConfig = new CTREConfig(false, true, false, true, 33, 0, 30, 15, true);

		// Create Smart Servo ID
		public static final int smartServoChannel = 9;

		// Climber ID
		public static final int climberID = 9;

=======
		public static final int climbMotorID = 1;
		public static final CTREConfig climbMotorConfig = new CTREConfig(false, true, 34, 32, 15, 0, true);
		
>>>>>>> Stashed changes
		// Hall effects for determining position
		public static final int topHallEffectID = 1;
		public static final int bottomHallEffectID = 2;
		
	}

	// Shooter Constants
	public static final class Shooter{

	}

	



}
