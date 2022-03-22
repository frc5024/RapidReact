package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystem.DriveTrain;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.utils.InputUtils;
import io.github.frc5024.lib5k.utils.InputUtils.ScalingMode;

public class DriveCommand extends CommandBase {

    private DriveTrain driveTrain = DriveTrain.getInstance();
    private OI oi = OI.getInstance();

    public DriveCommand() {
        addRequirements(driveTrain);
    }

    @Override
    public void initialize() {
        driveTrain.stop();
    }

    @Override
    public void execute() {



		
        DriveTrain.getInstance().handleDriverInputs(oi.getSpeed(), oi.getRotation());
		
		// double leftSpeed = InputUtils.scale(oi.getSpeed(), ScalingMode.CUBIC) + InputUtils.scale(oi.getRotation(), ScalingMode.CUBIC);
		// double rightSpeed = InputUtils.scale(oi.getSpeed(), ScalingMode.CUBIC) - InputUtils.scale(oi.getRotation(), ScalingMode.CUBIC);

		// double max = Math.max(leftSpeed, rightSpeed);

		// if(max > 1){
		// 	leftSpeed /= max;
		// 	rightSpeed /= max;
		// }

		// DriveTrain.getInstance().setSpeed(leftSpeed, rightSpeed);

    }

    
    @Override
    public void end(boolean interrupted) {
        driveTrain.stop();
    }

    

}
