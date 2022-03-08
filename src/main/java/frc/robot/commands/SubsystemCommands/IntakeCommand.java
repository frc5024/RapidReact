package frc.robot.commands.SubsystemCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Intake;

public class IntakeCommand extends CommandBase {
    
    private boolean sensorTrip = false;
    
    @Override
    public void initialize() {
        Intake.getInstance().intakeBall();
    }

    @Override
    public boolean isFinished() {
        return Intake.getInstance().shouldRetract();
    }

    @Override
    public void end(boolean interrupted) {
        Intake.getInstance().idle();

    }

    



}
