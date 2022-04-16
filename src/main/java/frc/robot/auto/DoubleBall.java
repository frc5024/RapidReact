package frc.robot.auto;

import javax.management.relation.RoleInfo;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.commands.SubsystemCommands.IntakeCommand;
import frc.robot.commands.SubsystemCommands.ShootCommand;
import frc.robot.commands.autocommands.AutoRotate;
import frc.robot.commands.autocommands.AutoShoot;
import frc.robot.commands.autocommands.ControlledForward;
import frc.robot.commands.autocommands.RollBack;
import frc.robot.commands.autocommands.RollForward;
import frc.robot.subsystem.DriveTrain;
import io.github.frc5024.lib5k.autonomous.AutonomousSequence;
import io.github.frc5024.purepursuit.pathgen.Path;

/*
 * Shoot, Turn to intake, Turn to shoot
 */
public class DoubleBall implements AutonomousSequence{


    @Override
    public String getName() {
        
        return "Double Ball!";
    }

    @Override
    public CommandBase getCommand() {
        
        SequentialCommandGroup completeCommand = new SequentialCommandGroup();

		completeCommand.addCommands(new WaitCommand(2));

        // Shoot ball High Shot
		completeCommand.addCommands(new AutoShoot(Constants.Shooter.closeTargetRPM));

        // Rotate 
        completeCommand.addCommands(new AutoRotate(180).withTimeout(3));


		SequentialCommandGroup delayedDrive = new SequentialCommandGroup(new WaitCommand(.5), new ControlledForward(1.3));

        // Parallel Intake Command with Drive Forwards Command
        completeCommand.addCommands(new ParallelDeadlineGroup(new IntakeCommand().withTimeout(5), delayedDrive));
		//completeCommand.addCommands(new ParallelDeadlineGroup(new IntakeCommand().withTimeout(5), new ControlledForward(1.3)));


        // Rotate
        completeCommand.addCommands(new AutoRotate(180));

        // Shoot ball High Shot
        completeCommand.addCommands(new AutoShoot(Constants.Shooter.lineShotTargetRPM));

        return completeCommand;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(0, 0, new Rotation2d(0));
    }

    
}