package frc.robot;

import io.github.frc5024.lib5k.hardware.ctre.motors.CTREConfig;

public final class Constants {
	
	// Controller Constants
	public static final class Controllers{

		public static final int driveController = 0;

		public static final int operatorContoller = 1;

	}

	// TODO Correct these values
	// Drive Train Constants
	public static final class DriveTrain{

		// Right side TalonFX
		public static final int rightMaster = 0;
		public static final int rightSlave = 1;

		public static final boolean shouldInvertRight = false;

		public static final boolean setBreaks = true;

		public static final CTREConfig rightSideConfig = new CTREConfig();



		// Left side TalonFX
		public static final int leftMaster = 2;
		public static final int leftSlave = 3;

		public static final boolean shouldInvertLeft = true;

		public static final CTREConfig leftSideConfig = new CTREConfig();

		// General Properties
		public static final int encoderTPR = 5024;

		// Physical Properties Measured in meters
		public static final double wheelDiameter = .12;

		public static final double driveTrainWidth = .75;





	}

	// Intake Constants
	public static final class Intake{

	}

	// Climb Constants
	public static final class Climb{

	}

	// Shooter Constants
	public static final class Shooter{

	}

	



}
