package frc.robot.commands.autocommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.DriveTrain;

public class RollBack extends CommandBase{

	public RollBack(){
		addRequirements(DriveTrain.getInstance());
	}

	@Override
	public void execute() {
		DriveTrain.getInstance().setSpeed(-.5, -.5);
	}

	@Override
	public void end(boolean interrupted) {
		DriveTrain.getInstance().setSpeed(0, 0);
	}
	
}
