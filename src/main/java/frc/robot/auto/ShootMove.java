package frc.robot.auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.commands.SubsystemCommands.ShootCommand;
import frc.robot.commands.autocommands.AutoShoot;
import frc.robot.commands.autocommands.RollBack;
import frc.robot.subsystem.DriveTrain;
import io.github.frc5024.lib5k.autonomous.AutonomousSequence;
import io.github.frc5024.purepursuit.pathgen.Path;

public class ShootMove implements AutonomousSequence{

    private DriveTrain driveTrain = DriveTrain.getInstance();

    @Override
    public String getName() {
        return "Shoot and Move";
    }

    @Override
    public CommandBase getCommand() {
        
        SequentialCommandGroup completeCommand = new SequentialCommandGroup();


		completeCommand.addCommands(new AutoShoot(Constants.Shooter.lineShotTargetRPM));

        completeCommand.addCommands(new RollBack().withTimeout(1));

        


        return completeCommand;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(0, 0, new Rotation2d(0));
    }
    
}