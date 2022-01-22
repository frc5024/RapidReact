package frc.robot.auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.ShootCommand;
import frc.robot.subsystem.DriveTrain;
import io.github.frc5024.lib5k.autonomous.AutonomousSequence;

public class TestTurnPath implements AutonomousSequence {

    private DriveTrain driveTrain = DriveTrain.getInstance();

    @Override
    public String getName() {
        return "Drive and Turn Test Path";
    }

    @Override
    public CommandBase getCommand() {

        
        SequentialCommandGroup completeCommand = new SequentialCommandGroup();
        
        // Adding robots initial position
        completeCommand.addCommands(new InstantCommand(() -> {driveTrain.resetPose(getStartingPose());}));
        
        // Creating and adding turn command with desired parameters 
        completeCommand.addCommands(driveTrain.createTurnCommand(new Rotation2d(90), new Rotation2d(2), .6, false));
        

        //completeCommand.addCommands(new ShootCommand());


        return completeCommand; 
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(0, 0, new Rotation2d(0));
    }

}