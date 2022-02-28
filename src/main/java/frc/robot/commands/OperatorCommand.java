package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.commands.SubsystemCommands.ShootCommand;

public class OperatorCommand extends CommandBase {
    
    private OI oi = OI.getInstance();

    // Define commands here
    private ShootCommand shootCommand;


    /**
     * Initialize Commands here
     */
    public OperatorCommand(){
        shootCommand = new ShootCommand();
    }

    
    @Override
    public void initialize() {
        
    }


    /**
     * Set up bindings
     */
    @Override
    public void execute() {
        // TODO FIX
        if(oi.shouldShoot()){
            shootCommand.schedule();
        }else{
			shootCommand.cancel();
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
