package frc.robot.commands.SubsystemCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Intake;

public class IntakeCommand extends CommandBase {
    
    @Override
    public void initialize() {
        Intake.getInstance().intakeBall();
    }

    @Override
    public boolean isFinished() {
        return Intake.getInstance().intakeFinished();
    }

    @Override
    public void end(boolean interrupted) {
		if(interrupted){
			Intake.getInstance().idle();
		}else{
        	Intake.getInstance().spinDown();
		}
    }

	

    



}
