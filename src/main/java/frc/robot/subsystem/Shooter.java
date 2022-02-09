package frc.robot.subsystem;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;


/**
 * Subsystem for controlling the Shooter
 */
public class Shooter extends SubsystemBase {

    private static Shooter mInstance = null;


    private enum shooterState{
        IDLE,
        HOLDING,
        SPINNINGUP,
    }

    private StateMachine<shooterState> stateMachine;




    /**
	 * Gets the instance for the shooter
	 * 
	 * @return Shooter instance
	 */
    public static Shooter getInstance(){
        if(mInstance == null){
            mInstance = new Shooter();
        }

        return mInstance;
    }


    /**
	 * Constructor for the shooter
	 */
    private Shooter(){
        
		stateMachine = new StateMachine<>("Shooter");

        stateMachine.setDefaultState(shooterState.IDLE, this::handleIdle);
        stateMachine.addState(shooterState.HOLDING, this::handleHolding);
        stateMachine.addState(shooterState.SPINNINGUP, this::handleSpinningUp);





    }


    @Override
    public void periodic(){

    }
    

    private void handleIdle(StateMetadata<shooterState> meta){
        
    }

    private void handleHolding(StateMetadata<shooterState> meta){
        
    }

    private void handleSpinningUp(StateMetadata<shooterState> meta){
        
    }



    public void shootBall(){
        if(stateMachine.getCurrentState() == shooterState.HOLDING){
            
        }else{
            stateMachine.setState(shooterState.SPINNINGUP);
        }
    }

    public boolean isDoneShooting(){
        return false;
    }


}
