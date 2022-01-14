package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystem.DriveTrain;
import io.github.frc5024.lib5k.utils.InputUtils;


public class DriveCommand extends CommandBase {
    
    private DriveTrain driveTrain = DriveTrain.getInstance();
    private OI oi = OI.getInstance();

    public DriveCommand(){
        addRequirements(driveTrain);
        
    }


    @Override
    public void execute() {
        double rightSpeed = 0;
        double leftSpeed = 0;

        rightSpeed = oi.getSpeed() - oi.getRotation();
        leftSpeed = oi.getSpeed() + oi.getRotation();

        double maxSpeed = Math.max(Math.abs(rightSpeed), Math.abs(leftSpeed));

        rightSpeed /= maxSpeed;
        leftSpeed /= maxSpeed;


        driveTrain.drive(rightSpeed, leftSpeed);
        
        

        
    }

    @Override
    public boolean isFinished() {
        
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        
        driveTrain.drive(0, 0);
        

        super.end(interrupted);
    }

   


}
