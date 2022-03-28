package frc.robot.commands.autocommands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.DriveTrain;

public class AutoRotate extends CommandBase {

	private PIDController turnController;

	private double goal;

	private DriveTrain driveTrain = DriveTrain.getInstance();

	public AutoRotate(double goal){
		addRequirements(driveTrain);

		turnController = new PIDController(0, 0, 0);
		this.goal = goal;


	}

	@Override
	public void initialize() {
		driveTrain.stop();
		turnController.reset();

		turnController.setTolerance(5);
		// Check if it is faster to turn the other way
		double error = goal - driveTrain.getCurrentHeading().getDegrees();

		if(error > 180){
			error -= 360;
			goal *= -1;
		}


	}

	@Override
	public void execute() {
		




		double rotationSpeed = turnController.calculate(driveTrain.getCurrentHeading().getDegrees(), goal);
		


		driveTrain.setSpeed(rotationSpeed * .5, -rotationSpeed * .5);
	}

	@Override
	public boolean isFinished() {
		return turnController.atSetpoint();
		
	}
	
}
