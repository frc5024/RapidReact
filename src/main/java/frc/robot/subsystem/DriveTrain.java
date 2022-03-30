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

	private static DriveTrain mInstance = null;

	private ExtendedTalonFX rightMaster;
	private ExtendedTalonFX rightSlave;
	private ExtendedTalonFX leftMaster;
	private ExtendedTalonFX leftSlave;

	private double leftMeters = 0;
	private double rightMeters = 0;

	private CommonEncoder rightSideEncoder;
	private CommonEncoder leftSideEncoder;

	private int encoderInversionMultiplier = 1;
	private int motorInversionMultiplier = 1;
	
	private double initialHeading = 0;

	private NavX gyro;

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
		
		
		rightMaster = CTREMotorFactory.createTalonFX(Constants.DriveTrain.rightMaster,
				Constants.DriveTrain.rightSideConfig);
		rightSlave = rightMaster.makeSlave(Constants.DriveTrain.rightSlave);
		
		rightMaster.configFactoryDefault();
		rightSlave.configFactoryDefault();

		rightMaster.configNeutralDeadband(.0001);
		rightSlave.configNeutralDeadband(.0001);

		rightMaster.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 35, 34, 10));
		rightMaster.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 35, 34, 10));

		rightSlave.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 35, 34, 10));
		rightSlave.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 35, 34, 10));

		rightMaster.setInverted(true);
		rightSlave.setInverted(true);

		leftMaster = CTREMotorFactory.createTalonFX(Constants.DriveTrain.leftMaster,
				Constants.DriveTrain.leftSideConfig);
		leftSlave = leftMaster.makeSlave(Constants.DriveTrain.leftSlave);

		leftMaster.configFactoryDefault();
		leftSlave.configFactoryDefault();

		leftMaster.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 35, 34, 10));
		leftMaster.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 35, 34, 10));
		
		leftSlave.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 35, 34, 10));
		leftSlave.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 35, 34, 10));

		leftMaster.configNeutralDeadband(.0001);
		leftSlave.configNeutralDeadband(.0001);

		rightMaster.setSensorPhase(false);
		leftMaster.setSensorPhase(false);

		leftSideEncoder = leftMaster.getCommonEncoder(Constants.DriveTrain.encoderTPR);
		rightSideEncoder = rightMaster.getCommonEncoder(Constants.DriveTrain.encoderTPR);

		//setRampRate(0.12);




		gyro = new NavX();
		gyro.reset();	
		gyro.calibrate();

		


		enableBrakes(true);
		setRampRate(.05);
	}

	@Override
	public void periodic() {
		if(OI.getInstance().shouldInvertDriver()){
			invertMotors();
		}
		if(firstRun){
			initialHeading = gyro.getAngle();
			firstRun = false;
		}
		leftMeters = getLeftMeters();
		rightMeters = getRightMeters();

		SmartDashboard.putNumber("Left Master Volts", leftMaster.getMotorOutputVoltage());
		SmartDashboard.putNumber("Left Master Amp", leftMaster.getStatorCurrent());
		SmartDashboard.putNumber("gyro heading", getHeading());
		SmartDashboard.putNumber("inital heading", initialHeading);

		
	}

	public void stop(){
		rightMaster.stopMotor();
		leftMaster.stopMotor();
	}

	public double getHeading(){
		//initialHeading = gyro.getAngle();
		return gyro.getAngle() - initialHeading;
	}

	
	public double getLeftMeters() {
		return (((Math.PI * Constants.DriveTrain.wheelDiameter) * leftSideEncoder.getPosition())
				/ Constants.DriveTrain.leftSideGearRatio) * encoderInversionMultiplier;

	}

	
	public double getRightMeters() {
		return (((Math.PI * Constants.DriveTrain.wheelDiameter) * rightSideEncoder.getPosition())
				/ Constants.DriveTrain.rightSideGearRatio)
				* encoderInversionMultiplier;

	}

	


	public void resetEncoders() {
		rightSideEncoder.reset();
		leftSideEncoder.reset();
	}

	
	protected void setMotorsInverted(boolean motorsInverted) {

		motorInversionMultiplier = motorsInverted ? -1 : 1;

	}

	public void invertMotors(){
		motorInversionMultiplier *= -1;
	}

	
	protected void setEncodersInverted(boolean encodersInverted) {

		encoderInversionMultiplier = encodersInverted ? -1 : 1;
	}

	

	
	public void enableBrakes(boolean enabled) {
		if (enabled) {
			rightMaster.setNeutralMode(NeutralMode.Brake);
			leftMaster.setNeutralMode(NeutralMode.Brake);
			return;
		}

		rightMaster.setNeutralMode(NeutralMode.Coast);
		leftMaster.setNeutralMode(NeutralMode.Coast);

	}




	
	public void setRampRate(double rampTimeSeconds) {
		rightMaster.configOpenloopRamp(rampTimeSeconds);
		leftMaster.configOpenloopRamp(rampTimeSeconds);
	}

	public void setSpeed(double leftSpeed, double rightSpeed){
		rightMaster.set(rightSpeed * motorInversionMultiplier);
		leftMaster.set(leftSpeed * motorInversionMultiplier);
	}



}
