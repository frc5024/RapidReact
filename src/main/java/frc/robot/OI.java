package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
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

	
	public void toggleManualOveride(){
		if(driverController.getLeftBumperPressed()){
			Intake.getInstance().toggleManualOveride();
		}
		
	}

	public void setShooterSetpoint(){
		switch (driverController.getPOV()) {
			case 270:
				Shooter.getInstance().setTarget(Constants.Shooter.lowGoalTargetRPM, Constants.Shooter.low_kP, Constants.Shooter.low_kI, Constants.Shooter.low_kD);
				RobotLogger.getInstance().log("Setting target RPM to: %.2f", Constants.Shooter.lowGoalTargetRPM);
				break;
			case 0:
				Shooter.getInstance().setTarget(Constants.Shooter.closeTargetRPM, Constants.Shooter.kP, Constants.Shooter.kI, Constants.Shooter.kD);
				RobotLogger.getInstance().log("Setting target RPM to: %.2f", Constants.Shooter.closeTargetRPM);
				break;
			case 90:
				Shooter.getInstance().setTarget(Constants.Shooter.lineShotTargetRPM, Constants.Shooter.kP, Constants.Shooter.kI, Constants.Shooter.kD);
				RobotLogger.getInstance().log("Setting target RPM to: %.2f", Constants.Shooter.lineShotTargetRPM);
				break;
		
			default:
				break;
		}

		
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
		SmartDashboard.putBoolean("Manual Overide", manualControl);
		return manualControl;
	}

	public double getOperatorSpeed(){
		double speed = 0;
        speed += operatorController.getRightTriggerAxis();
        speed -= operatorController.getLeftTriggerAxis();
        return speed / 2;
	}

	public boolean shouldInvertDriver(){
		return driverController.getXButtonPressed();
	}

	public boolean manualSetSolenoid(){
		return operatorController.getRightBumper();
	}

}

