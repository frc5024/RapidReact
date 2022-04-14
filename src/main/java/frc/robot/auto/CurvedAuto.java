package frc.robot.auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.SubsystemCommands.IntakeCommand;
import frc.robot.commands.SubsystemCommands.ShootCommand;
import frc.robot.commands.autocommands.AutoRotate;
import frc.robot.commands.autocommands.ControlledForward;
import io.github.frc5024.lib5k.autonomous.AutonomousSequence;

public class CurvedAuto implements AutonomousSequence{

	@Override
	public String getName() {
		return "Curved Auto Path";
	}

	@Override
	public CommandBase getCommand() {
		SequentialCommandGroup completeCommand = new SequentialCommandGroup();

		completeCommand.addCommands(new ShootCommand());

		completeCommand.addCommands(new ControlledForward(-1.25));

		completeCommand.addCommands(new AutoRotate(40));

		completeCommand.addCommands(new ParallelDeadlineGroup(new IntakeCommand().withTimeout(4), new ControlledForward(.7)));

		completeCommand.addCommands(new AutoRotate(50));

		completeCommand.addCommands(new ShootCommand());

		return completeCommand;
	}

	@Override
	public Pose2d getStartingPose() {
		
		return new Pose2d();
	}
	
}
