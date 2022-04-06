package frc.robot.auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.SubsystemCommands.IntakeCommand;
import frc.robot.commands.SubsystemCommands.ShootCommand;
import frc.robot.subsystem.DriveTrain;
import io.github.frc5024.lib5k.autonomous.AutonomousSequence;
import io.github.frc5024.purepursuit.pathgen.Path;

/*
 * Shoot, Turn to intake, Turn to shoot
 */
public class DoubleBall implements AutonomousSequence{

    private DriveTrain driveTrain = DriveTrain.getInstance();


    @Override
    public String getName() {
        
        return "Double Ball!";
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

        completeCommand.addCommands(new ShootCommand());
        
        completeCommand.addCommands(driveTrain.createTurnCommand(new Rotation2d(180), new Rotation2d(2), .6, false));
        
        completeCommand.addCommands(driveTrain.createPathingCommand(new Path(getStartingPose().getTranslation(), new Translation2d(0.5, 0)), .1));

        completeCommand.addCommands(new IntakeCommand());

        completeCommand.addCommands(driveTrain.createTurnCommand(new Rotation2d(180), new Rotation2d(2), .6, false));

        completeCommand.addCommands(driveTrain.createPathingCommand(new Path(getStartingPose().getTranslation(), new Translation2d(0.5, 0)), .1));

        completeCommand.addCommands(new ShootCommand());

        return completeCommand;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(0, 0, new Rotation2d(0));
    }

    
}