package frc.robot.commands.autocommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.Shooter;

public class AutoShoot extends CommandBase{
	
	public AutoShoot(){

	}

	@Override
	public void initialize() {
		Shooter.getInstance().setTarget(Constants.Shooter.closeTargetRPM, Constants.Shooter.kP, Constants.Shooter.kI, Constants.Shooter.kD);
		Shooter.getInstance().shootBall();
	}

	@Override
	public boolean isFinished() {
		return Shooter.getInstance().isDoneShooting();
	}

	

}
