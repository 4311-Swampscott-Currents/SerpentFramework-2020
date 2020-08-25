package frc.robot;

import org.swampscottcurrents.serpentframework.*;
import org.swampscottcurrents.serpentframework.logix.*;

import edu.wpi.first.wpilibj2.command.*;

public class Robot extends FastRobot {
    @Override
    public void robotStart() {
        LogixConditional.DefaultTimeProvider = this::getRobotTime;
    }
}
