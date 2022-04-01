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

		turnController = new PIDController(.1, 0, 0);
		this.goal = goal;
		

	}

	@Override
	public void initialize() {
		driveTrain.stop();
		turnController.reset();

		turnController.setTolerance(5);


	}

	@Override
	public void execute() {
		




		double rotationSpeed = turnController.calculate(driveTrain.getHeading(), goal);
		


		driveTrain.setSpeed(rotationSpeed * .5, -rotationSpeed * .5);
		driveTrain.enableBrakes(true);
	}

	@Override
	public boolean isFinished() {



		
		return turnController.atSetpoint();

		
	}

	@Override
	public void end(boolean interrupted) {
		driveTrain.stop();
		driveTrain.enableBrakes(false);
	}
	
}
