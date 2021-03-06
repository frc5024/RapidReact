package frc.robot.subsystem;


import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.OI;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.generic.sensors.HallEffect;
import io.github.frc5024.lib5k.hardware.generic.servos.SmartServo;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.RevMotorFactory;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;


/**
 * Subsystem for controlling the climber
 */
public class Climber extends SubsystemBase {

    private static Climber mInstance = null;

    private enum climberState{
        Deploying,
        Idle,
        Retracting,
        FinishClimb
    }

    private StateMachine<climberState> stateMachine;


    private ExtendedTalonSRX pullMotor;
    private SmartServo pin;
    
    private HallEffect bottomSensor;
    
    
    /**
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
        stateMachine.addState(climberState.FinishClimb, this::handleFinishClimb);
        stateMachine.addState(climberState.Retracting, this::handleRetract);


        pullMotor = CTREMotorFactory.createTalonSRX(1);

        pin = new SmartServo(0);

        bottomSensor = new HallEffect(2);

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
    
    private void handleDeploying(StateMetadata<climberState> metadata){
        if(metadata.isFirstRun()){
            pin.set(1);
        }
        
        stateMachine.setState(climberState.Retracting);
    }
    
    private void handleRetract(StateMetadata<climberState> metadata){

        if(bottomSensor.get()){
            pullMotor.set(0);
            stateMachine.setState(climberState.FinishClimb);
            return;
        }

        if(OI.getInstance().shouldPullClimb()){
            pullMotor.set(1);
        } else {
            pullMotor.stopMotor();
        }

    }
    
    private void handleFinishClimb(StateMetadata<climberState> metadata){
        if(metadata.isFirstRun()){
            
        }
    }
    
}
