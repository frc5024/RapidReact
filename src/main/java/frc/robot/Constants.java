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


	// Drive Train Constants
	public static final class DriveTrain{

		// Right side TalonFX
		public static final int rightMaster = 1;
		public static final int rightSlave = 2;

		public static final boolean shouldInvertRight = false;

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

		// Motor used for spinning intake and feeding balls
		public static final int spinnerID = 7;

		// Motor used for spinning intake and feeding balls config
		public static final CTREConfig spinnerConfig = new CTREConfig(false);

		// Speed for intaking the balls
		public static final double intakeSpeed = 0.36;

		// Speed for outtaking balls
		public static final double outputSpeed = -0.10;
		// Speed to run the motors when spinning down
		public static final double spinDownSpeed = 0.2;

		// Solenoid forward and reverse channel ids
		public static final int solenoidForward = 1;
		public static final int solenoidReverse = 0;

		public static final int compressorID = 0;

		public static final int retractSensorID = 1;
		public static final int holdSensorID = 0;


	}

	// Climb Constants
	public static final class Climb{

		// Create Climber Config and configured
		public static final CTREConfig climbConfig = new CTREConfig(false, true, false, true, 36, 32, 10, 0, true);

		// Create Smart Servo ID
		public static final int smartServoChannel = 9;

		// Climber ID
		public static final int climberID = 9;

		// Hall effects for determining position
		public static final int bottomHallEffectID = 7;


		// Pneumatic forward channel
		public static final int pneumaticForward = 2;

		// Pneumatic reverse channel
		public static final int pneumaticReverse = 3;

		// Motor pull speed
		public static final double downPullSpeed = .9;
		public static final double upPullSpeed = .4;

		// The max speed we can travel downwards before our controlloops kick in units: m/s
		public static final double maximumAllowableFallSpeed = 1;

		// Voltage required to fight gravity
		public static final double kG = 6;
		
		
	}

	// Shooter Constants
	public static final class Shooter{

		// Line break
		public static final int lineBreakChannelId = 2;

		// Shooter Values

		// Shooter Epsilon
		public static final double shooterPositionTolerance = 50;

		public static final double shooterVelocityTolerance = 10;

		// Flywheel motor id
		public static final int flyWheelID = 12;

		// Fly wheel config
		public static final CTREConfig flywheelConfig = new CTREConfig(false, true, false, true, 36, 12, 34, 24, true);
		
		// PID values Current are from last year
		public static final double kP = 0.0028;
		public static final double kI = .001;
		public static final double kD = .0007;

		// PID values for low shoot, these values aren't currently used
		public static final double low_kP = 0.0023;
		public static final double low_kI = .001;
		public static final double low_kD = 0;

		public static final int encoderTPR = 2048;

		// Speed for setting feed motor
		public static final double beltFeedSpeed = .8;

		// Preheat voltage
		public static final double preheatVoltage = 2;

		// This value corrects the miscalculation in our library and converts to RPM
		public static final double velocityCorrectionFactor = 1000 / .001666;

		// The ratio from the motor to the flywheel
		public static final double shooterRatio = .714;

		public static final class RPMS{

			// Shot and Names

			// Eject shot
			public static final double lowGoalTargetRPM = 1600;
			public static final String lowGoalName = "Low Goal";
			

			// Close Shoot
			public static final double closeTargetRPM = 2400;
			public static final String closeGoalName = "Mid Shot";
	
			// Line Shot 
			public static final double lineShotTargetRPM = 2700;
			public static final String lineShotName = "Far Shot";
	
			// Long Shot
			public static final double longShot = 3500;
			public static final String longShotName = "Launch Pad Shoot";


		}	

		
	}


}
