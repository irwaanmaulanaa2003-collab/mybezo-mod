package com.mybezo.mod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class SlimefunLockerMixin {

    @Shadow protected Slot focusedSlot;
    @Shadow public abstract ScreenHandler getScreenHandler();

    private int lockedSlotId = -1;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_L && this.focusedSlot != null) {
            this.lockedSlotId = this.focusedSlot.id;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§a[Mybezo Mod] §fSlot " + this.lockedSlotId + " berhasil di-lock!"), true);
            }
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        long window = client.getWindow().getHandle();
        
        if (button == 0 && InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT)) {
            if (this.focusedSlot != null && this.lockedSlotId != -1 && client.interactionManager != null) {
                int sourceSlotId = this.focusedSlot.id;
                int syncId = this.getScreenHandler().syncId;
                
                client.interactionManager.clickSlot(syncId, sourceSlotId, 0, SlotActionType.PICKUP, client.player);
                client.interactionManager.clickSlot(syncId, this.lockedSlotId, 0, SlotActionType.PICKUP, client.player);
                client.interactionManager.clickSlot(syncId, sourceSlotId, 0, SlotActionType.PICKUP, client.player);

                cir.setReturnValue(true);
            }
        }
    }
}

