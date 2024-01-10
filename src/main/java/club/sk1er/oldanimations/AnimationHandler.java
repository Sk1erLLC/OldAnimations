package club.sk1er.oldanimations;

import club.sk1er.oldanimations.config.OldAnimationsSettings;
import club.sk1er.oldanimations.mixins.ItemFoodAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

public class AnimationHandler {

    private static final AnimationHandler INSTANCE = new AnimationHandler();
    private final Minecraft mc = Minecraft.getMinecraft();

    public static AnimationHandler getInstance() {
        return INSTANCE;
    }

    public float prevSwingProgress;
    public float swingProgress;
    private int swingProgressInt;
    private boolean isSwingInProgress;

    /**
     * Interpolates swing time using partialTicks. Makes sure to account for possible negative values
     * If there is no swing override, use the default swing
     */
    public float getSwingProgress(float partialTickTime) {
        float currentProgress = this.swingProgress - this.prevSwingProgress;

        if (!isSwingInProgress) {
            return mc.thePlayer.getSwingProgress(partialTickTime);
        }

        if (currentProgress < 0.0F) {
            ++currentProgress;
        }

        return this.prevSwingProgress + currentProgress * partialTickTime;
    }

    /**
     * Gets the number of ticks to play the swing animation for
     */
    private int getArmSwingAnimationEnd(EntityPlayerSP player) {
        return player.isPotionActive(Potion.digSpeed) ? 5 - player.getActivePotionEffect(Potion.digSpeed).getAmplifier() :
            (player.isPotionActive(Potion.digSlowdown) ? 8 + player.getActivePotionEffect(Potion.digSlowdown).getAmplifier() * 2 : 6);
    }

    /**
     * Updates the swing progress, also enables swing if hitting a block
     */
    private void updateSwingProgress() {
        final EntityPlayerSP player = mc.thePlayer;
        if (player == null) {
            return;
        }

        prevSwingProgress = swingProgress;

        int max = getArmSwingAnimationEnd(player);

        if (OldAnimationsSettings.punching && mc.gameSettings.keyBindAttack.isKeyDown() &&
            mc.objectMouseOver != null &&
            mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (!this.isSwingInProgress || this.swingProgressInt >= max >> 1 || this.swingProgressInt < 0) {
                isSwingInProgress = true;
                swingProgressInt = -1;
            }
        }

        if (this.isSwingInProgress) {
            ++this.swingProgressInt;

            if (this.swingProgressInt >= max) {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        } else {
            this.swingProgressInt = 0;
        }

        this.swingProgress = (float) this.swingProgressInt / (float) max;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            updateSwingProgress();
        }
    }

    public void doSwordBlock3rdPersonTransform() {
        if (OldAnimationsSettings.oldSwordBlock3) {
            GlStateManager.translate(-0.15f, -0.2f, 0);
            GlStateManager.rotate(70, 1, 0, 0);
            GlStateManager.translate(0.119f, 0.2f, -0.024f);
        }
    }

    /**
     * Transforms the item to make it look like the player is holding it in first person,
     * replicating the 1.7 positioning
     * (This was a ****** nightmare to put together)
     *
     * @return Whether to perform the 1.8 First Person transform as well
     */
    public boolean doFirstPersonTransform(ItemStack stack) {
        switch (stack.getItemUseAction()) {
            case BOW:
                if (!OldAnimationsSettings.oldBow) return true;
                break;
            case EAT:
            case DRINK:
                if (!OldAnimationsSettings.oldEating) return true;
                break;
            case BLOCK:
                if (!OldAnimationsSettings.oldSwordBlock) return true;
                break;
            case NONE:
                if (stack.getItem() instanceof ItemFishingRod && OldAnimationsSettings.oldRod) {
                    // no-op to perform transforms
                } else if (!OldAnimationsSettings.oldModel) return true;
        }

        GlStateManager.translate(0.58800083f, 0.36999986f, -0.77000016f);
        GlStateManager.translate(0, -0.3f, 0.0F);
        GlStateManager.scale(1.5f, 1.5f, 1.5f);
        GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(335.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-0.9375F, -0.0625F, 0.0F);

        GlStateManager.scale(-2, 2, -2);

        if (mc.getRenderItem().shouldRenderItemIn3D(stack)) {
            GlStateManager.scale(0.58823526f, 0.58823526f, 0.58823526f);
            GlStateManager.rotate(-25, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(0, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(135, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0, -0.25f, -0.125f);
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            return true;
        }

        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        return false;
    }
}
