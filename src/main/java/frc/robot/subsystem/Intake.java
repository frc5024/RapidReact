package frc.robot.subsystem;

import edu.wpi.first.wpilibj2.command.SubsystemBase;


/**
 * Subsystem for controlling the intake
 */
public class Intake extends SubsystemBase {

    private static Intake mInstance = null;

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

    }


    @Override
    public void periodic(){

    }


}