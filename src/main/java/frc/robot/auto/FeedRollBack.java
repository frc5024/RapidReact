package frc.robot.auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.autocommands.Feed;
import frc.robot.commands.autocommands.RollForward;
import frc.robot.subsystem.DriveTrain;
import io.github.frc5024.lib5k.autonomous.AutonomousSequence;

public class FeedRollBack implements AutonomousSequence{
    private DriveTrain driveTrain = DriveTrain.getInstance();


    @Override
    public String getName() {
        
        return "Feed and leave";
    }

    @Override
    public CommandBase getCommand() {
        
        SequentialCommandGroup completeCommand = new SequentialCommandGroup();

		completeCommand.addCommands(new Feed().withTimeout(5));

        completeCommand.addCommands(new RollForward().withTimeout(1));

        

        return completeCommand;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(0, 0, new Rotation2d(0));
    }

    
}