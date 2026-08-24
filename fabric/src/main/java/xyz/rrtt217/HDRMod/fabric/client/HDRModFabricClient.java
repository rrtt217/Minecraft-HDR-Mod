package xyz.rrtt217.HDRMod.fabric.client;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.color.BrightnessValueControl;
import xyz.rrtt217.HDRMod.core.screenshot.PngjHDRScreenshot;

import static xyz.rrtt217.HDRMod.HDRMod.*;

public final class HDRModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register key mappings.
        KeyMappingHelper.registerKeyMapping(OPEN_CONFIG);
        KeyMappingHelper.registerKeyMapping(HDR_SCREENSHOT);
        KeyMappingHelper.registerKeyMapping(TOGGLE_HDR);
        KeyMappingHelper.registerKeyMapping(VALUE_UP);
        KeyMappingHelper.registerKeyMapping(VALUE_DOWN);
        KeyMappingHelper.registerKeyMapping(TOGGLE_VALUE_ADJUSTED);
        KeyMappingHelper.registerKeyMapping(TOGGLE_VALUE_ADJUSTED_BACKWARDS);
        // Register listeners.
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (OPEN_CONFIG.consumeClick()) {
                minecraft.gui.setScreen(AutoConfigClient.getConfigScreen(HDRModConfig.class, minecraft.gui.screen()).get());
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (HDR_SCREENSHOT.consumeClick()) {
                PngjHDRScreenshot.grab(minecraft.gameDirectory, minecraft.gameRenderer.mainRenderTarget(), (arg) -> minecraft.execute(() -> {
                    minecraft.gui.hud.getChat().addClientSystemMessage(arg);
                    minecraft.getNarrator().saySystemChatQueued(arg);
                }));
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (TOGGLE_HDR.consumeClick()) {
                HDRModConfig config = configHolder.getConfig();
                config.enableHDR = !config.enableHDR;
                configHolder.setConfig(config);
                configHolder.save();
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            HDRModConfig config = configHolder.getConfig();
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
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            BrightnessValueControl.consumeClick();
        });
    }
}
