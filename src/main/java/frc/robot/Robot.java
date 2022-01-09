// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.DriveTrain;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.Shooter;
import io.github.frc5024.lib5k.autonomous.RobotProgram;

/**
 * The VM is configured to automatically run this class. If you change the name
 * of this class or the
 * package after creating this project, you must also update the build.gradle
 * file in the project.
 */
public class Robot extends RobotProgram {
	

	// Subsystem instance variables
	private DriveTrain driveTrain;
	private Climber climber;
	private Shooter shooter;
	private Intake intake;

	public Robot() {
		super(false, true, null);

		// Initalize subsystem variables
		driveTrain = DriveTrain.getInstance();
		climber = Climber.getInstance();
		shooter = Shooter.getInstance();
		intake = Intake.getInstance();



		// Register subsystems
		driveTrain.register();
		climber.register();
		shooter.register();
		intake.register();
		


	}


	@Override
	public void periodic(boolean init) {
		// TODO Auto-generated method stub

	}

	@Override
	public void autonomous(boolean init) {
		// TODO Auto-generated method stub

	}

	@Override
	public void teleop(boolean init) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(boolean init) {
		// TODO Auto-generated method stub

	}

	@Override
	public void test(boolean init) {
		// TODO Auto-generated method stub

	}
}
