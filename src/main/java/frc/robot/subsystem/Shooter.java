package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.subsystem.RestrictedMotor.owner;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonFX;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

/**
 * Subsystem for controlling the Shooter
 * 
 * The shooter uses a PID loop to get to and hold the correct speed
 * 
 * 
 */
public class Shooter extends SubsystemBase {
	// Set this class up as a singleton
	private static Shooter mInstance = null;

	// Robot logger instance
	private RobotLogger logger;

	// Main flywheel motor
	private ExtendedTalonFX flywheelMotor;

	// Shared feed/intake motor
	private RestrictedMotor feedMotor;

	// Sensors
	private CommonEncoder flywheelEncoder;

	// Value for the current target rpm
	private double targetRPM;

	// The name to display in network tables
	private String targetRPMName;

	// The PID controller used for controlling the motor
	private PIDController shooterController;

	// Extra time to spin the shooter after the ball has cleared
	// TODO use a combonation of intake encoder readings and Line break to tell if ball is all the way out
	private Timer extraSpinTimer;

	// Shooter states
	public enum shooterState {
		IDLE, // Default state, at rest
		FEED, // At Speed start feeding balls
		SPINNINGUP, // Spinning up to speed
		TARGETING, // Targeting with limelight not yet in use
		PREHEAT, // Preheating the flywheel to a set speed
	}

	// Configure the statemachine
	private StateMachine<shooterState> stateMachine;

	// Are we done shooting, used for ending the command
	private boolean isDoneShooting = false;

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
		this.flywheelMotor = CTREMotorFactory.createTalonFX(Constants.Shooter.flyWheelID,
				Constants.Shooter.flywheelConfig);
		
		// Sets the motor to coast mode so we don't fight
		flywheelMotor.setNeutralMode(NeutralMode.Coast);

		// Invert the motor so positive speeds move us in the right direction
		flywheelMotor.setInverted(true);

		// Setup flywheel encoder
		this.flywheelEncoder = flywheelMotor.getCommonEncoder(Constants.Shooter.encoderTPR);
		this.flywheelEncoder.setPhaseInverted(true);
		this.flywheelEncoder.reset();

		// Get the shared motor instance
		this.feedMotor = RestrictedMotor.getInstance();

		// Controller Setup
		shooterController = new PIDController(Constants.Shooter.kP, Constants.Shooter.kI, Constants.Shooter.kD);

		// Sets tolerances of PID loop
		shooterController.setTolerance(Constants.Shooter.shooterPositionTolerance, Constants.Shooter.shooterVelocityTolerance);
		

		// Setup Statemachine default state is idle
		stateMachine = new StateMachine<>("Shooter");

		// Adds all the states to the statemachine
		stateMachine.setDefaultState(shooterState.IDLE, this::handleIdle);
		stateMachine.addState(shooterState.TARGETING, this::handleTargeting);
		stateMachine.addState(shooterState.SPINNINGUP, this::handleSpinningUp);
		stateMachine.addState(shooterState.FEED, this::handleFeeding);
		stateMachine.addState(shooterState.PREHEAT, this::handlePreheat);

		// Sets the current set point and the name to display
		targetRPM = Constants.Shooter.RPMS.lineShotTargetRPM;
		targetRPMName = Constants.Shooter.RPMS.lineShotName;

