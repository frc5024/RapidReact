package frc.robot.subsystem;

import edu.wpi.first.wpilibj2.command.SubsystemBase;


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
