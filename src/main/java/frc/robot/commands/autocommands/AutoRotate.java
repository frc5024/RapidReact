package frc.robot.commands.autocommands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.DriveTrain;

public class AutoRotate extends CommandBase {

	private PIDController turnController;

	private double goal;

	private DriveTrain driveTrain = DriveTrain.getInstance();

	public AutoRotate(double goal){
		addRequirements(driveTrain);

		//turnController = new PIDController(.005, .006, 0);
		turnController = new PIDController(.003, 0.00030, 0);
		this.goal = goal;
		

	}

	@Override
	public void initialize() {
		driveTrain.stop();
		turnController.reset();
		driveTrain.resetGyro();

		turnController.setTolerance(3, 10);


	}

	@Override
	public void execute() {
		




		double rotationSpeed = turnController.calculate(driveTrain.getHeading(), goal);
		


		driveTrain.setSpeed(rotationSpeed * .5, -rotationSpeed * .5);
		
	}

	@Override
	public boolean isFinished() {



		
		return turnController.atSetpoint();

		
	}

	@Override
	public void end(boolean interrupted) {
		driveTrain.stop();
		
	}
	
}
