package frc.robot.subsystem;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.OI;
import frc.robot.subsystem.RestrictedMotor.owner;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonFX;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.generic.cameras.AutoCamera;
import io.github.frc5024.lib5k.hardware.generic.sensors.LineBreak;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;


/**
 * Subsystem for controlling the intake
 */
public class Intake extends SubsystemBase {

    private static Intake mInstance = null;

	private AutoCamera intakeCamera;
	
	private RobotLogger logger;

    private DoubleSolenoid intakeSolenoid;

    private Compressor compressor;

    private RestrictedMotor intakeMotor;

    private LineBreak retractSensor;

    private LineBreak ballSensor;

	private boolean motorSpeedSet = false;
	
	private boolean hasBall;

	private Timer extraRollTime;

    private enum intakeState{
        ARMSTOWED,
        INTAKING,
		SPINDOWN,
    }

    private StateMachine<intakeState> stateMachine;


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

		
        retractSensor = new LineBreak(Constants.Intake.retractSensorID);

        ballSensor = new LineBreak(Constants.Intake.holdSensorID);

        stateMachine = new StateMachine<>("Intake");

        // Setup statemachine
        stateMachine.setDefaultState(intakeState.ARMSTOWED, this::handleArmStowed);
        stateMachine.addState(intakeState.INTAKING, this::handleIntaking);
		stateMachine.addState(intakeState.SPINDOWN, this::handleSpinDown);

		hasBall = false;

		extraRollTime = new Timer();

    }


    @Override
    public void periodic(){
        // Update statemachine
        stateMachine.update();


		SmartDashboard.putBoolean("Top Line Break", ballSensor.get());
		SmartDashboard.putBoolean("Bottom Line Break", retractSensor.get());

		SmartDashboard.putBoolean("Ball Detected", hasBall);
		SmartDashboard.putString("Intake State", stateMachine.getCurrentState().toString());
		OI.getInstance().switchBallState();

		if(retractSensor.get()){
			hasBall = true;
		}
    }
    
    private void handleArmStowed(StateMetadata<intakeState> meta){
        // Stow arms on first run
        if (meta.isFirstRun()) {
            retractArms();
        }
        
    }


    private void handleIntaking(StateMetadata<intakeState> meta){
        
        // Extend arms on first run
        if (meta.isFirstRun()) {
            intakeSolenoid.set(Value.kForward);
			motorSpeedSet = false;
        }
       
		
        // Set the motor if we own it, otherwise try to claim it
		
		if (intakeMotor.getCurrentOwner() != owner.INTAKE) {
				RobotLogger.getInstance().log("Do not own motor");
				intakeMotor.obtain(owner.INTAKE);
		} else {
				intakeMotor.set(Constants.Intake.intakeSpeed, owner.INTAKE);
				RobotLogger.getInstance().log("Own motor");
				motorSpeedSet = true;
		}
		

		// If ball is detected then stow it with the arms
		if (retractSensor.get()) {
			stateMachine.setState(intakeState.SPINDOWN);
		}
		
		

    }

	private void handleSpinDown(StateMetadata<intakeState> meta){
		if(meta.isFirstRun()){
			intakeSolenoid.set(Value.kReverse);
			extraRollTime.reset();
			extraRollTime.start();
		}

		if(extraRollTime.hasElapsed(2) || ballSensor.get()){
			extraRollTime.stop();
			stateMachine.setState(intakeState.ARMSTOWED);
			hasBall = true;
		}

		if (intakeMotor.getCurrentOwner() != owner.INTAKE) {
			RobotLogger.getInstance().log("Do not own motor");
			intakeMotor.obtain(owner.INTAKE);
		} else {
			intakeMotor.set(.2, owner.INTAKE);
			RobotLogger.getInstance().log("Own motor");
			motorSpeedSet = true;
	}



	}

    /**
     * Method to intake a ball
     */
    public void intakeBall(){
        // If arms are stowed currently we want to change to intake state
		if(!hasBall){
			stateMachine.setState(intakeState.INTAKING);
		}
        
        

    }

    /** 
     * Retracts the arms to idle
     */
    public void idle(){
        stateMachine.setState(intakeState.SPINDOWN);
    }

    /**
     * Method to retract arms and free motor
     */
    private void retractArms() {
        intakeSolenoid.set(Value.kReverse);
        intakeMotor.free(owner.INTAKE);
        
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
     * Method that returns if a ball 
     * is detected by the line break sensor
     */
    public boolean hasBallStored() {
        // return the ball sensor's reading
        return hasBall;

    }

	public boolean ballSensorReading(){
		return ballSensor.get();
	}

	public boolean canIntake(){
		return !retractSensor.get() && !(stateMachine.getCurrentState() == intakeState.SPINDOWN) && !hasBall;
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
     * Method to force change state to INTAKING
     */
    public void forceIntaking() {
        stateMachine.setState(intakeState.INTAKING);
    }

	public void switchBallState(){
		hasBall = !hasBall;
	}
	

	
}