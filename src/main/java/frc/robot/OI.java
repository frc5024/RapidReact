package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import io.github.frc5024.lib5k.logging.RobotLogger;
import frc.robot.subsystem.DriveTrain;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.Shooter;

/**
 * OI is the class delegated the task of monitoring input from the controllers
 */
public class OI {
	// Treat oi as a singleton
    private static OI mInstance = null;
    
	// Controller variables
    private XboxController driverController;
    private XboxController operatorController;

	// Variable used to remember if we are in manual control
	// TODO move manual functionality to its own command
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
        // Initialize both controllers to read from the right port
        driverController = new XboxController(Constants.Controllers.driverController);
        operatorController = new XboxController(Constants.Controllers.operatorController);
    }

	/**
	 * Get the speed the driver wants to move at
	 * 
	 * @return the right trigger - the left trigger, [-1 - 1]
	 */
    public double getSpeed(){
        double speed = 0;
        speed += driverController.getRightTriggerAxis();
        speed -= driverController.getLeftTriggerAxis();
        return speed;
    }
    
    /**
	 * Gets the amount the driver wishes to turn
	 * 
	 * @return turning percentage [-1 - 1]
	 */
    public double getRotation(){
        
        return driverController.getLeftX();
       
    }

	/**
	 * Should the climber be deployed
	 * 
	 * @return should the climber be deployed
	 */
    public boolean shouldClimbDeploy(){
        return driverController.getStartButton() && driverController.getBackButton();
    }

	/**
	 * Should we pull the climber down
	 * 
	 * @return should the climber be pulled down
	 */
    public boolean shouldRetractClimb(){
        return driverController.getBButton();
	}
	
	/**
	 * 
	 * @return should the climber be extended
	 */
	public boolean shouldExtendClimb(){
		return driverController.getYButton();
	}

	/**
	 * Does the driver wish to deploy the intake
	 * 
	 * @return if the A button is down
	 */
    public boolean shouldIntake(){
        return driverController.getAButton();
    }

	/**
	 * Set the shooter set point based on the direction on the d-pad
	 */
	public void setShootSetpoint(){
		switch (operatorController.getPOV()) {
			case 270:
				Shooter.getInstance().setTarget(Constants.Shooter.RPMS.lowGoalTargetRPM, Constants.Shooter.RPMS.lowGoalName);
				break;

			case 0:
				Shooter.getInstance().setTarget(Constants.Shooter.RPMS.closeTargetRPM, Constants.Shooter.RPMS.closeGoalName);
				break;

			case 90:
				Shooter.getInstance().setTarget(Constants.Shooter.RPMS.lineShotTargetRPM, Constants.Shooter.RPMS.lineShotName);
				break;

			case 180:
				Shooter.getInstance().setTarget(Constants.Shooter.RPMS.longShot, Constants.Shooter.RPMS.longShotName);
				break;
		
			default:
				break;
		}
	}

	/**
	 * Should the shooter start to spin up
	 * 
	 * @return is the Y button held down
	 */
	public boolean shouldShoot(){
		return operatorController.getYButton();
	}

	/**
	 * Should the climber enter hold mode
	 * 
	 * @return has the left bumper been pressed
	 */
	public boolean shouldEnterReverse(){
		return driverController.getLeftBumper();
	}


	/**
	 * Toggles whether the operator has control over the intake
	 * 
	 * TODO this should be moved
	 */
	public void toggleOperatorOverride(){
		if(operatorController.getXButtonPressed()){
			manualControl = !manualControl;
			
		}
	}

	/**
	 * TODO needs to be removed
	 * 
	 * @return if we are in manual override
	 */
	public boolean getManualOverride(){
		return manualControl;
	}


	/**
	 * Gets the speed the operator would like to set the intake rollers to in manual mode
	 * 
	 * @return the right stick Y value
	 */
	public double getOperatorSpeed(){
		
		return operatorController.getRightY();
		
	}

	/*
	 * Inverts the direction the motors will travel
	 * 
	 */
	public boolean shouldInvertDriver(){
		return driverController.getXButtonPressed();
	}

	/**
	 * Rumbles the operators controller
	 * 
	 * @param rumble set rumble on or off
	 */
	public void rumbleOperator(boolean rumble){
		if(rumble){
			// Sets both sides to max rumble
			operatorController.setRumble(RumbleType.kLeftRumble, 1);
			operatorController.setRumble(RumbleType.kRightRumble, 1);
		}else{
			// Sets both sides to stop rumble
			operatorController.setRumble(RumbleType.kLeftRumble, 0);
			operatorController.setRumble(RumbleType.kRightRumble, 0);
		}


	}

	/**
	 * should we manually set the solenoid on
	 * 
	 * @return is the right bumper held down
	 */
	public boolean manualSetSolenoid(){
		return operatorController.getRightBumper();
	}

	/**
	 * Should we preheat
	 * 
	 * @return has the A button been pressed
	 */
	public boolean togglePreheat(){
		return operatorController.getAButtonPressed();
	}

}

