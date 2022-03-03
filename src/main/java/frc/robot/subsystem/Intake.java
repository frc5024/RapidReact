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

	private Timer time = new Timer();

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
        // intakeCamera = new AutoCamera("Intake Camera", 0);
        // intakeCamera.keepCameraAwake(true);
        // intakeCamera.showCamera(true);

        // Initialize the logger
        logger = RobotLogger.getInstance();

        // Setup the solenoid
        intakeSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.Intake.solenoidForward, Constants.Intake.solenoidReverse);

        // Intitialize compressor
        compressor = new Compressor(Constants.Intake.compressorID, PneumaticsModuleType.CTREPCM);
        compressor.disable();

        // Initialize Restricted Motor
        this.intakeMotor = RestrictedMotor.getInstance();

		intakeCamera = new AutoCamera();

		intakeCamera.keepCameraAwake(true);
		intakeCamera.showCamera(true);

        retractSensor = new LineBreak(1);

        ballSensor = new LineBreak(2);

        stateMachine = new StateMachine<>("Intake");

        // Setup statemachine
        stateMachine.setDefaultState(intakeState.ARMSTOWED, this::handleArmStowed);
        stateMachine.addState(intakeState.INTAKING, this::handleIntaking);
		stateMachine.addState(intakeState.SPINDOWN, this::handleSpinDown);

    }


    @Override
    public void periodic(){
        // Update statemachine
        stateMachine.update();
    }
    
    private void handleArmStowed(StateMetadata<intakeState> meta){
        // Stow arms on first run
        if (meta.isFirstRun()) {
            retractArms();
        }
        
    }


    private void handleIntaking(StateMetadata<intakeState> meta){
        RobotLogger.getInstance().log("Handling intake");
        // Extend arms on first run
        if (meta.isFirstRun()) {
            intakeSolenoid.set(Value.kForward);
			
        }
       

        // Set the motor if we own it, otherwise try to claim it
        if (intakeMotor.getCurrentOwner() == owner.INTAKE) {
            intakeMotor.set(Constants.Intake.intakeSpeed, owner.INTAKE);
            RobotLogger.getInstance().log("Own motor");
        } else {
            RobotLogger.getInstance().log("Do not own motor");
            intakeMotor.obtain(owner.INTAKE);
        }
        
        // If ball is detected then stow it with the arms
        // if (retractSensor.get()) {
        //     stateMachine.setState(intakeState.BALLSTOWED);
        // }
		

    }

	private void handleSpinDown(StateMetadata<intakeState> meta){
		if(meta.isFirstRun()){

		}


	}

    /**
     * Method to intake a ball
     */
    public void intakeBall(){
        // If arms are stowed currently we want to change to intake state
        stateMachine.setState(intakeState.INTAKING);
        

    }

    /** 
     * Retracts the arms to idle
     */
    public void idle(){
        stateMachine.setState(intakeState.ARMSTOWED);
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
        return ballSensor.get();

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

	
}