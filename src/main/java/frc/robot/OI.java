package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import io.github.frc5024.lib5k.utils.InputUtils;

public class OI {
    private static OI mInstance = null;
    
    private XboxController driverController;
    private XboxController operatorController;
    /**
	 * Gets the instance for the oi
	 * 
	 * @return OI instance
	 */
    public static OI getInstance(){
        if(mInstance == null){
            mInstance = new OI();
        }

        return mInstance;
    }
    /**
     * Contructor for the OI    
     */
    private OI(){
        
        driverController = new XboxController(Constants.Controllers.driverController);
        operatorController = new XboxController(Constants.Controllers.operatorController);
    }

    public double getSpeed(){
        double speed = 0;
        speed += driverController.getTriggerAxis(Hand.kRight);
        speed -= driverController.getTriggerAxis(Hand.kLeft);

        return speed;
    }
    
    
    public double getRotation(){
        
        return driverController.getX(Hand.kLeft);
       
    }



	
}
