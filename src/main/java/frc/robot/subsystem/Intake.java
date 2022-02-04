package frc.robot.subsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonFX;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.generic.sensors.LineBreak;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;


/**
 * Subsystem for controlling the intake
 */
public class Intake extends SubsystemBase {

    private static Intake mInstance = null;

    private ExtendedTalonSRX intakeMotor;

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
        
    }

    private void handleBallStowed(StateMetadata<intakeState> meta){
        // If ball is no longer detected then set state to arms stowed
        if (!ballSensor.get()) {
            stateMachine.setState(intakeState.ARMSTOWED);
        }
    }

    private void handleIntaking(StateMetadata<intakeState> meta){
        // If ball is detected then stow it with the arms
        if (ballSensor.get()) {
            stowBall();
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

    public void stowBall() {
        stateMachine.setState(intakeState.BALLSTOWED);

    }

    public void stowArms() {
        if (!ballSensor.get()) {
            stateMachine.setState(intakeState.ARMSTOWED);

        }
        
    }

    public boolean getBallReading() {
        return ballSensor.get();

    }



}