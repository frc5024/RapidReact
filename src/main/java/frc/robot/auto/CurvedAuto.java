package frc.robot.auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.commands.SubsystemCommands.IntakeCommand;
import frc.robot.commands.SubsystemCommands.SecondIntake;
import frc.robot.commands.SubsystemCommands.ShootCommand;
import frc.robot.commands.autocommands.AutoRotate;
import frc.robot.commands.autocommands.AutoShoot;
import frc.robot.commands.autocommands.ControlledForward;
import io.github.frc5024.lib5k.autonomous.AutonomousSequence;

public class CurvedAuto implements AutonomousSequence{

	@Override
	public String getName() {
		return "Shot Steal auto";
	}

	@Override
	public CommandBase getCommand() {
		SequentialCommandGroup completeCommand = new SequentialCommandGroup();

		completeCommand.addCommands(new AutoShoot(Constants.Shooter.closeTargetRPM));

		

		completeCommand.addCommands(new AutoRotate(148).withTimeout(6));

		SequentialCommandGroup delayedDrive = new SequentialCommandGroup(new WaitCommand(.5), new ControlledForward(2));

		completeCommand.addCommands(new ParallelDeadlineGroup(new IntakeCommand().withTimeout(4), delayedDrive));

		completeCommand.addCommands(new SecondIntake());


		

		return completeCommand;
	}

	@Override
	public Pose2d getStartingPose() {
		
		return new Pose2d();
	}
	
}
