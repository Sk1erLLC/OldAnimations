package club.sk1er.oldanimations.command;

import club.sk1er.mods.core.ModCore;
import club.sk1er.oldanimations.OldAnimations;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class OldAnimationsCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "oldanimations";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        ModCore.getInstance().getGuiHandler().open(OldAnimations.oldAnimationsSettings.gui());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
