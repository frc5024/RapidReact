package frc.robot.commands.SubsystemCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Shooter;

public class ShootCommand extends CommandBase {
    

    @Override
    public void initialize() {
        Shooter.getInstance().shootBall();
    }


    @Override
    public void execute() {
        // TODO Auto-generated method stub
        super.execute();
    }

    @Override
    public boolean isFinished() {
        return Shooter.getInstance().isDoneShooting(); 
    }


    @Override
    public void end(boolean interrupted) {
        // TODO Auto-generated method stub
        super.end(interrupted);
    }



}
