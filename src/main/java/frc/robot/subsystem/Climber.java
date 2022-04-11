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
 * TODO possibly remove the deploying state as with the new code design it isn't needed
 */
public class Climber extends SubsystemBase {
	
	// Creating Instance
	private static Climber mInstance = null;

	// Creating Statemachine
	private StateMachine<climberState> stateMachine;

	private Timer climbDeployTimer;

	// Creating Motors and Sensors
	private ExtendedTalonSRX pullMotor;
	private DoubleSolenoid deploySolenoid;

	private HallEffect bottomSensor;

	// System states
	private enum climberState {
		Idle, // climber not in use
		Retracting, // arms are retracting and pulling the robot up
	}

	private boolean shouldFeedForward = false;
	
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
		// Initializes the state machine and adds the states
		stateMachine = new StateMachine<>("Climber Subsystem");

		stateMachine.setDefaultState(climberState.Idle, this::handleIdle);
		stateMachine.addState(climberState.Retracting, this::handleRetracting);
		
		// Initialize the motor
		pullMotor = CTREMotorFactory.createTalonSRX(Constants.Climb.climberID, Constants.Climb.climbConfig);

		// Add a current limit
		pullMotor.configSupplyCurrentLimit(
				new SupplyCurrentLimitConfiguration(true, Constants.Climb.climbConfig.peakAmps,
						Constants.Climb.climbConfig.holdAmps, 1));

		// Solenoid for the climber release
		deploySolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.Climb.pneumaticForward, Constants.Climb.pneumaticReverse);

		// Create the buttom sensor
		bottomSensor = new HallEffect(Constants.Climb.bottomHallEffectID);

		climbDeployTimer = new Timer();
	}

	@Override
	public void periodic() {
		stateMachine.update();
	
		// SmartDashboard.putBoolean("Bottom Sensor", bottomSensor.get());
		// SmartDashboard.putString("State", stateMachine.getCurrentState().toString());
		SmartDashboard.putBoolean("Bottom Sensor", shouldFeedForward);
		
	}

	private void handleIdle(StateMetadata<climberState> metadata) {
		// Stop Motor and Pin if climber is not in use
		if (metadata.isFirstRun()) {
			pullMotor.stopMotor();
			
		}

		// If operator deploys switch to deploying
		if (OI.getInstance().shouldClimbDeploy()) {
			stateMachine.setState(climberState.Retracting);
		}
	}

	private void handleRetracting(StateMetadata<climberState> metadata) {
		if(metadata.isFirstRun()){
			deploySolenoid.set(Value.kReverse);
		}

		if(OI.getInstance().shouldEnterHoldMode()){
			shouldFeedForward = !shouldFeedForward;
		}

		double voltsToSet = 0;

		if(shouldFeedForward){
		
			// Decides if what direction to set the climb and sets speed accordingly
			if (OI.getInstance().shouldExtendClimb()) {
				voltsToSet = 11;
			} else if(OI.getInstance().shouldRetractClimb() && !bottomSensor.get()){
				voltsToSet = 3;
			}else{
				voltsToSet = 6;
			}
		}else{
			// Decides if what direction to set the climb and sets speed accordingly
			if (OI.getInstance().shouldExtendClimb()) {
				voltsToSet = 9;
			} else if(OI.getInstance().shouldRetractClimb() && !bottomSensor.get()){
				voltsToSet = -3;
			}else{
				voltsToSet = 0;
			}
		}

		

		pullMotor.setVoltage(!bottomSensor.get() ? voltsToSet : 0);
	}

	/**
	 * Set the climber back to idle
	 */
	public void setIdle(){
		stateMachine.setState(climberState.Idle);
	}

}