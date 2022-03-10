package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.utils.InputUtils;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.Shooter;

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
        speed += driverController.getRightTriggerAxis();
        speed -= driverController.getLeftTriggerAxis();
        return speed;
    }
    
    
    public double getRotation(){
        
        return driverController.getLeftX();
       
    }


    public boolean shouldShoot(){
        return driverController.getYButton();
    }

    public boolean shouldClimbDeploy(){
        return operatorController.getStartButton() && operatorController.getBackButton();
    }

    public boolean shouldRetractClimb(){
        return operatorController.getPOV() == 180;
    }

    public boolean shouldIntake(){
        return driverController.getAButton();
    }

	
	public void switchBallState(){
		if(driverController.getLeftBumperPressed()){
			Intake.getInstance().switchBallState();
		}
		
	}

	public void setShooterSetpoint(){
		switch (driverController.getPOV()) {
			case 270:
				Shooter.getInstance().setTargetRPM(Constants.Shooter.lowGoalTargetRPM);
				RobotLogger.getInstance().log("Setting target RPM to: %.2f", Constants.Shooter.lowGoalTargetRPM);
				break;
			case 0:
				Shooter.getInstance().setTargetRPM(Constants.Shooter.closeTargetRPM);
				RobotLogger.getInstance().log("Setting target RPM to: %.2f", Constants.Shooter.closeTargetRPM);
				break;
			case 90:
				Shooter.getInstance().setTargetRPM(Constants.Shooter.lineShotTargetRPM);
				RobotLogger.getInstance().log("Setting target RPM to: %.2f", Constants.Shooter.lineShotTargetRPM);
				break;
		
			default:
				break;
		}

		
	}


}
