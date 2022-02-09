package frc.robot.subsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.subsystem.RestrictedMotor.owner;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonFX;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.generic.cameras.AutoCamera;
import io.github.frc5024.lib5k.hardware.generic.sensors.LineBreak;
import io.github.frc5024.lib5k.logging.RobotLogger;
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

    private RestrictedMotor intakeMotor;

    private LineBreak retractSensor;

    private LineBreak ballSensor;

    private enum intakeState{
        ARMSTOWED,
        BALLSTOWED,
        INTAKING,
    }

    private StateMachine<intakeState> stateMachine;


    /**
	 * Gets the instance for the intake
	 * 
	 * @return Climber instance
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
        // Initialize the logger
        logger = RobotLogger.getInstance();

        // Setup the solenoid
        intakeSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.Intake.solenoidForward, Constants.Intake.solenoidReverse);

        // Initialize Restricted Motor
        this.intakeMotor = RestrictedMotor.getInstance();

		intakeCamera = new AutoCamera();

		intakeCamera.keepCameraAwake(true);
		intakeCamera.showCamera(true);

		stateMachine = new StateMachine<>("Intake");

        // Setup statemachine
        stateMachine.setDefaultState(intakeState.ARMSTOWED, this::handleArmStowed);
        stateMachine.addState(intakeState.BALLSTOWED, this::handleBallStowed);
        stateMachine.addState(intakeState.INTAKING, this::handleIntaking);

    }


    @Override
    public void periodic(){
        // Update statemachine
        stateMachine.update();
    }
    
    private void handleArmStowed(StateMetadata<intakeState> meta){
        if (meta.isFirstRun()) {
            retractArms();
        }


        
    }

    private void handleBallStowed(StateMetadata<intakeState> meta){
        if(meta.isFirstRun()){
            retractArms();
        }


        // If ball is no longer detected then set state to arms stowed
        if (!ballSensor.get()) {
            stateMachine.setState(intakeState.ARMSTOWED);
        }
    }

    private void handleIntaking(StateMetadata<intakeState> meta){
        if (meta.isFirstRun()) {
            intakeSolenoid.set(Value.kForward);
        }
        
        // Set the motor if we own in, otherwise try to claim it
        if (intakeMotor.getCurrentOwner() == owner.INTAKE) {
            intakeMotor.set(Constants.Intake.intakeSpeed, owner.INTAKE);
        } else {
            intakeMotor.obtain(owner.INTAKE);
        }
        

        // If ball is detected then stow it with the arms
        if (retractSensor.get()) {
            stateMachine.setState(intakeState.BALLSTOWED);
        }


    }

    /**
     * Method to intake a ball
     */
    public void intakeBall(){
        //If arms are stowed currently we want to change to intake state
        if (stateMachine.getCurrentState() == intakeState.ARMSTOWED){
            stateMachine.setState(intakeState.INTAKING);
        }

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
     */
    public boolean shouldRetract() {
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

}