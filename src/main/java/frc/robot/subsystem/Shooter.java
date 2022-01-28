package frc.robot.subsystem;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.subsystem.RestrictedMotor.owner;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.generic.sensors.LineBreak;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.ExtendedSparkMax;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.RevMotorFactory;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

/**
 * Subsystem for controlling the Shooter
 */
public class Shooter extends SubsystemBase {

    private static Shooter mInstance = null;

    private RobotLogger logger;

    // Motors
    private ExtendedSparkMax flywheelMotor;

    // Shared motor
    private RestrictedMotor feedMotor;

    // Sensors
    private CommonEncoder flywheelEncoder;

    // Sensor for telling if the ball exists
    private LineBreak ballSensor;

    private double targetRPM = 500;

    private PIDController shooterController;



    private enum shooterState {
        IDLE,
        FEED,
        EJECTING,
        SPINNINGUP,
        TARGETING
    }

    private StateMachine<shooterState> stateMachine;

    /**
     * Gets the instance for the shooter
     * 
     * @return Shooter instance
     */
    public static Shooter getInstance() {
        if (mInstance == null) {
            mInstance = new Shooter();
        }

        return mInstance;
    }

    /**
     * Constructor for the shooter
     */
    private Shooter() {
        // Initialize the logger
        logger = RobotLogger.getInstance();

        // Initialize flywheel motor
        this.flywheelMotor = RevMotorFactory.createSparkMax(Constants.Shooter.flyWheelID,
                Constants.Shooter.flywheelConfig);

        // Setup flywheel encoder
        this.flywheelEncoder = flywheelMotor.getCommonEncoder();
        this.flywheelEncoder.setPhaseInverted(true);

        // Get the shared motor instance
        this.feedMotor = RestrictedMotor.getInstance();

        // PID Setup
        shooterController = new PIDController(Constants.Shooter.kP, Constants.Shooter.kI, Constants.Shooter.kD);
        shooterController.reset();
        
        // Setup Statemachine default state is idle
        stateMachine.setDefaultState(shooterState.IDLE, this::handleIdle);
        stateMachine.addState(shooterState.EJECTING, this::handleEjecting);
        stateMachine.addState(shooterState.TARGETING, this::handleTargeting);
        stateMachine.addState(shooterState.SPINNINGUP, this::handleSpinningUp);
        stateMachine.addState(shooterState.FEED, this::handleFeeding);

    }

    @Override
    public void periodic() {
        // Update statemachine
        stateMachine.update();
    }

    /**
     * Method ran while Idle
     */
    private void handleIdle(StateMetadata<shooterState> metaData) {
        if (metaData.isFirstRun()) {
            flywheelMotor.set(0);
            shooterController.reset();

            if(feedMotor.getCurrentOwner() == owner.SHOOTER){
                feedMotor.free(owner.SHOOTER);
            }
        }
        

    }

    /**
     * Method ran while ejecting
     */
    private void handleEjecting(StateMetadata<shooterState> metaData) {

    }

    /**
     * Method for handling targeting, this will be blank until we start using a
     * limelight
     */
    private void handleTargeting(StateMetadata<shooterState> metaData) {
        stateMachine.setState(shooterState.SPINNINGUP);
    }

    /**
     * Method for handling the spin up of the motor to shooting speeds
     */
    private void handleSpinningUp(StateMetadata<shooterState> metaData) {
        if (metaData.isFirstRun()) {
            
        }

        flywheelMotor.set(shooterController.calculate(getShooterRPM(), targetRPM));

        if(atTarget()){
            stateMachine.setState(shooterState.FEED);
        }

    }

    /**
     * Method for feeding balls into the shooter
     */
    private void handleFeeding(StateMetadata<shooterState> metaData) {
        if(metaData.isFirstRun()){
            // Try to get the shared motor
            if(feedMotor.isFree()){
                feedMotor.obtain(owner.SHOOTER);
            }else{
                logger.log("Trying to handle feed, shooter can't claim motor", Level.kWarning);
            }
        }

        // If we are the owner, start spinning the ball
        if(feedMotor.getCurrentOwner() == owner.SHOOTER){
            feedMotor.set(.5, owner.SHOOTER);


        }else{
            // Otherwise try to claim it
            logger.log("Trying to claim shared motor", Level.kInfo);
            feedMotor.obtain(owner.SHOOTER);

        }


        // If we no longer detect the ball finish shooting
        if(!ballSensor.get()){
            finishShooting();
        }


    }

    /**
     * Method that sets the state to eject the ball from the shooter
     */
    public void eject() {
        stateMachine.setState(shooterState.EJECTING);
    }

    /**
     * Method to shoot the ball
     */
    public void shootBall() {
        stateMachine.setState(shooterState.TARGETING);
    }

    /**
     * Returns the system to idle
     */
    public void stop() {
        stateMachine.setState(shooterState.IDLE);
    }

    /**
     * Returns system to idle, but notifies the intake subsystem of changes
     */
    private void finishShooting() {

        // Stops the feed motor
        feedMotor.stopMotor(owner.SHOOTER);

        // Invoke some method to tell intake to switch states
        feedMotor.free(owner.SHOOTER);

        stateMachine.setState(shooterState.IDLE);
    }

    /**
     * Checks if the system is done shooting
     * 
     * @return if the system is finished shooting
     */
    public boolean isDoneShooting() {
        return false;
    }

    /**
     * Get the rpm of the motor
     * 
     * @return flywheel motor rpm
     */
    private double getShooterRPM() {

        return flywheelEncoder.getVelocity();
    }

    /**
     * Check if our shooter speed is with our targets epsilon
     * 
     * @return if we are within the epsilon of our target
     */
    private boolean atTarget() {

        double currentRPM = getShooterRPM();

        // Check if our RPM is within epsilon
        if ((currentRPM > targetRPM - Constants.Shooter.shooterEpsilon)
                && (currentRPM < targetRPM + Constants.Shooter.shooterEpsilon)){
                    
            logger.log("Flywheel at target RPM of: %d", Level.kRobot, String.valueOf(targetRPM));   
                    
            return true;
        }

        return false;
            
    }

}