		// Initializes the extra spin timer
		extraSpinTimer = new Timer();

		
		
	}

	@Override
	public void periodic() {
		// Update statemachine
		stateMachine.update();


		SmartDashboard.putString("Target Speed", targetRPMName);
		SmartDashboard.putNumber("FLYWHEEL VELOCITY", getShooterRPM());
		SmartDashboard.putString("SHOOTER STATE", stateMachine.getCurrentState().toString());
		SmartDashboard.putBoolean("In Preheat", stateMachine.getCurrentState() == shooterState.PREHEAT);
	}

	/**
	 * Method ran while Idle
	 */
	private void handleIdle(StateMetadata<shooterState> metaData) {
		// Reset everything and free the feed motor
		if (metaData.isFirstRun()) {
			flywheelMotor.set(0);
			isDoneShooting = false;

			if (feedMotor.getCurrentOwner() == owner.SHOOTER) {
				feedMotor.free(owner.SHOOTER);
			}
		}

	}


	private void handlePreheat(StateMetadata<shooterState> metadata){
		// Set the flywheel to its preheat voltage
		flywheelMotor.setVoltage(Constants.Shooter.preheatVoltage);
		
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
			// Clears controller and sets the tolerances
			shooterController.reset();
			shooterController.setSetpoint(targetRPM);
		}

		// set the motor until we are at the appropriate speed
		flywheelMotor.set(shooterController.calculate(getShooterRPM(), targetRPM));
		
		// Switch to feeding when at setpoint
		if (shooterController.atSetpoint()) {
			stateMachine.setState(shooterState.FEED);
			logger.log("At setpoint switching to feed");
		}
		

	}

	/**
	 * Method for feeding balls into the shooter
	 */
	private void handleFeeding(StateMetadata<shooterState> metaData) {
		// If we are are in auto, start a timer so we only shoot so long
		// TODO make it work on encoders and linebreak
		if (metaData.isFirstRun() && DriverStation.isAutonomous()) {
		
			extraSpinTimer.reset();
			extraSpinTimer.start();
		}

		// Continue to give the motor speeds
		flywheelMotor.set(shooterController.calculate(getShooterRPM(), targetRPM));

		// If we are the owner, start spinning the ball
		if (feedMotor.getCurrentOwner() == owner.SHOOTER) {

			feedMotor.set(Constants.Shooter.beltFeedSpeed, owner.SHOOTER);

		} else {
			// Otherwise try to claim it
			logger.log("Trying to claim shared motor", Level.kInfo);
			feedMotor.obtain(owner.SHOOTER);

		}

		// If we don't see the ball start the timer
		if (!Intake.getInstance().ballSensorReading()) {
			extraSpinTimer.start();
			logger.log("No ball detected starting timer");
		}

		// Once the timer has elapsed return to idle
		// this is poorly code but it will be changed so I will do that when make changes
		if (extraSpinTimer.hasElapsed(1)) {
			logger.log("Shot made winding down");
			extraSpinTimer.stop();
			extraSpinTimer.reset();
			stateMachine.setState(shooterState.IDLE);
			isDoneShooting = true;
		}

	}

	/**
	 * Method to shoot the ball
	 */
	public void shootBall() {
		logger.log("Shoot command issued");
		stateMachine.setState(shooterState.TARGETING);
	}

	

	/**
	 * Returns the system to idle
	 */
	public void stop() {
		logger.log("Stopping Shooter subsystem");

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
		return isDoneShooting;

	}

	/**
	 * Get the rpm of the motor
	 * 
	 * @return flywheel motor rpm
	 */
	private double getShooterRPM() {
		// Gets the velocity of the encoder corrects it so it is in RPM that factors in the gear ratio of the flywheel
		return (flywheelEncoder.getVelocity() * Constants.Shooter.velocityCorrectionFactor) * Constants.Shooter.shooterRatio;
	}

	/**
	 * Sets the new RPM of the shooter
	 * 
	 * @param newRPM new rpm set point
	 * @param name the name associated with the shot
	 */
	public void setTarget(double newRPM, String name) {
		targetRPM = newRPM;
		targetRPMName = name;
		logger.log("Setting Shooter Speed to: %.2f", newRPM);
		
	}

	/**
	 * Toggles between preheat and idle if we aren't in either state it is a No-op
	 */
	public void togglePreheat(){
		if(stateMachine.getCurrentState() == shooterState.PREHEAT){
			stateMachine.setState(shooterState.IDLE);
			logger.log("Toggling to IDLE");
		}else if(stateMachine.getCurrentState() == shooterState.IDLE){
			stateMachine.setState(shooterState.PREHEAT);
			logger.log("Toggling to Preheat");
		}
	}

	/**
	 * 
	 * @return the current state of the shooter
	 */
	public shooterState getState(){
		return stateMachine.getCurrentState();
	}
}