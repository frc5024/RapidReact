package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
public class DriveTrain extends DualPIDTankDriveTrain {

	private static DriveTrain mInstance = null;

	private ExtendedTalonFX rightMaster;
	private ExtendedTalonFX rightSlave;
	private ExtendedTalonFX leftMaster;
	private ExtendedTalonFX leftSlave;

	private CommonEncoder rightSideEncoder;
	private CommonEncoder leftSideEncoder;

	private int encoderInversionMultiplier = 1;
	private int motorInversionMultiplier = 1;
	
	double leftSetVoltage = 0;
	double rightSetVoltage = 0;

	private NavX gyro;

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
		super(new ExtendedPIDController(.0088, .01, .0106), .478);

		// Initialize right side motors
		rightMaster = CTREMotorFactory.createTalonFX(Constants.DriveTrain.rightMaster,
				Constants.DriveTrain.rightSideConfig);
		rightSlave = rightMaster.makeSlave(Constants.DriveTrain.rightSlave);
		
		// Set configurations for right side motors
		rightMaster.configNeutralDeadband(.0001);
		rightSlave.configNeutralDeadband(.0001);

		rightMaster.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 35, 34, 10));
		rightMaster.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 35, 34, 10));

		rightSlave.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 35, 34, 10));
		rightSlave.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 35, 34, 10));

		rightMaster.setInverted(true);
		rightSlave.setInverted(true);

		// Initialize left side motors
		leftMaster = CTREMotorFactory.createTalonFX(Constants.DriveTrain.leftMaster,
				Constants.DriveTrain.leftSideConfig);
		leftSlave = leftMaster.makeSlave(Constants.DriveTrain.leftSlave);

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

		setRampRate(0.12);




		gyro = new NavX();
		gyro.calibrate();

		enableBrakes(false);
		
	}

	@Override
	public double getLeftMeters() {
		return (((Math.PI * Constants.DriveTrain.wheelDiameter) * leftSideEncoder.getPosition())
				/ Constants.DriveTrain.leftSideGearRatio) * encoderInversionMultiplier;

	}

	@Override
	public double getRightMeters() {
		return (((Math.PI * Constants.DriveTrain.wheelDiameter) * rightSideEncoder.getPosition())
				/ Constants.DriveTrain.rightSideGearRatio)
				* encoderInversionMultiplier;

	}

	@Override
	public double getWidthMeters() {

		return Constants.DriveTrain.driveTrainWidth;
		
	}

	@Override
	protected void handleVoltage(double leftVolts, double rightVolts) {
		leftSetVoltage = leftVolts;
		rightSetVoltage = rightVolts;

		rightMaster.setVoltage(rightVolts * motorInversionMultiplier);
		leftMaster.setVoltage(leftVolts * motorInversionMultiplier);
	}

	@Override
	protected void resetEncoders() {
		rightSideEncoder.reset();
		leftSideEncoder.reset();
	}

	@Override
	protected void setMotorsInverted(boolean motorsInverted) {

		motorInversionMultiplier = motorsInverted ? -1 : 1;

	}

	public void invertMotors(){
		motorInversionMultiplier *= -1;
	}

	@Override
	protected void setEncodersInverted(boolean encodersInverted) {

		encoderInversionMultiplier = encodersInverted ? -1 : 1;
	}

	@Override
	protected void handleGearShift(Gear gear) {
		// TODO Auto-generated method stub

	}

	

	@Override
	protected void enableBrakes(boolean enabled) {
		if (enabled) {
			rightMaster.setNeutralMode(NeutralMode.Brake);
			leftMaster.setNeutralMode(NeutralMode.Brake);
			return;
		}

		rightMaster.setNeutralMode(NeutralMode.Coast);
		leftMaster.setNeutralMode(NeutralMode.Coast);

	}

	@Override
	protected Rotation2d getCurrentHeading() {
		
		return gyro.getRotation();
	}

	@Override
	protected void runIteration() {
		SmartDashboard.putNumber("Left Voltage Set", leftSetVoltage);
		SmartDashboard.putNumber("Left Voltage Actual", leftMaster.getMotorOutputPercent());
		SmartDashboard.putNumber("Right Voltage Set", rightSetVoltage);
		SmartDashboard.putNumber("Right Voltage Actual", rightMaster.getMotorOutputPercent());

		
		if(OI.getInstance().shouldInvertDriver()){
			invertMotors();
		}

	}

	@Override
	public void setRampRate(double rampTimeSeconds) {
		rightMaster.configOpenloopRamp(rampTimeSeconds);
		leftMaster.configOpenloopRamp(rampTimeSeconds);
	}

	/**
	 * 
	 */
	public void setSpeed(double leftSpeed, double rightSpeed){
		leftSetVoltage = leftSpeed;
		rightSetVoltage = rightSpeed;

		rightMaster.set(rightSpeed * motorInversionMultiplier);
		leftMaster.set(leftSpeed * motorInversionMultiplier);
	}


}
