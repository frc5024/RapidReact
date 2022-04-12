package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.OI;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.bases.drivetrain.implementations.DualPIDTankDriveTrain;
import io.github.frc5024.lib5k.control_loops.ExtendedPIDController;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonFX;
import io.github.frc5024.lib5k.hardware.kauai.gyroscopes.NavX;

/**
 * Subsystem for controlling the drivetrain
 */
public class DriveTrain extends SubsystemBase {
	// Creates the drivetrain as a singleton variable
	private static DriveTrain mInstance = null;

	// Motors
	private ExtendedTalonFX rightMaster;
	private ExtendedTalonFX rightSlave;
	private ExtendedTalonFX leftMaster;
	private ExtendedTalonFX leftSlave;

	// Encoders
	private CommonEncoder rightSideEncoder;
	private CommonEncoder leftSideEncoder;

	// Motor inversion multiplier
	private int motorInversionMultiplier = 1;
	
	// Gyroscope
	private NavX gyro;

	// What direction we are initially heading
	private double initialHeading = 0;

	// Checks if this is the first run of the program
	// For some reason the initial heading wasn't working properly when ran during the initialization of this class
	private boolean firstRun = true;
	

	/**
	 * Gets the instance for the drivetrain
	 * 
	 * @return DriveTrain instance
	 */
	public static DriveTrain getInstance() {
		if (mInstance == null) {
			mInstance = new DriveTrain();
		}

		return mInstance;
		
	}


	

	private DriveTrain() {
		
		
		// Initialize right side motors
		rightMaster = CTREMotorFactory.createTalonFX(Constants.DriveTrain.rightMaster,
				Constants.DriveTrain.rightSideConfig);
		rightSlave = rightMaster.makeSlave(Constants.DriveTrain.rightSlave);
		
		// Configures the motors to factory default this provides us with consistency
		rightMaster.configFactoryDefault();
		rightSlave.configFactoryDefault();

		// Set configurations for right side motors

		// This attempts to fix problems when one motor is following the other
		rightMaster.configNeutralDeadband(.0001);
		rightSlave.configNeutralDeadband(.0001);

		// This sets new stator and supply current limits
		rightMaster.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 35, 34, 10));
		rightMaster.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 35, 34, 10));

		rightSlave.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 35, 34, 10));
		rightSlave.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 35, 34, 10));

		// Inverts the right side motors
		rightMaster.setInverted(true);
		rightSlave.setInverted(true);

		// Initialize left side motors
		leftMaster = CTREMotorFactory.createTalonFX(Constants.DriveTrain.leftMaster,
				Constants.DriveTrain.leftSideConfig);
		leftSlave = leftMaster.makeSlave(Constants.DriveTrain.leftSlave);


		leftMaster.configFactoryDefault();
		leftSlave.configFactoryDefault();

		// Set configurations for right side motors.
		leftMaster.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 35, 34, 10));
		leftMaster.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 35, 34, 10));
		
		leftSlave.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 35, 34, 10));
		leftSlave.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 35, 34, 10));

		leftMaster.configNeutralDeadband(.0001);
		leftSlave.configNeutralDeadband(.0001);

		rightMaster.setSensorPhase(false);
		leftMaster.setSensorPhase(false);

		// Initialize encoders
		leftSideEncoder = leftMaster.getCommonEncoder(Constants.DriveTrain.encoderTPR);
		rightSideEncoder = rightMaster.getCommonEncoder(Constants.DriveTrain.encoderTPR);


		// Initialize and reset the gyroscope
		gyro = new NavX();
		gyro.reset();	
		gyro.calibrate();

		

		// Enable the brakes on the motors
		enableBrakes(true);
		
		// Set the default ramp rate
		setRampRate(.05);
	}

	@Override
	public void periodic() {
		// Invert the motors if the driver wants
		// TODO move this into the drive command
		if(OI.getInstance().shouldInvertDriver()){
			invertMotors();
		}

		// If this is the first run of this program reset the initial heading this can probably be removed since it is only nessacary in auto programs which do this automatically
		// TODO remove this
		if(firstRun){
			initialHeading = gyro.getAngle();
			firstRun = false;
		}	

		
	}

	/**
	 * Stops the motors
	 */
	public void stop(){
		rightMaster.stopMotor();
		leftMaster.stopMotor();
	}

	/**
	 * 
	 * @return The heading we are traveling at relative to the last heading reset
	 */
	public double getHeading(){
		//initialHeading = gyro.getAngle();
		return gyro.getAngle() - initialHeading;
	}


	/**
	 * Rezeros the gyro
	 */
	public void resetGyro(){
		initialHeading = gyro.getAngle();
	}

	/**
	 * 
	 * @return gets the meters that the left side of the robot has traveled
	 */
	public double getLeftMeters() {
		// Circumference of the wheel time the amount of rotations divided by the gear ratio
		return (((Math.PI * Constants.DriveTrain.wheelDiameter) * leftSideEncoder.getPosition())
				/ Constants.DriveTrain.leftSideGearRatio);

	}

	/**
	 * 
	 * @return gets the meters that the right side of the robot has traveled
	 */
	public double getRightMeters() {
		// Circumference of the wheel time the amount of rotations divided by the gear ratio
		return (((Math.PI * Constants.DriveTrain.wheelDiameter) * rightSideEncoder.getPosition())
				/ Constants.DriveTrain.rightSideGearRatio);

	}

	/**
	 * Resets the encoder values
	 */
	public void resetEncoders() {
		rightSideEncoder.reset();
		leftSideEncoder.reset();
	}

	/**
	 * Inverts the direction the motors will travel
	 */
	public void invertMotors(){
		motorInversionMultiplier *= -1;
	}


	/**
	 * sets the setting of the brakes between brake and coast
	 * 
	 * @param enabled should the brakes be enable
	 */
	public void enableBrakes(boolean enabled) {
		if (enabled) {
			rightMaster.setNeutralMode(NeutralMode.Brake);
			leftMaster.setNeutralMode(NeutralMode.Brake);
			return;
		}

		rightMaster.setNeutralMode(NeutralMode.Coast);
		leftMaster.setNeutralMode(NeutralMode.Coast);

	}

	/**
	 * sets the ramp rate
	 * 
	 * @param rampTimeSeconds how many seconds to get to full speed
	 */
	public void setRampRate(double rampTimeSeconds) {
		rightMaster.configOpenloopRamp(rampTimeSeconds);
		leftMaster.configOpenloopRamp(rampTimeSeconds);
	}

	/**
	 * 
	 * @param leftSpeed speed to set the left side
	 * @param rightSpeed speed to set the right side
	 */
	public void setSpeed(double leftSpeed, double rightSpeed){
		rightMaster.set(rightSpeed * motorInversionMultiplier);
		leftMaster.set(leftSpeed * motorInversionMultiplier);
	}

	/**
	 * Gets the speed down in m/s
	 * 
	 * @return the speed going down according to the NavX
	 */
	public double getDownwardSpeed(){
		return gyro.getVelocityZ();
	}

}
