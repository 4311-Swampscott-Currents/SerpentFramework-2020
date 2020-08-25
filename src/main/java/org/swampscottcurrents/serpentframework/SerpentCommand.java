package org.swampscottcurrents.serpentframework;

import edu.wpi.first.wpilibj2.command.*;

/** Represents a subsystem with additional, customizable functionality. SerpentCommands call the reset method on each SerpentSubsystem they use after they finish. */
public class SerpentCommand extends CommandBase {
    /** Called when the command has finished. */
    public void finish(boolean interrupted) {

    }

    /** Called when a SerpentSubsystem needs to be reset. This may be overriden with custom behavior. */
    public void resetSubsystem(SerpentSubsystem system) {
        system.reset();
    }

    /** Called when the command exits. This command first calls finish, where custom behavior should be implemented, and then resets all associated SerpentSubsystems. */
    @Override
    public final void end(boolean interrupted) {
        finish(interrupted);
        for(Subsystem system : m_requirements) {
            if(system instanceof SerpentSubsystem) {
                resetSubsystem((SerpentSubsystem)system);
            }
        }
    }
}