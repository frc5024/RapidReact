package frc.robot.subsystem;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.subsystem.RestrictedMotor.owner;
import io.github.frc5024.lib5k.hardware.generic.cameras.AutoCamera;
import io.github.frc5024.lib5k.hardware.generic.sensors.LineBreak;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;


/**
 * Subsystem for controlling the intake
 */
public class Intake extends SubsystemBase {
    // Set up the subsystem as a singleton
    private static Intake mInstance = null;

    // Set up the logger
    private RobotLogger logger;

    // Set up the components

    // Webcam that looks into the intake
	private AutoCamera intakeCamera;
	
    // Solenoid used for controlling intake up and down
    private DoubleSolenoid intakeSolenoid;

    // Compressor for compressing obviously
    private Compressor compressor;

    // Shared intake/feed motor for use in intaking
    private RestrictedMotor intakeMotor;

    // Sensor for telling if we need to retract, this is further down
    private LineBreak retractSensor;

    // Sensor for telling if we have the ball, this is higher up
    private LineBreak ballSensor;

	private boolean spindownFinished;

    // Timer used to tract the extra spindown
    // TODO replace this with an encoder
	private Timer extraRollTime;

    // States of intaking
    private enum intakeState{
        ARMSTOWED,
        INTAKING,
		SPINDOWN,
        OUTPUT,
    }

    // Statemachine
    private StateMachine<intakeState> stateMachine;

    // Tracking the amount of times we deploy the arms
	private int intakeCount = 0;

    /**
	 * Gets the instance for the intake
	 * 
	 * @return Intake instance
	 */
    public static Intake getInstance(){
        if(mInstance == null){
            mInstance = new Intake();
        }

        return mInstance;

    }


    /**
	 * Constructor for the intake
	 */
    private Intake(){
		
        // Initialize the camera
        intakeCamera = new AutoCamera("Intake Camera", 0);
        intakeCamera.keepCameraAwake(true);
        intakeCamera.showCamera(true);

        // Initialize the logger
        logger = RobotLogger.getInstance();

        // Setup the solenoid
        intakeSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.Intake.solenoidForward, Constants.Intake.solenoidReverse);

        // Intitialize compressor
        compressor = new Compressor(Constants.Intake.compressorID, PneumaticsModuleType.CTREPCM);
        compressor.disable();

        // Initialize Restricted Motor
        this.intakeMotor = RestrictedMotor.getInstance();

		// Initialize the retract line break sensor
        retractSensor = new LineBreak(Constants.Intake.retractSensorID);

        // Initialize the ball line break sensor
        ballSensor = new LineBreak(Constants.Intake.holdSensorID);

        stateMachine = new StateMachine<>("Intake");

        // Setup statemachine
        stateMachine.setDefaultState(intakeState.ARMSTOWED, this::handleArmStowed);
        stateMachine.addState(intakeState.INTAKING, this::handleIntaking);
        stateMachine.addState(intakeState.OUTPUT, this::handleOutput);
		stateMachine.addState(intakeState.SPINDOWN, this::handleSpinDown);

        // Have we finished spinning down
		spindownFinished = false;

