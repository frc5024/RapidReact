package frc.robot;

import io.github.frc5024.lib5k.hardware.ctre.motors.CTREConfig;

public final class Constants {
	
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

	}

	// Shooter Constants
	public static final class Shooter{

	}

	



}
