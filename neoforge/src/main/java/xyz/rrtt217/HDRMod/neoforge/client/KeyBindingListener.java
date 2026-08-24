package xyz.rrtt217.HDRMod.neoforge.client;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.color.BrightnessValueControl;
import xyz.rrtt217.HDRMod.core.screenshot.PngjHDRScreenshot;

import static xyz.rrtt217.HDRMod.HDRMod.*;
import static xyz.rrtt217.HDRMod.HDRMod.minecraft;

@EventBusSubscriber(modid = HDRMod.MOD_ID)
public class KeyBindingListener {
    @SubscribeEvent // on the game event bus only on the physical client
    public static void onClientTick(ClientTickEvent.Post event) {
        HDRModConfig config = configHolder.getConfig();
        while (OPEN_CONFIG.consumeClick()) {
            minecraft.gui.setScreen(AutoConfigClient.getConfigScreen(HDRModConfig.class, minecraft.gui.screen()).get());
        }
        while (HDR_SCREENSHOT.consumeClick()) {
            PngjHDRScreenshot.grab(minecraft.gameDirectory, minecraft.gameRenderer.mainRenderTarget(), (arg) -> minecraft.execute(() -> {
                minecraft.gui.hud.getChat().addClientSystemMessage(arg);
                minecraft.getNarrator().saySystemChatQueued(arg);
            }));
        }
        while (TOGGLE_HDR.consumeClick()) {
            config.enableHDR = !config.enableHDR;
            configHolder.setConfig(config);
            configHolder.save();
        }
        while (TOGGLE_VALUE_ADJUSTED.consumeClick()) {
            BrightnessValueControl.valueSwitchAdjusted();
        }
        while (TOGGLE_VALUE_ADJUSTED_BACKWARDS.consumeClick()) {
            BrightnessValueControl.valueSwitchAdjustedBackwards();
        }
        if(!VALUE_DOWN.isDown()) BrightnessValueControl.clearDownTick(); else BrightnessValueControl.stepDownTick();
        if(!VALUE_UP.isDown()) BrightnessValueControl.clearUpTick(); else BrightnessValueControl.stepUpTick();
        while (VALUE_UP.consumeClick()) {
            if(config.roundStepToInitial) {BrightnessValueControl.valueAdjust((Math.round(BrightnessValueControl.currentValueUpTick * config.timeFactor / config.initialStep) + 1) * config.initialStep);}
            else BrightnessValueControl.valueAdjust(Math.round(BrightnessValueControl.currentValueUpTick * config.timeFactor + config.initialStep));
        }
        while (VALUE_DOWN.consumeClick()) {
            if(config.roundStepToInitial) {BrightnessValueControl.valueAdjust((Math.round( - BrightnessValueControl.currentValueDownTick * config.timeFactor / config.initialStep) - 1) * config.initialStep);}
            else BrightnessValueControl.valueAdjust(Math.round(-BrightnessValueControl.currentValueDownTick * config.timeFactor - config.initialStep));
        }
        BrightnessValueControl.consumeClick();
    }
}
