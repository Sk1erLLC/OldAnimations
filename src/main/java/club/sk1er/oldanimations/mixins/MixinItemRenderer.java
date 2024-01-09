package club.sk1er.oldanimations.mixins;

import club.sk1er.oldanimations.AnimationHandler;
import club.sk1er.oldanimations.config.OldAnimationsSettings;
import gg.essential.lib.mixinextras.injector.ModifyExpressionValue;
import gg.essential.lib.mixinextras.injector.WrapWithCondition;
import gg.essential.lib.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    @Shadow
    private ItemStack itemToRender;

    @Shadow
    private float equippedProgress;

    @Shadow private int equippedItemSlot;

    @Inject(method = "doBowTransformations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"))
    private void preBowScale(CallbackInfo ci) {
        if (OldAnimationsSettings.oldBow) {
            GlStateManager.rotate(-335.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-50.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.5F, 0.0F);
        }
    }

    @Inject(method = "doBowTransformations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V", shift = At.Shift.AFTER))
    private void postBowScale(CallbackInfo ci) {
        if (OldAnimationsSettings.oldBow) {
            GlStateManager.translate(0.0F, -0.5F, 0.0F);
            GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(335.0F, 0.0F, 0.0F, 1.0F);
        }
    }

    @SuppressWarnings("deprecation")
    @ModifyArg(method = "renderItemInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V"), index = 2)
    private ItemCameraTransforms.TransformType renderItem(ItemCameraTransforms.TransformType transform) {
        if (itemToRender == null) return transform;

        if (this.itemToRender.getItem().shouldRotateAroundWhenRendering()) {
            GlStateManager.rotate(180f, 0f, 1f, 0f);
        }

        if (AnimationHandler.getInstance().doFirstPersonTransform(this.itemToRender)) {
            return ItemCameraTransforms.TransformType.FIRST_PERSON;
        } else {
            return ItemCameraTransforms.TransformType.NONE;
        }
    }

    @ModifyArg(
            method = "renderItemInFirstPerson",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformFirstPersonItem(FF)V"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;performDrinking(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;doItemUsedTransformations(F)V")
            ),
            index = 1)
    private float modifySwingProgress(float swingProgress, @Local(ordinal = 0) float partialTicks) {
        if (OldAnimationsSettings.oldBlockhitting) {
            return AnimationHandler.getInstance().getSwingProgress(partialTicks);
        } else {
            return swingProgress;
        }
    }

    @ModifyExpressionValue(
            method = "renderItemInFirstPerson",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;getItemInUseCount()I")
    )
    private int blockHitOverride(int useCount) {
        EnumAction action = itemToRender.getItemUseAction();
        Item item = itemToRender.getItem();
        boolean blockHitOverride = false;
        if (OldAnimationsSettings.punching && useCount <= 0 && Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown()) {
            boolean block = action == EnumAction.BLOCK;
            boolean consume = false;
            if (item instanceof ItemFood) {
                boolean alwaysEdible = ((ItemFoodAccessor) item).getAlwaysEdible();
                if (Minecraft.getMinecraft().thePlayer.canEat(alwaysEdible)) {
                    consume = action == EnumAction.EAT || action == EnumAction.DRINK;
                }
            }

            if (block || consume) {
                blockHitOverride = true;
            }
        }
        if ((useCount > 0 || blockHitOverride) && Minecraft.getMinecraft().thePlayer.isUsingItem()) {
            return 1;
        } else {
            return 0;
        }
    }

    @WrapWithCondition(
            method = "renderItemInFirstPerson",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformFirstPersonItem(FF)V"),
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;performDrinking(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"))
    )
    private boolean captureTransform(ItemRenderer renderer, float equipProgress, float swingProgress) {
        return !OldAnimationsSettings.oldModel;
    }

    @ModifyArg(method = "updateEquippedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;clamp_float(FFF)F"), index = 0)
    private float handleItemSwitch(float original) {
        EntityPlayer entityplayer = Minecraft.getMinecraft().thePlayer;
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        if (OldAnimationsSettings.itemSwitch && this.equippedItemSlot == entityplayer.inventory.currentItem && ItemStack.areItemsEqual(this.itemToRender, itemstack)) {
            return 1.0f - this.equippedProgress;
        }
        return original;
    }
}
