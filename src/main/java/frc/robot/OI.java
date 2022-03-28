package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.utils.InputUtils;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystem.DriveTrain;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.Shooter;

public class OI {
    private static OI mInstance = null;
    
    private XboxController driverController;
    private XboxController operatorController;

	private boolean manualControl = false;

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
        speed += driverController.getRightTriggerAxis();
        speed -= driverController.getLeftTriggerAxis();
        return speed;
    }
    
    
    public double getRotation(){
        
        return driverController.getLeftX();
       
    }

    public boolean shouldClimbDeploy(){
        return driverController.getStartButton() && driverController.getBackButton();
    }

    public boolean shouldRetractClimb(){
        return driverController.getPOV() == 180;
    }

    public boolean shouldIntake(){
        return driverController.getAButton();
    }

	
	public void toggleManualOveride(){
		if(driverController.getLeftBumperPressed()){
			Intake.getInstance().toggleManualOveride();
		}
		
	}


	public boolean shouldShootLowerHub(){
		return operatorController.getPOV() == 270;
	}

	public boolean shouldShootClose(){
		return operatorController.getPOV() == 0;
	}

	public boolean shouldShootFar(){
		return operatorController.getPOV() == 90;
	}



	public void switchMotors(){
		if(driverController.getXButtonPressed()){
			DriveTrain.getInstance().invertMotors();
		}
	}

	public void toggleOperatorOverride(){
		if(operatorController.getXButtonPressed()){
			manualControl = !manualControl;
			
		}
	}

	public boolean getManualOverride(){
		return manualControl;
	}

	public double getOperatorSpeed(){
		
		return operatorController.getRightY();
		
	}

	public boolean shouldInvertDriver(){
		return driverController.getXButtonPressed();
	}

	public void rumbleOperator(boolean rumble){
		if(rumble){
			operatorController.setRumble(RumbleType.kLeftRumble, 1);
			operatorController.setRumble(RumbleType.kRightRumble, 1);
		}else{
			operatorController.setRumble(RumbleType.kLeftRumble, 0);
			operatorController.setRumble(RumbleType.kRightRumble, 0);
		}


	}

	public boolean manualSetSolenoid(){
		return operatorController.getRightBumper();
	}

	public boolean togglePreheat(){
		return operatorController.getAButtonPressed();
	}

}

