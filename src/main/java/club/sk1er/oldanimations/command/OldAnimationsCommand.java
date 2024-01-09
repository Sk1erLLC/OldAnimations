package club.sk1er.oldanimations.command;

import club.sk1er.oldanimations.OldAnimations;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.utils.GuiUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class OldAnimationsCommand extends Command {
    public OldAnimationsCommand() {
        super("oldanimations");
    }

    @Nullable
    @Override
    public Set<Alias> getCommandAliases() {
        Set<Alias> aliases = new HashSet<>();
        aliases.add(new Alias("oam", false));
        return aliases;
    }

    @DefaultHandler
    public void handle() {
        GuiUtil.open(Objects.requireNonNull(OldAnimations.oldAnimationsSettings.gui()));
    }
}
