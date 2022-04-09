package frc.robot.commands.autocommands;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.RestrictedMotor;
import frc.robot.subsystem.RestrictedMotor.owner;

public class Feed extends CommandBase{

    RestrictedMotor motor = RestrictedMotor.getInstance();
    Intake intake = Intake.getInstance();

    @Override
    public void initialize() {
        motor.obtain(owner.OVERRIDE);
        intake.setSolenoid(Value.kForward);
    }

    @Override
    public void execute() {
        motor.set(-.5, owner.OVERRIDE);

    }

    @Override
    public void end(boolean interrupted) {
        motor.free(owner.OVERRIDE);
        intake.setSolenoid(Value.kReverse);
    }
    
}
