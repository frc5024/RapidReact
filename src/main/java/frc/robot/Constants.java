package frc.robot;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.system.plant.DCMotor;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREConfig;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.RevConfig;

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

		// TODO MOTOR ID AND CONFIG NEEDS TO BE CHANGED
		// Motor used for spinning intake and feeding balls
		public static final int spinnerID = 7;

		// Motor used for spinning intake and feeding balls config
		public static final CTREConfig spinnerConfig = new CTREConfig(false);

		// Speed for intaking the balls
		public static final double intakeSpeed = 0.36;

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
		public static final int topHallEffectID = 6;
		public static final int bottomHallEffectID = 2;
		
	}

	// Shooter Constants
	public static final class Shooter{
		// Linebreak
		public static final int lineBreakChannelId = 2;

		// Shooter Values

		// Shooter Epsilon
		public static final double shooterEpsilon = 100;

		// Flywheel motor id
		public static final int flyWheelID = 12;

		// Fly wheel config
		public static final CTREConfig flywheelConfig = new CTREConfig(false, true, false, true, 36, 12, 34, 24, true);
		
		// PID values Current are from last year
		public static final double kP = 0.0023;
		public static final double kI = .001;
		public static final double kD = 0;

		// // PID values Current are from last year
		// public static final double kP = 0.0023;
		// public static final double kI = .001;
		// public static final double kD = 0;

		public static final int encoderTPR = 2048;

		// The speed for ejecting
		public static final double ejectSetSpeed = 150;

		// Speed for setting feed motor
		public static final double beltFeedSpeed = .8;


		// Target RPMs

		// Eject/Low Goal
		public static final double lowGoalTargetRPM = 1200;

		// Close Shoot
		public static final double closeTargetRPM = 2800;

		// Line Shot 
		public static final double lineShotTargetRPM = 3100;

		

		
	}

	



}
