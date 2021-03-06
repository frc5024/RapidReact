package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.OI;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.generic.sensors.HallEffect;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;


/*
 * Subsystem for controlling the climber
 */
public class Climber extends SubsystemBase {

	

	// Creating Instance
	private static Climber mInstance = null;

	// Creating Statemachine
	private StateMachine<climberState> stateMachine;

	private Timer climbDeployTimer;

	// Creating Motors and Sensors
	private ExtendedTalonSRX pullMotor;
	private DoubleSolenoid pin;

	private HallEffect bottomSensor;
	private HallEffect topSensor;

	// System states
	private enum climberState {
		Idle, // climber not in use
		Deploying, // arms are deployed for climb
		Retracting, // arms are retracting and pulling the robot up
		FinishClimb // robot has climbed and is off the ground
	}

	/*
	 * Gets the instance for the climber
	 * 
	 * @return Climber instance
	 */
	public static Climber getInstance() {
		if (mInstance == null) {
			mInstance = new Climber();
		}

		return mInstance;
	}

	/**
	 * Constructor for the climber
	 */
	private Climber() {
		stateMachine = new StateMachine<>("Climber Subsystem");

		stateMachine.setDefaultState(climberState.Idle, this::handleIdle);
		stateMachine.addState(climberState.Deploying, this::handleDeploying);
		stateMachine.addState(climberState.Retracting, this::handleRetracting);
		stateMachine.addState(climberState.FinishClimb, this::handleFinishClimb);

		pullMotor = CTREMotorFactory.createTalonSRX(Constants.Climb.climberID, Constants.Climb.climbConfig);
		pullMotor.configSupplyCurrentLimit(
				new SupplyCurrentLimitConfiguration(true, Constants.Climb.climbConfig.peakAmps,
						Constants.Climb.climbConfig.holdAmps, 1));

		// climber release
		pin = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3);

		bottomSensor = new HallEffect(7);

		climbDeployTimer = new Timer();
	}

	@Override
	public void periodic() {
		stateMachine.update();
		// SmartDashboard.putString("State", stateMachine.getCurrentState().toString());
	
		SmartDashboard.putBoolean("Bottom Sensor", bottomSensor.get());
		SmartDashboard.putString("State", stateMachine.getCurrentState().toString());
		SmartDashboard.putBoolean("Sensor", bottomSensor.get());
	}

	private void handleIdle(StateMetadata<climberState> metadata) {
		// Stop Motor and Pin if climber is not in use
		if (metadata.isFirstRun()) {
			pullMotor.stopMotor();
			
		}

		// If operator deploys switch to deploying
		if (OI.getInstance().shouldClimbDeploy()) {
			stateMachine.setState(climberState.Deploying);
		}
	}

	private void handleDeploying(StateMetadata<climberState> metadata) {
		if (metadata.isFirstRun()) {
			// Release pin to send climber up
			pin.set(Value.kReverse);
			climbDeployTimer.reset();
			climbDeployTimer.start();
		}

		// Switch to retracting state once sensor tells us we are in the right spot
		// Stop the pin at the same time
		if (climbDeployTimer.hasElapsed(1)) {
			climbDeployTimer.stop();
			
			stateMachine.setState(climberState.Retracting);
		}

	}

	private void handleRetracting(StateMetadata<climberState> metadata) {
		// If the bottom sensor tells us we are off the ground stop the motor
		if(metadata.isFirstRun()){
			pin.set(Value.kReverse);
		}

		// If done retracting stop the motor
		if (OI.getInstance().shouldRetractClimb() && !bottomSensor.get()) {
			// positive number for climb
			pullMotor.set(.9);
		} else {
			pullMotor.stopMotor();
		}

		if(bottomSensor.get()){
			pullMotor.stopMotor();
			stateMachine.setState(climberState.FinishClimb);
		}
	}

	private void handleFinishClimb(StateMetadata<climberState> metadata) {
		// Stop motor and win points
		if (metadata.isFirstRun()) {
			pullMotor.setNeutralMode(NeutralMode.Coast);
			pullMotor.set(0);
			pullMotor.stopMotor();
		}
	}

	public void setIdle(){
		stateMachine.setState(climberState.Idle);
	}

}
