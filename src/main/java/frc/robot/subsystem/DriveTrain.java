package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
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
import io.github.frc5024.libkontrol.statemachines.StateMachine;


/**
 * Subsystem for controlling the drivetrain
 */
public class DriveTrain extends DualPIDTankDriveTrain{

	private static DriveTrain mInstance = null;
	
	private ExtendedTalonFX rightMaster;
	private ExtendedTalonFX rightSlave;
	private ExtendedTalonFX leftMaster;
	private ExtendedTalonFX leftSlave;

	private CommonEncoder rightSideEncoder;
	private CommonEncoder leftSideEncoder;
	
	private ADGyro gyro;

	/**
	 * Gets the instance for the drivetrain
	 * 
	 * @return DriveTrain instance
	 */
	public static DriveTrain getInstance(){
		if(mInstance == null){
			mInstance = new DriveTrain();
		}
	
		return mInstance;
	}

	private DriveTrain() {
		// TODO correct values
		super(new ExtendedPIDController(1, 1, 1), .1);

		

		rightMaster = CTREMotorFactory.createTalonFX(Constants.DriveTrain.rightMaster, Constants.DriveTrain.rightSideConfig);
		rightSlave = rightMaster.makeSlave(Constants.DriveTrain.rightSlave);

		rightSideEncoder = rightMaster.getCommonEncoder(Constants.DriveTrain.encoderTPR);
		
		leftMaster = CTREMotorFactory.createTalonFX(Constants.DriveTrain.leftMaster, Constants.DriveTrain.leftSideConfig);
		leftSlave = leftMaster.makeSlave(Constants.DriveTrain.leftSlave);
		
		leftSideEncoder = leftMaster.getCommonEncoder(Constants.DriveTrain.encoderTPR);
		
		setRampRate(.12);

		gyro = new ADGyro();
	}



	@Override
	public double getLeftMeters() {
		double leftMeters = (Math.PI * Constants.DriveTrain.wheelDiameter) * leftSideEncoder.getPosition();
		return leftMeters;
	}


	@Override
	public double getRightMeters() {
		double rightMeters = (Math.PI * Constants.DriveTrain.wheelDiameter) * rightSideEncoder.getPosition();
		return rightMeters;
	}


	@Override
	public double getWidthMeters() {
		
		return Constants.DriveTrain.driveTrainWidth;
	}


	@Override
	protected void handleVoltage(double leftVolts, double rightVolts) {
		
		rightMaster.setVoltage(rightVolts);
		leftMaster.setVoltage(leftVolts);
	}


	@Override
	protected void resetEncoders() {
		rightSideEncoder.reset();
		leftSideEncoder.reset();
	}


	@Override
	protected void setMotorsInverted(boolean motorsInverted) {
		// TODO Auto-generated method stub
		rightMaster.setInverted(motorsInverted);
		leftMaster.setInverted(motorsInverted);
	}


	@Override
	protected void setEncodersInverted(boolean encodersInverted) {
		// TODO Auto-generated method stub
		rightSideEncoder.setPhaseInverted(encodersInverted);		
		leftSideEncoder.setPhaseInverted(encodersInverted);
	}


	@Override
	protected void handleGearShift(Gear gear) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void enableBrakes(boolean enabled) {
		rightMaster.setNeutralMode(enabled ? NeutralMode.Brake : NeutralMode.Brake);
		leftMaster.setNeutralMode(enabled ? NeutralMode.Brake : NeutralMode.Brake);
	}


	@Override
	protected Rotation2d getCurrentHeading() {
		// TODO Auto-generated method stub
		return new Rotation2d(gyro.getHeading());
		
	}


	@Override
	protected void runIteration() {
		drive();
		
	}


	@Override
	public void setRampRate(double rampTimeSeconds) {
		// TODO Auto-generated method stub
		rightMaster.configOpenloopRamp(rampTimeSeconds);
		leftMaster.configOpenloopRamp(rampTimeSeconds);
	}

	private void drive(){
		rightMaster.set(OI.getInstance().getSpeed());
		leftMaster.set(OI.getInstance().getSpeed());
	
	}

	






}
