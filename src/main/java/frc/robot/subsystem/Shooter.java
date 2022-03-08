package frc.robot.subsystem;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.OI;
import frc.robot.subsystem.RestrictedMotor.owner;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonFX;
import io.github.frc5024.lib5k.hardware.generic.sensors.LineBreak;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.ExtendedSparkMax;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.RevMotorFactory;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

/**
 * Subsystem for controlling the Shooter
 * 
 * 
 * 
 * 
 */
public class Shooter extends SubsystemBase {

	private static Shooter mInstance = null;

	private RobotLogger logger;

	// Motors
	private ExtendedTalonFX flywheelMotor;

	// Shared motor
	private RestrictedMotor feedMotor;

	// Sensors
	private CommonEncoder flywheelEncoder;

	// Value for the target rpm
	private double targetRPM = Constants.Shooter.shootingTargetRPM;

	private PIDController shooterController;

	private Timer time = new Timer();

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

		// // Initialize flywheel motor
		this.flywheelMotor = CTREMotorFactory.createTalonFX(Constants.Shooter.flyWheelID,
				Constants.Shooter.flywheelConfig);
			flywheelMotor.setInverted(true);

		// // Setup flywheel encoder
		this.flywheelEncoder = flywheelMotor.getCommonEncoder(Constants.Shooter.encoderTPR);
		this.flywheelEncoder.setPhaseInverted(true);

		// // Get the shared motor instance
		this.feedMotor = RestrictedMotor.getInstance();

		// // PID Setup
		shooterController = new PIDController(Constants.Shooter.kP, Constants.Shooter.kI, Constants.Shooter.kD);
		shooterController.reset();

		// Setup Statemachine default state is idle
		stateMachine = new StateMachine<>("Shooter");

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
		SmartDashboard.putNumber("FLYWHEEL VELOCITY", flywheelEncoder.getVelocity());
		// if(OI.getInstance().shouldFeed()){
		// 	feedMotor.obtain(owner.SHOOTER);
		// 	feedMotor.set(Constants.Shooter.beltFeedSpeed, owner.SHOOTER);
		// }else{
		// 	feedMotor.set(0, owner.SHOOTER);
		// }
	}

	/**
	 * Method ran while Idle
	 */
	private void handleIdle(StateMetadata<shooterState> metaData) {
		if (metaData.isFirstRun()) {
			flywheelMotor.set(0);

			if (feedMotor.getCurrentOwner() == owner.SHOOTER) {
				feedMotor.free(owner.SHOOTER);
			}
		}

	}

	/**
	 * Method ran while ejecting
	 */
	private void handleEjecting(StateMetadata<shooterState> metaData) {
		if (metaData.isFirstRun()) {
			// Clears controller
			shooterController.reset();
			time.reset();
			time.start();
		}

		// Sets the motor until we are at target speed
		flywheelMotor.set(MathUtil.clamp(shooterController.calculate(getShooterRPM(), targetRPM), -1, 1));

		// At target switch state to feed
		
		if (atTarget(Constants.Shooter.ejectSetSpeed) ) {
			stateMachine.setState(shooterState.FEED);
		}
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
			// Clears controller
			shooterController.reset();
			time.reset();
			time.start();
			
		}

		// set the motor until we are at the appropriate speed
		//flywheelMotor.set(MathUtil.clamp(shooterController.calculate(getShooterRPM(), targetRPM), -1, 1));
		flywheelMotor.set(.95);
		// Switch to feeding
		if (atTarget(targetRPM) || time.hasElapsed(3)) {
			stateMachine.setState(shooterState.FEED);
		}

	}

	/**
	 * Method for feeding balls into the shooter
	 */
	private void handleFeeding(StateMetadata<shooterState> metaData) {
		feedMotor.free(owner.INTAKE);
		// If we are the owner, start spinning the ball
		if (feedMotor.getCurrentOwner() == owner.SHOOTER) {
			
			feedMotor.set(Constants.Shooter.beltFeedSpeed, owner.SHOOTER);

		} else {
			// Otherwise try to claim it
			logger.log("Trying to claim shared motor", Level.kInfo);
			feedMotor.obtain(owner.SHOOTER);

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

		// Stop and free the feed motor
		feedMotor.stopMotor(owner.SHOOTER);
		feedMotor.free(owner.SHOOTER);

		// Stop the flywheel
		flywheelMotor.set(0);

		// Switch to idle
		stateMachine.setState(shooterState.IDLE);
	}

	/**
	 * Checks if the system is done shooting
	 * 
	 * @return if the system is finished shooting
	 */
	public boolean isDoneShooting() {
		if (stateMachine.getCurrentState() == shooterState.FEED && !Intake.getInstance().hasBallStored()) {
			return true;
		}

		return false;

	}

	/**
	 * Get the rpm of the motor
	 * 
	 * @return flywheel motor rpm
	 */
	private double getShooterRPM() {

		return flywheelEncoder.getVelocity() * 600;
	}

	/**
	 * Check if our shooter speed is with our targets epsilon
	 * 
	 * @return if we are within the epsilon of our target
	 */
	private boolean atTarget(double target) {

		double currentRPM = getShooterRPM();

		// Check if our RPM is within epsilon
		if ((currentRPM > target - Constants.Shooter.shooterEpsilon)
				&& (currentRPM < target + Constants.Shooter.shooterEpsilon)) {

			logger.log("Flywheel at target RPM of: %d", Level.kRobot, String.valueOf(targetRPM));

			return true;
		}

		return false;

	}

}