        // Initialize extra roll timer
		extraRollTime = new Timer();

    }


    @Override
    public void periodic(){
        // Update statemachine
        stateMachine.update();
		
		// Output the status of different subsystem sensors and states via smart dashboard.
		SmartDashboard.putBoolean("Top Line Break", ballSensor.get());
		SmartDashboard.putBoolean("Bottom Line Break", retractSensor.get());
		SmartDashboard.putNumber("Intake Count", intakeCount);
		
    }
    
    private void handleArmStowed(StateMetadata<intakeState> meta){
        // Stow arms on first run
        if (meta.isFirstRun()) {
			intakeSolenoid.set(Value.kReverse);
			intakeMotor.free(owner.INTAKE);
			spindownFinished = false;
        }
        
    }


    private void handleIntaking(StateMetadata<intakeState> meta){
        
        // Extend arms on first run
        if (meta.isFirstRun()) {
            intakeSolenoid.set(Value.kForward);

            // Increments the intake count
			intakeCount += 1;
			
        }
       
		
        // Set the motor if we own it, otherwise try to claim it
		if (intakeMotor.getCurrentOwner() != owner.INTAKE) {
				intakeMotor.obtain(owner.INTAKE);
		} else {
				intakeMotor.set(Constants.Intake.intakeSpeed, owner.INTAKE);
		}
		

		// If ball is detected then stow it with the arms
		if (retractSensor.get()) {
			stateMachine.setState(intakeState.SPINDOWN);
		}
		
		

    }

    private void handleOutput(StateMetadata<intakeState> meta){
        //Extend arms on first run
        if (meta.isFirstRun()){
            intakeSolenoid.set(Value.kForward);
            intakeCount += 1;
        }

        //Set the motor if we own it, otherwise try to claim it
        if (intakeMotor.getCurrentOwner() != owner.INTAKE) {
            intakeMotor.obtain(owner.INTAKE);
        } else {
                intakeMotor.set(Constants.Intake.outputSpeed, owner.INTAKE);
        }

    }

	private void handleSpinDown(StateMetadata<intakeState> meta){
        // Start spin down process on first run
		if(meta.isFirstRun()){
            intakeSolenoid.set(Value.kReverse);

            // Reset and start extra roll timer.
			extraRollTime.reset();
			extraRollTime.start();
		}

        // Obtain the intake motor, theoretically we should always have it at this point, but better safe than sorry
		if (intakeMotor.getCurrentOwner() != owner.INTAKE) {
			intakeMotor.obtain(owner.INTAKE);
		} else {
			intakeMotor.set(Constants.Intake.spinDownSpeed, owner.INTAKE);
		}

        // If we detect the ball exlusively on the hold sensor or the time elapses switch states
		if((ballSensor.get() && !retractSensor.get()) || extraRollTime.hasElapsed(2)){
			extraRollTime.stop();
			stateMachine.setState(intakeState.ARMSTOWED);
			spindownFinished = true;
			
		}


	}

    /**
     * Method to intake a ball
     */
    public void intakeBall(){
        // If arms are stowed currently we want to change to intake state
		stateMachine.setState(intakeState.INTAKING);
    }

    public void outputBall(){
        //If arms are stowed we want to change to output state
        stateMachine.setState(intakeState.OUTPUT);
    }

    /** 
     * Retracts the arms to idle
     */
    public void idle(){
        stateMachine.setState(intakeState.SPINDOWN);
    }

    /**
     * Method that checks if a ball is detected
     * and returns a boolean for if the arms should retract
     */
    public boolean shouldRetract() {
        // return sensor reading
        return retractSensor.get();
    }

    /**
     * 
     * @return the reading of the top line break
     */
	public boolean ballSensorReading(){
		return ballSensor.get();
	}

    /**
     * 
     * @return is the intake finished intaking
     */
	public boolean intakeFinished(){
		return spindownFinished;
	}

    /**
     * Method that disables the compressor
     */
    public void disableCompressor() {
        compressor.disable();
    }

    /**
     * Method that enables the compressor
     */
    public void enableCompressor() {
        compressor.enableDigital();
    }

    /**
     * Method to force change state to ARMSTOWED
     */
    public void forceArmStowed() {
        stateMachine.setState(intakeState.ARMSTOWED);
    }

    /**
     * Method that toggles the value of manual overide
     */
	
    /**
     * Method that returns wether or not
     * the current state is SPINDOWN
     */
	public boolean inSpinDown(){
		return stateMachine.getCurrentState() == intakeState.SPINDOWN;
	}

    /**
     * Method that sets the current state to SPINDOWN
     */
	public void spinDown(){
		stateMachine.setState(intakeState.SPINDOWN);
	}
	
    /**
     * Method that sets the intake solenoid's 
     * to a desired value
     */
	public void setSolenoid(Value value){
		intakeSolenoid.set(value);
	}
	
}