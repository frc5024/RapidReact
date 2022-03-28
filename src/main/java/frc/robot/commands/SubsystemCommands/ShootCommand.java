package frc.robot.commands.SubsystemCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.Shooter;

public class ShootCommand extends CommandBase {
    
	private double setRPM = Constants.Shooter.lineShotTargetRPM;

	public ShootCommand(double setPoint){
		this.setRPM = setPoint;
	}

    @Override
    public void initialize() {
		Shooter.getInstance().setTarget(setRPM, 0, 0, 0);

        Shooter.getInstance().shootBall();
    }


    @Override
    public void execute() {
        
    }

    @Override
    public boolean isFinished() {
        return Shooter.getInstance().isDoneShooting(); 
    }


    @Override
    public void end(boolean interrupted) {
        
        Shooter.getInstance().stop();        
        
    }



}
