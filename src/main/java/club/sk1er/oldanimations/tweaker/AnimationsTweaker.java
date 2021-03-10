package club.sk1er.oldanimations.tweaker;

import club.sk1er.oldanimations.installer.ModCoreInstaller;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class AnimationsTweaker implements IFMLLoadingPlugin {

    public AnimationsTweaker() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.oldanimations.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        int initialize = ModCoreInstaller.initialize(Launch.minecraftHome, "1.8.9");

        if (ModCoreInstaller.isErrored() || initialize != 0 && initialize != -1) {
            // Technically wouldn't happen in simulated installed but is important for actual impl
            System.out.println("Failed to load Sk1er Modcore - " + initialize + " - " + ModCoreInstaller.getError());
        }

        // If true the classes are loaded
        if (ModCoreInstaller.isIsRunningModCore()) {
            return new String[]{"club.sk1er.mods.core.forge.ClassTransformer"};
        }

        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
