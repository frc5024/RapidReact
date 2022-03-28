package frc.robot.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.OI;
import frc.robot.commands.SubsystemCommands.IntakeCommand;
import frc.robot.commands.SubsystemCommands.ShootCommand;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.RestrictedMotor;
import frc.robot.subsystem.Shooter;
import frc.robot.subsystem.RestrictedMotor.owner;
import io.github.frc5024.lib5k.logging.RobotLogger;

public class OperatorCommand extends CommandBase {
    
    private OI oi = OI.getInstance();

    // Define commands here
    private ShootCommand lowHubShootCommand;
	private ShootCommand highHubCloseShootCommand;
	private ShootCommand highHubFarShootCommand;

    
    private IntakeCommand intakeCommand;

	private boolean manualFirstrun = true;


    /**
     * Initialize Commands here
     */
    public OperatorCommand(){
        lowHubShootCommand = new ShootCommand(Constants.Shooter.lowGoalTargetRPM);
		highHubCloseShootCommand = new ShootCommand(Constants.Shooter.closeTargetRPM);
		highHubFarShootCommand = new ShootCommand(Constants.Shooter.lineShotTargetRPM);


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
        if(oi.shouldShootLowerHub() && !Shooter.getInstance().isDoneShooting()){
            lowHubShootCommand.schedule();
        }else{
            lowHubShootCommand.cancel();
        }

		if(oi.shouldShootClose() && !Shooter.getInstance().isDoneShooting()){
			highHubCloseShootCommand.schedule();
		}else{
			highHubCloseShootCommand.cancel();
		}

		if(oi.shouldShootFar() && !Shooter.getInstance().isDoneShooting()){
			highHubFarShootCommand.schedule();
		}else{
			highHubFarShootCommand.cancel();
		}



        if(OI.getInstance().shouldIntake() && !intakeCommand.isScheduled()){
            intakeCommand.schedule();
			
        }else if(!oi.shouldIntake() && intakeCommand.isScheduled()){
			intakeCommand.cancel();
			
		}



		OI.getInstance().toggleOperatorOverride();

		if(OI.getInstance().getManualOverride()){
			if(manualFirstrun){
				manualFirstrun = false;
				RobotLogger.getInstance().log("Entering manual overide");
				OI.getInstance().rumbleOperator(true);
			}
			if(oi.manualSetSolenoid()){
				Intake.getInstance().setSolenoid(Value.kForward);
			}else{
				Intake.getInstance().setSolenoid(Value.kReverse);
			}

			RestrictedMotor.getInstance().set(oi.getOperatorSpeed(), owner.OVERRIDE);
			
		}else{
			if(!manualFirstrun){
				manualFirstrun = true;
				RobotLogger.getInstance().log("Exiting manual overide");
				RestrictedMotor.getInstance().set(0, owner.OVERRIDE);
				Intake.getInstance().setSolenoid(Value.kReverse);
				OI.getInstance().rumbleOperator(false);
			}

			
		}

		if(OI.getInstance().togglePreheat()){
			Shooter.getInstance().togglePreheat();
		}
        
    }


    /**
     * Cancel all commands here
     */
    @Override
    public void end(boolean interrupted) {
		

		lowHubShootCommand.cancel();
        highHubCloseShootCommand.cancel();
		highHubFarShootCommand.cancel();
    }

}
