package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.OI;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.generic.sensors.HallEffect;
import io.github.frc5024.lib5k.hardware.generic.servos.SmartServo;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.RevMotorFactory;
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

    // Creating Motors and Sensors
    private ExtendedTalonSRX pullMotor;
    private SmartServo pin;

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

        pin = new SmartServo(Constants.Climb.smartServoChannel);
        addChild("Release", pin);

        bottomSensor = new HallEffect(Constants.Climb.bottomHallEffectID);
        topSensor = new HallEffect(Constants.Climb.topHallEffectID);

    }

    @Override
    public void periodic() {
        stateMachine.update();

    }

    private void handleIdle(StateMetadata<climberState> metadata) {
        // Stop Motor and Pin if climber is not in use
        if (metadata.isFirstRun()) {
            pullMotor.stopMotor();
            pin.stop();
        }

        // If operator deploys switch to deploying
        if (OI.getInstance().shouldClimbDeploy()) {
            stateMachine.setState(climberState.Deploying);

        }
    }

    private void handleDeploying(StateMetadata<climberState> metadata) {
        if (metadata.isFirstRun()) {
            // Release pin to send climber up
            pin.rip();
        }

        // Switch to retracting state once sensor tells us we are in the right spot
        // Stop the pin at the same time
        if (topSensor.get()) {
            pin.stop();
            stateMachine.setState(climberState.Retracting);
        }

    }

    private void handleRetracting(StateMetadata<climberState> metadata) {
        // If the bottom sensor tells us we are off the ground stop the motor
        if (bottomSensor.get()) {
            pullMotor.set(0);
            stateMachine.setState(climberState.FinishClimb);
            return;
        }

        // If done retracting stop the motor
        if (OI.getInstance().shouldRetractClimb()) {
            pullMotor.set(1);
        } else {
            pullMotor.stopMotor();
        }
    }

    private void handleFinishClimb(StateMetadata<climberState> metadata) {
        // Stop motor and win points
        if (metadata.isFirstRun()) {
            pullMotor.setNeutralMode(NeutralMode.Coast);
            pullMotor.set(0);
        }
    }

}
