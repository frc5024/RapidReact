package frc.robot.commands.SubsystemCommands;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.RestrictedMotor;
import frc.robot.subsystem.Shooter;
import frc.robot.subsystem.RestrictedMotor.owner;

public class SecondIntake extends CommandBase{
	

	private Intake intake = Intake.getInstance();

	private RestrictedMotor motor = RestrictedMotor.getInstance();

	

	@Override
	public void initialize() {
		Shooter.getInstance().setPreheat();

		motor.obtain(owner.OVERRIDE);
		
	}

	@Override
	public void execute() {

		if(motor.getCurrentOwner() != owner.OVERRIDE){
			motor.obtain(owner.OVERRIDE);
		}else{
			motor.set(Constants.Intake.intakeSpeed, owner.OVERRIDE);
		}


		
	}

	@Override
	public boolean isFinished() {
		return !intake.ballSensorReading();
	}

	@Override
	public void end(boolean interrupted) {
		Shooter.getInstance().stop();
		motor.free(owner.OVERRIDE);
	}
}
