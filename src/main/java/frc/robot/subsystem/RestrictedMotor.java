package frc.robot.subsystem;

import frc.robot.Constants;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;

/**
 * This class is designed to aid in the sharing of the intake motor between two subsystems
 * Currently the motor controller is a TalonSRX this may change as the robot is built
 */
public class RestrictedMotor{
    private static RestrictedMotor instance = null;
    
    // Logger instance
    private RobotLogger logger = RobotLogger.getInstance();

    // Shared motor
    private ExtendedTalonSRX sharedMotor;
    

    // Get instance
    public static RestrictedMotor getInstance(){
        if(instance == null){
            instance = new RestrictedMotor();
        }

        return instance;
    }

    // Each possible owner is in here, it is used for determining ownership
    public enum owner{
        NONE,
        SHOOTER,
        INTAKE,
    }

    // Who is the current owner
    private owner currentOwner = owner.NONE;

    /**
     * Gets the current motor owner
     * 
     * @return subsystem that currently owns the motor
     */
    public owner getCurrentOwner(){
        return currentOwner;
    }

    /**
     * Checks if there is any ownership to the motor
     * 
     * @return if the motor has an owner
     */
    public boolean isFree(){
        return currentOwner == owner.NONE ? true : false;
    }

    /**
     * Makes the motor avaliable for others to use, can only be done by current owner
     * 
     * @param user subsystem attempting to free the motor
     * 
     * @return whether it was successful or not
     */
    public boolean free(owner user){

        // if the user attempting to free owns allow it to free
        if(user == currentOwner){
            currentOwner = owner.NONE;
            logger.log("Share motor has been freed by: %s", user);  

            sharedMotor.set(0);

            return true;
        }


        logger.log("Attempted to free motor without ownership", Level.kWarning);  

        return false;
    }

    /**
     * Used to claim ownership of motor, must use free method after uses
     * 
     * @param user the subsystem claiming the motor
     * 
     * @return whether it was successful or not
     */
    public void obtain(owner user){

        // If the motor is free allow it to be claimed
        if(isFree()){
            currentOwner = user;

            logger.log("Motor ownership transferred to: %s", user);

            return;
        }
        
        logger.log("Attempted to obtain motor illegally", Level.kWarning);  

    }


    private RestrictedMotor(){
        this.sharedMotor = CTREMotorFactory.createTalonSRX(Constants.Intake.spinnerID, Constants.Intake.spinnerConfig);
        sharedMotor.set(0);
    }



    /**
     * Set the motor speed, if you are the current owner
     * 
     * @param value the value to set the motor to.
     * @param user the subsystem attempting to set the speed
     */
    public void set(double value, owner user){
        if(user == currentOwner){
            sharedMotor.set(value);

            return;
        }

        logger.log("Attempted to access motor without ownership", Level.kWarning);       
    }

    /**
     * Stops the motor if you are the current owner
     * 
     * @param user the subsystem attempting to stop
     */
    public void stopMotor(owner user){
        set(0, user);
    }


    














}
