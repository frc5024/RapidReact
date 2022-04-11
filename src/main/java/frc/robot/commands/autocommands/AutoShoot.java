package frc.robot.commands.autocommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.Shooter;

public class AutoShoot extends CommandBase{
	
	private double targetRPM;

	public AutoShoot(double rpm){
		this.targetRPM = rpm;
	}

	@Override
	public void initialize() {
		Shooter.getInstance().setTarget(targetRPM);
		Shooter.getInstance().shootBall();
	}

	@Override
	public boolean isFinished() {
		return Shooter.getInstance().isDoneShooting();
	}

	

}
