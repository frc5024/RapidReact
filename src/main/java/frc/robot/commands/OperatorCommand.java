package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.commands.SubsystemCommands.IntakeCommand;
import frc.robot.commands.SubsystemCommands.ShootCommand;
import frc.robot.subsystem.Intake;

public class OperatorCommand extends CommandBase {
    
    private OI oi = OI.getInstance();

    // Define commands here
    private ShootCommand shootCommand;
    
    private IntakeCommand intakeCommand;

    /**
     * Initialize Commands here
     */
    public OperatorCommand(){
        shootCommand = new ShootCommand();
        intakeCommand = new IntakeCommand();
    }

    
    
    @Override
    public void initialize() {
        
    }


    /**
     * Set up bindings
     */
    @Override
    public void execute() {
        if(oi.shouldShoot()){
            shootCommand.schedule();
        }else{
            shootCommand.cancel();
        }

        if(oi.shouldIntake() && Intake.getInstance().canIntake()){
            intakeCommand.schedule();
        }else{
            intakeCommand.cancel();
        }


        
    }


    /**
     * Cancel all commands here
     */
    @Override
    public void end(boolean interrupted) {
        shootCommand.cancel();
    }

}
