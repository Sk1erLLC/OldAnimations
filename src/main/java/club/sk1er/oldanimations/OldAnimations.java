package club.sk1er.oldanimations;

import club.sk1er.oldanimations.command.OldAnimationsCommand;
import club.sk1er.oldanimations.config.OldAnimationsSettings;
import club.sk1er.oldanimations.installer.ModCoreInstaller;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = OldAnimations.MODID, name = "Sk1er Old Animations", version = OldAnimations.VERSION)
public class OldAnimations {

    public static final String MODID = "sk1er_old_animations";
    public static final String VERSION = "1.0";
    public static OldAnimationsSettings oldAnimationsSettings;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);
        ClientCommandHandler.instance.registerCommand(new OldAnimationsCommand());
        oldAnimationsSettings = new OldAnimationsSettings();
        oldAnimationsSettings.preload();

        MinecraftForge.EVENT_BUS.register(AnimationHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(SneakHandler.getInstance());
    }
}
