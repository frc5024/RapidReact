package frc.robot.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.OI;
import frc.robot.commands.SubsystemCommands.IntakeCommand;
import frc.robot.commands.SubsystemCommands.SecondIntake;
import frc.robot.commands.SubsystemCommands.ShootCommand;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.RestrictedMotor;
import frc.robot.subsystem.Shooter;
import frc.robot.subsystem.RestrictedMotor.owner;
import io.github.frc5024.lib5k.logging.RobotLogger;

public class OperatorCommand extends CommandBase {
    
    private OI oi = OI.getInstance();

    // Define commands here
    private ShootCommand shootCommand;

    
    private IntakeCommand intakeCommand;

	private boolean manualFirstrun = true;

	private SecondIntake secondIntake = new SecondIntake();


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
		oi.setShootSetpoint();

        if(oi.shouldShoot()){
            shootCommand.schedule();
			
        }else if(!oi.shouldShoot() && Shooter.getInstance().getState() == Shooter.shooterState.FEED){
           
        }else{
			shootCommand.cancel();
		}




        if(OI.getInstance().shouldIntake() && !intakeCommand.isScheduled()){
            intakeCommand.schedule();
			
        }else if(!oi.shouldIntake() && intakeCommand.isScheduled()){
			intakeCommand.cancel();
			
		}

		if(OI.getInstance().shouldPullup()){
			secondIntake.schedule();
		}else{
			secondIntake.cancel();	
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
		

		shootCommand.cancel();
    }

}
