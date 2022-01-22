// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.TestPath;
import frc.robot.auto.TestTurnPath;
import frc.robot.commands.DriveCommand;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.DriveTrain;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.Shooter;
import io.github.frc5024.lib5k.autonomous.RobotProgram;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.USBLogger;

/**
 * The VM is configured to automatically run this class. If you change the name
 * of this class or the
 * package after creating this project, you must also update the build.gradle
 * file in the project.
 */
public class Robot extends RobotProgram {
	
	private static ShuffleboardTab mainShuffleboardTab = Shuffleboard.getTab("Main Tab");
	
	private RobotLogger logger = RobotLogger.getInstance();


	// Subsystem instance variables
	private DriveTrain driveTrain;
	private Climber climber;
	private Shooter shooter;
	private Intake intake;

	// Commands
	private DriveCommand driveCommand;
	

	// TODO REMOVE IF NEEDED
	@Override
	public void startCompetition() {
		super.startCompetition();

	}

	public Robot() {
		super(false, true, mainShuffleboardTab);

		// Set up logger
		if (Constants.shouldLogToUSB){
			logger.enableUSBLogging(new USBLogger());
		}


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
		
		// Commands
		driveTrain.setDefaultCommand(new DriveCommand());

		// Creating Auto Commands
		addAutonomous(new TestPath());
		addAutonomous(new TestTurnPath());

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
		

	}

	@Override
	public void disabled(boolean init) {
		if (init) {
            DriveTrain.getInstance().stop();
        }

	}

	@Override
	public void test(boolean init) {
		// TODO Auto-generated method stub

	}
}
