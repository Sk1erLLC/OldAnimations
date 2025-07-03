package club.sk1er.oldanimations;

import club.sk1er.oldanimations.command.OldAnimationsCommand;
import club.sk1er.oldanimations.config.OldAnimationsSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = OldAnimations.MODID, name = "Sk1er Old Animations", version = OldAnimations.VERSION)
public class OldAnimations {

    public static final String MODID = "sk1er_old_animations";
    public static final String VERSION = "0.2.0";
    public static OldAnimationsSettings oldAnimationsSettings;

    public static GuiScreen screen = null;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        oldAnimationsSettings = new OldAnimationsSettings();
        oldAnimationsSettings.preload();

        MinecraftForge.EVENT_BUS.register(AnimationHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(SneakHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new OldAnimationsCommand());
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        if (screen != null) {
            Minecraft.getMinecraft().displayGuiScreen(screen);
            screen = null;
        }
    }
}
