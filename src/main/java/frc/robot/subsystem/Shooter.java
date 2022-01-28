package frc.robot.subsystem;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.generic.sensors.LineBreak;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.ExtendedSparkMax;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.RevMotorFactory;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;


/**
 * Subsystem for controlling the Shooter
 */
public class Shooter extends SubsystemBase {

    private static Shooter mInstance = null;

    // Motors
    private ExtendedSparkMax flywheelMotor;

    // Shared motor
    private RestrictedMotor feedMotor;

    // Sensors
    private CommonEncoder flywheelEncoder;

    // Sensor for telling if the ball exists
    private LineBreak ballSensor;


    private enum shooterState{
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
    public static Shooter getInstance(){
        if(mInstance == null){
            mInstance = new Shooter();
        }

        return mInstance;
    }


    /**
	 * Constructor for the shooter
	 */
    private Shooter(){

        // Initialize flywheel motor
        this.flywheelMotor = RevMotorFactory.createSparkMax(Constants.Shooter.flyWheelID, Constants.Shooter.flywheelConfig);

        // Setup flywheel encoder
        this.flywheelEncoder = flywheelMotor.getCommonEncoder();
        this.flywheelEncoder.setPhaseInverted(true);

        // Get the shared motor instance
        this.feedMotor = RestrictedMotor.getInstance();

        

        // Setup Statemachine default state is idle
        stateMachine.setDefaultState(shooterState.IDLE, this::handleIdle);
        stateMachine.addState(shooterState.EJECTING, this::handleEjecting);
        stateMachine.addState(shooterState.TARGETING, this::handleTargeting);
        stateMachine.addState(shooterState.SPINNINGUP, this::handleSpinningUp);
        stateMachine.addState(shooterState.FEED, this::handleFeeding);


    }


    @Override
    public void periodic(){
        // Update statemachine
        stateMachine.update();
    }

    /**
     * Method ran while Idle
     */
    private void handleIdle(StateMetadata<shooterState> metaData){
        if(metaData.isFirstRun()){
            flywheelMotor.set(0);
        }

    }

    /**
     * Method ran while ejecting
     */
    private void handleEjecting(StateMetadata<shooterState> metaData){
        
    }

    /**
     * Method for handling targeting, this will be blank until we start using a limelight
     */
    private void handleTargeting(StateMetadata<shooterState> metaData){
        stateMachine.setState(shooterState.SPINNINGUP);
    }
    
    /**
     * Method for handling the spin up of the motor to shooting speeds
     */
    private void handleSpinningUp(StateMetadata<shooterState> metaData){
        
    }

    /**
     * Method for feeding balls into the shooter
     */
    private void handleFeeding(StateMetadata<shooterState> metaData){
        
    }

    /**
     * Method that sets the state to eject the ball from the shooter
     */
    public void eject(){
        
    }

    /**
     * Method to shoot the ball
     */
    public void shootBall(){

    }

    /**
     * Returns the system to idle
     */
    public void stop(){

    }

    /**
     * Returns system to idle, but notifies the intake subsystem of changes
     */
    private void finishShooting(){

    }

    /**
     * Checks if the system is done shooting
     * 
     * @return if the system is finished shooting
     */
    public boolean isDoneShooting(){
        return false;
    }
    

    


}
