package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.OI;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.bases.drivetrain.implementations.DualPIDTankDriveTrain;
import io.github.frc5024.lib5k.control_loops.ExtendedPIDController;
import io.github.frc5024.lib5k.control_loops.base.Controller;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonFX;
import io.github.frc5024.lib5k.hardware.generic.gyroscopes.ADGyro;
import io.github.frc5024.lib5k.hardware.kauai.gyroscopes.NavX;
import io.github.frc5024.libkontrol.statemachines.StateMachine;

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
		super(new ExtendedPIDController(1, 1, 1), .1);

		rightMaster = CTREMotorFactory.createTalonFX(Constants.DriveTrain.rightMaster,
				Constants.DriveTrain.rightSideConfig);
		rightSlave = rightMaster.makeSlave(Constants.DriveTrain.rightSlave);

		rightMaster.setInverted(true);
		rightSlave.setInverted(true);

		leftMaster = CTREMotorFactory.createTalonFX(Constants.DriveTrain.leftMaster,
				Constants.DriveTrain.leftSideConfig);
		leftSlave = leftMaster.makeSlave(Constants.DriveTrain.leftSlave);

		rightMaster.setSensorPhase(false);
		leftMaster.setSensorPhase(false);

		leftSideEncoder = leftMaster.getCommonEncoder(Constants.DriveTrain.encoderTPR);
		rightSideEncoder = rightMaster.getCommonEncoder(Constants.DriveTrain.encoderTPR);

		setRampRate(.12);

		gyro = new NavX();
		gyro.calibrate();
		
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
		SmartDashboard.putNumber("Value", getLeftMeters());
		Shuffleboard.getTab("Main Tab");
	}

	@Override
	public void setRampRate(double rampTimeSeconds) {
		rightMaster.configOpenloopRamp(rampTimeSeconds);
		leftMaster.configOpenloopRamp(rampTimeSeconds);
	}

}
