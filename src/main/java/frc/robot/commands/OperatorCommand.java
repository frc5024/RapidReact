package frc.robot.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.commands.SubsystemCommands.IntakeCommand;
import frc.robot.commands.SubsystemCommands.ShootCommand;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.RestrictedMotor;
import frc.robot.subsystem.Shooter;
import frc.robot.subsystem.RestrictedMotor.owner;

public class OperatorCommand extends CommandBase {
    
    private OI oi = OI.getInstance();

    // Define commands here
    private ShootCommand shootCommand;
    
    private IntakeCommand intakeCommand;

	private boolean manualFirstrun = true;


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
        if(oi.shouldShoot() && !Shooter.getInstance().isDoneShooting()){
            shootCommand.schedule();
        }else{
            shootCommand.cancel();
        }

        if(OI.getInstance().shouldIntake() && !intakeCommand.isScheduled()){
            intakeCommand.schedule();
			
        }else if(!oi.shouldIntake() && intakeCommand.isScheduled()){
			intakeCommand.cancel();
			
		}

		if(OI.getInstance().getManualOverride()){
			if(manualFirstrun){
				manualFirstrun = false;
			}
			if(oi.manualSetSolenoidForward()){
				Intake.getInstance().setSolenoid(Value.kForward);
			}else if(oi.manualSetSolenoidReverse()){
				Intake.getInstance().setSolenoid(Value.kReverse);
			}

			RestrictedMotor.getInstance().set(oi.getOperatorSpeed(), owner.OVERRIDE);
			
		}else{
			if(!manualFirstrun){
				manualFirstrun = true;
				RestrictedMotor.getInstance().set(0, owner.OVERRIDE);
				Intake.getInstance().setSolenoid(Value.kReverse);
			}
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
