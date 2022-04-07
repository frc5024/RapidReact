package frc.robot.commands.autocommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.subsystem.DriveTrain;
import io.github.frc5024.lib5k.logging.RobotLogger;

public class RollForward extends CommandBase{
    
    private DriveTrain driveTrain;

    private RobotLogger logger;

    private double distance;

    private double initalRightDistance;
    private double initalLeftDistance;

    private double rightError = 1;
    private double leftError = 1;

    /** Tune these two variables */

    // The value the scale based on error this would be the M in y = mx + b
    private double speedValue = .5;

    // The constant speed to set the motor to this would be b in y = mx + b
    private double speedConstant = .2;


    /**
     * 
     * @param distance distance in meters you'd like to roll back
     */
    public RollForward(double distance){

        driveTrain = DriveTrain.getInstance();

        logger = RobotLogger.getInstance();

        addRequirements(driveTrain);

        this.distance = distance;
    }


    @Override
    public void initialize() {

        // Gets the initial distance traveled
        initalLeftDistance = driveTrain.getLeftMeters();
        initalRightDistance = driveTrain.getRightMeters();
    }

    @Override
    public void execute() {

        // Calculates how far we are
        rightError = (initalRightDistance + distance) - driveTrain.getRightMeters();
        leftError = (initalLeftDistance + distance) - driveTrain.getLeftMeters();

        // Speeds to set the motors too
        double rightSpeed = (Math.max((rightError / distance), .5) * speedValue) + speedConstant;
        double leftSpeed = (Math.max((leftError / distance), .5) * speedValue) + speedConstant;

        driveTrain.setSpeed(leftSpeed, rightSpeed);
        


    }

    @Override
    public boolean isFinished() {
        // If both sides are over the distance, you may want to switch this to a or instead of an and, but you'll need to see how it works irl
        return rightError <= 0 && leftError <= 0;
    }

    @Override
    public void end(boolean interrupted) {
        driveTrain.stop();
    }




}
