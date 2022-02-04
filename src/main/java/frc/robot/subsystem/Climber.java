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

    private static Climber mInstance = null;

    private StateMachine<climberState> stateMachine;

        private ExtendedTalonSRX pullMotor;
        private SmartServo pin;
        private HallEffect bottomSensor;
        private HallEffect topSensor;

        // System states
        private enum climberState{
            Idle, // climber not in use
            Deploying, // arms are deployed for climb
            Retracting, // arms are retracting and pulling the robot up
            FinishClimb // robot has climed and is off the ground
        }

        // System positions
        private enum Position {
            Current, // Hold at current position
            Retracted, // Low Climb position
            Level, // High Climb position
        }


    /*
     * Gets the instance for the climber
     * 
     * @return Climber instance
     */
    public static Climber getInstance(){
        if(mInstance == null){
            mInstance = new Climber();
        }

        return mInstance;
    }
/**
     * Constructor for the climber
     */
    private Climber(){
        stateMachine = new StateMachine<>("Climber Subsystem");

        stateMachine.setDefaultState(climberState.Idle, this::handleIdle);
        stateMachine.addState(climberState.Deploying, this::handleDeploying);
        stateMachine.addState(climberState.Retracting, this::handleRetracting);
        stateMachine.addState(climberState.FinishClimb, this::handleFinishClimb);

        pullMotor = CTREMotorFactory.createTalonSRX(1);

        pin = new SmartServo(0);
        addChild("Release", pin);

        bottomSensor = new HallEffect(Constants.Climb.bottomHallEffectID);
        topSensor = new HallEffect(Constants.Climb.topHallEffectID);


    }


    @Override
    public void periodic(){
        stateMachine.update();

    }

    private void handleIdle(StateMetadata<climberState> metadata){
        if(metadata.isFirstRun()){
            pullMotor.stopMotor();
            pin.stop();
        }

        if(OI.getInstance().shouldDeployClimb()){
            stateMachine.setState(climberState.Deploying);

        }
    }

    private void handleDeploying(StateMetadata<climberState>metadata){
        if(metadata.isFirstRun()){
            pullMotor.stopMotor();
            pin.rip();
        }
        stateMachine.setState(climberState.Retracting);
    }
    // clear all sensors before retracting
    private void handleRetracting(StateMetadata<climberState>metadata){
        if(bottomSensor.get()){
            pullMotor.set(0);
            Position Current = getPosition();
            stateMachine.setState(climberState.FinishClimb);
            return;
        }


        if(OI.getInstance().shouldPullClimb()){
            pullMotor.set(1);
        } else {
            pullMotor.stopMotor();
        }
    }

    private void handleFinishClimb(StateMetadata<climberState>metadata){
        if(metadata.isFirstRun()){
            pullMotor.setNeutralMode(NeutralMode.Coast);
        }
    }

// public void setPosition(Position position) {
//     this.wantedPosition = position;
//     this.state = climberState.Retracting;
// }

    public Position getPosition() {
        if(topSensor.get()){
            return Position.Level;
        } else if (bottomSensor.get()) {
            return Position.Retracted;
        } else {
            return Position.Current;
        }
    }


}
