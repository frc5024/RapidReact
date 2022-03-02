package frc.robot.auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.DriveTrain;
import io.github.frc5024.lib5k.autonomous.AutonomousSequence;
import io.github.frc5024.purepursuit.pathgen.Path;

/*
 * Move out of the Tarmac and shoot.
 */
public class MoveScore implements AutonomousSequence{

    private DriveTrain driveTrain = DriveTrain.getInstance();


    @Override
    public String getName() {
        
        return "Move and Shoot";
    }

    @Override
    public CommandBase getCommand() {
        
        SequentialCommandGroup completeCommand = new SequentialCommandGroup();

        completeCommand.addCommands(new InstantCommand(new Runnable() {
            @Override
            public void run() {
                driveTrain.resetPose(getStartingPose());
                
            }
        }));

        completeCommand.addCommands(driveTrain.createPathingCommand(new Path(getStartingPose().getTranslation(), new Translation2d(1, 0)), .1));
        
        


        return completeCommand;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(0, 0, new Rotation2d(0));
    }

    
}