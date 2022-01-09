package frc.robot.subsystem;

import edu.wpi.first.wpilibj2.command.SubsystemBase;


/**
 * Subsystem for controlling the Shooter
 */
public class Shooter extends SubsystemBase {

    private static Shooter mInstance = null;

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

    }


    @Override
    public void periodic(){

    }


}
