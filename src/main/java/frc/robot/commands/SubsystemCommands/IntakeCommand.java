package frc.robot.commands.SubsystemCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Intake;

public class IntakeCommand extends CommandBase {
    
    private boolean sensorTrip = false;
    
    @Override
    public void initialize() {
        Intake.getInstance().intakeBall();
        sensorTrip = false;
    }

    @Override
    public boolean isFinished() {
        sensorTrip = true;

        return Intake.getInstance().shouldRetract();
    }




    @Override
    public void end(boolean interrupted) {
        if(!sensorTrip){
            Intake.getInstance().idle();
        }
    }

    



}
