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
        

    }


    @Override
    public void periodic(){
        





    }

}
