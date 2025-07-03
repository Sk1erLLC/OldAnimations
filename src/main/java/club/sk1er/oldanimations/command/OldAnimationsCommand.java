package club.sk1er.oldanimations.command;

import club.sk1er.oldanimations.OldAnimations;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.*;

public class OldAnimationsCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "oldanimations";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "oldanimations";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        OldAnimations.screen = OldAnimations.oldAnimationsSettings.gui();
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("oam");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
