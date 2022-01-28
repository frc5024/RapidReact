package frc.robot.subsystem;

import frc.robot.Constants;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;

/**
 * This class is designed to aid in the sharing of the intake motor between two subsystems
 */
public class RestrictedMotor{
    private static RestrictedMotor instance = null;
    
    private RobotLogger logger = RobotLogger.getInstance();


    private ExtendedTalonSRX sharedMotor;
    

    // Get Motor
    public static RestrictedMotor getInstance(){
        if(instance == null){
            instance = new RestrictedMotor();
        }

        return instance;
    }

    public enum owner{
        NONE,
        SHOOTER,
        INTAKE,
    }

    private owner currentOwner = owner.NONE;


    public owner getCurrentOwner(){
        return currentOwner;
    }


    public boolean isFree(){
        return currentOwner == owner.NONE ? true : false;
    }

    public void free(owner user){
        if(user == currentOwner){
            currentOwner = owner.NONE;
            logger.log("Share motor has been freed by: %s", user);  

            return;
        }

        logger.log("Attempted to free motor without ownership", Level.kWarning);  

    }

    public void obtain(owner user){
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




    public void set(double value, owner user){
        if(user == currentOwner){
            sharedMotor.set(value);

            return;
        }

        logger.log("Attempted to access motor without ownership", Level.kWarning);       
    }

    public void stopMotor(owner user){
        set(0, user);
    }


    














}
