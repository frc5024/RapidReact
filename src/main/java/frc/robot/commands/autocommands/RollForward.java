package frc.robot.commands.autocommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.subsystem.DriveTrain;
import io.github.frc5024.lib5k.logging.RobotLogger;

public class RollForward extends CommandBase{
    
    public RollForward(){
		addRequirements(DriveTrain.getInstance());
	}

	@Override
	public void execute() {
		DriveTrain.getInstance().setSpeed(.3, .3);
	}

	@Override
	public void end(boolean interrupted) {
		DriveTrain.getInstance().setSpeed(0, 0);
	}
	
}




