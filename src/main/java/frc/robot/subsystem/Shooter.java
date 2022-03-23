package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.OI;
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
	private double targetRPM;

	private BangBangController shooterController;

	private Timer extraSpinTimer;

	private SimpleMotorFeedforward feedforward;

	private enum shooterState {
		IDLE,
		FEED,
		SPINNINGUP,
		TARGETING
	}

	private StateMachine<shooterState> stateMachine;

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

		// // Initialize flywheel motor
		this.flywheelMotor = CTREMotorFactory.createTalonFX(Constants.Shooter.flyWheelID,
				Constants.Shooter.flywheelConfig);
		
		flywheelMotor.setNeutralMode(NeutralMode.Coast);
		flywheelMotor.setInverted(true);

		// // Setup flywheel encoder
		this.flywheelEncoder = flywheelMotor.getCommonEncoder(Constants.Shooter.encoderTPR);
		this.flywheelEncoder.setPhaseInverted(true);
		this.flywheelEncoder.reset();

		// // Get the shared motor instance
		this.feedMotor = RestrictedMotor.getInstance();

		// Controller Setup
		shooterController = new BangBangController(50);

		//feedforward = new SimpleMotorFeedforward(ks, kv)
		

		// Setup Statemachine default state is idle
		stateMachine = new StateMachine<>("Shooter");

		stateMachine.setDefaultState(shooterState.IDLE, this::handleIdle);
		stateMachine.addState(shooterState.TARGETING, this::handleTargeting);
		stateMachine.addState(shooterState.SPINNINGUP, this::handleSpinningUp);
		stateMachine.addState(shooterState.FEED, this::handleFeeding);

		targetRPM = Constants.Shooter.lineShotTargetRPM;

		extraSpinTimer = new Timer();
		SmartDashboard.putBoolean("at Point", false);
		
	}

	@Override
	public void periodic() {
		// Update statemachine
		stateMachine.update();
		OI.getInstance().setShooterSetpoint();
		SmartDashboard.putNumber("Target Speed", targetRPM);
		SmartDashboard.putNumber("FLYWHEEL VELOCITY", getShooterRPM());
		SmartDashboard.putString("SHOOTER STATE", stateMachine.getCurrentState().toString());
	}

	/**
	 * Method ran while Idle
	 */
	private void handleIdle(StateMetadata<shooterState> metaData) {
		if (metaData.isFirstRun()) {
			flywheelMotor.set(0);
			isDoneShooting = false;

			if (feedMotor.getCurrentOwner() == owner.SHOOTER) {
				feedMotor.free(owner.SHOOTER);
			}
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
			
			shooterController.setSetpoint(targetRPM);
		}

		// set the motor until we are at the appropriate speed
		flywheelMotor.set(shooterController.calculate(getShooterRPM(), targetRPM));
		
		// Switch to feeding
		if (shooterController.atSetpoint()) {
			stateMachine.setState(shooterState.FEED);
			SmartDashboard.putBoolean("at Point", true);
		}
		

	}

	/**
	 * Method for feeding balls into the shooter
	 */
	private void handleFeeding(StateMetadata<shooterState> metaData) {
		if (metaData.isFirstRun()) {
		
			extraSpinTimer.reset();
			extraSpinTimer.start();
		}

		flywheelMotor.set(shooterController.calculate(getShooterRPM(), targetRPM));
		// If we are the owner, start spinning the ball
		if (feedMotor.getCurrentOwner() == owner.SHOOTER) {

			feedMotor.set(Constants.Shooter.beltFeedSpeed, owner.SHOOTER);

		} else {
			// Otherwise try to claim it
			logger.log("Trying to claim shared motor", Level.kInfo);
			feedMotor.obtain(owner.SHOOTER);

		}

		// if (!Intake.getInstance().ballSensorReading()) {
		//extraSpinTimer.start();
		// }

		if (extraSpinTimer.hasElapsed(3)) {
			extraSpinTimer.stop();
			stateMachine.setState(shooterState.IDLE);
			isDoneShooting = true;
		}

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
		return isDoneShooting;

	}

	/**
	 * Get the rpm of the motor
	 * 
	 * @return flywheel motor rpm
	 */
	private double getShooterRPM() {

		return (flywheelEncoder.getVelocity() * 1000 / .001666) * .714;
	}

	public void setTarget(double newRPM, double newP, double newI, double newD) {
		targetRPM = newRPM;
		//shooterController.setPID(newP, newI, newD);
	}

}
