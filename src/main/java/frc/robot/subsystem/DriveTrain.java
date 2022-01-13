package frc.robot.subsystem;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.bases.drivetrain.implementations.DualPIDTankDriveTrain;
import io.github.frc5024.lib5k.control_loops.ExtendedPIDController;
import io.github.frc5024.lib5k.control_loops.base.Controller;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonFX;
import io.github.frc5024.libkontrol.statemachines.StateMachine;


/**
 * Subsystem for controlling the drivetrain
 */
public class DriveTrain extends DualPIDTankDriveTrain{



	public DriveTrain() {
		// TODO correct values
		super(new ExtendedPIDController(1, 1, 1), .1);
		
		
	}



	@Override
	public double getLeftMeters() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double getRightMeters() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double getWidthMeters() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	protected void handleVoltage(double leftVolts, double rightVolts) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void resetEncoders() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void setMotorsInverted(boolean motorsInverted) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void setEncodersInverted(boolean encodersInverted) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void handleGearShift(Gear gear) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void enableBrakes(boolean enabled) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected Rotation2d getCurrentHeading() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void runIteration() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setRampRate(double rampTimeSeconds) {
		// TODO Auto-generated method stub
		
	}

	






}
