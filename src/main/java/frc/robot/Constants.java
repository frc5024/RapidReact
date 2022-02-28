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

		// TODO MOTOR ID AND CONFIG NEEDS TO BE CHANGED
		// Motor used for spinning intake and feeding balls
		public static final int spinnerID = 50;

		// Motor used for spinning intake and feeding balls config
		public static final CTREConfig spinnerConfig = new CTREConfig(false);
	}

	// Climb Constants
	public static final class Climb{

	}

	// Shooter Constants
	public static final class Shooter{
		
		/* Shooter Values */ 

		// Shooter Epsilon
		public static final double shooterEpsilon = 100;

		// TODO These are just guess as to what motors will be assigned
		// Flywheel motor id
		public static final int flyWheelID = 16;

		// Fly wheel config
		public static final RevConfig flywheelConfig = new RevConfig(MotorType.kBrushless);

		// Flywheel encoder tpr
		//public static final int flyWheelEncoderTPR = 2048;

		// PID values Current are from last year
		public static final double kP = 0.00048;
		public static final double kI = 3e-7;
		public static final double kD = 0.355;

		// The speed for ejecting
		public static final double ejectSetSpeed = 150;

		// Speed for setting feed motor
		public static final double beltFeedSpeed = .5;


		// Target RPM TODO placeholder value
		public static final double shootingTargetRPM = 500;

		

		
	}

	



}
