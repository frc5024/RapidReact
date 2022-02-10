package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Shooter;

public class ShootCommand extends CommandBase {
    

    private Shooter shooter = Shooter.getInstance();


    @Override
    public void initialize() {
        shooter.shootBall();
    }






}
