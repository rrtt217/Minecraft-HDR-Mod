package xyz.rrtt217.HDRMod.core.color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.mixin.features.BossHealthOverlayAccessor;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BrightnessValueControl {
    public enum BrightnessValue {
        PAPERWHITE("text.autoconfig.hdr_mod.option.customGamePaperWhiteBrightness", 0.0F, 1000.0F){
            @Override
            public float getCurrentValue() {
                return HDRMod.colorManagementInfoProvider.getCurrentGamePaperWhiteBrightness(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            public void valueAdjust(float delta) {
                HDRModConfig config = HDRMod.configHolder.getConfig();
                if(config.customGamePaperWhiteBrightness < 0) config.customGamePaperWhiteBrightness = getCurrentValue() + delta;
                else config.customGamePaperWhiteBrightness += delta;
                if(config.customGamePaperWhiteBrightness < delta) config.customGamePaperWhiteBrightness = delta;
                HDRMod.configHolder.save();
            }
        },
        PEAK("text.autoconfig.hdr_mod.option.customGamePeakBrightness", 0.0F, 10000.0F){
            @Override
            public float getCurrentValue() {
                return HDRMod.colorManagementInfoProvider.getCurrentGamePeakBrightness(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            public void valueAdjust(float delta) {
                HDRModConfig config = HDRMod.configHolder.getConfig();
                if(config.customGamePeakBrightness < 0) config.customGamePeakBrightness = getCurrentValue() + delta;
                else config.customGamePeakBrightness += delta;
                if(config.customGamePeakBrightness < delta) config.customGamePeakBrightness = delta;
                HDRMod.configHolder.save();
            }
        },
        MINIMUM("text.autoconfig.hdr_mod.option.customGameMinimumBrightness", 0.0F, 1.0F){
            @Override
            public float getCurrentValue() {
                return HDRMod.colorManagementInfoProvider.getCurrentGameMinimumBrightness(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            public void valueAdjust(float delta) {
                HDRModConfig config = HDRMod.configHolder.getConfig();
                if(config.customGameMinimumBrightness < 0) config.customGameMinimumBrightness = getCurrentValue() + delta;
                else config.customGameMinimumBrightness += delta;
                if(config.customGameMinimumBrightness < 0) config.customGameMinimumBrightness = 0;
                HDRMod.configHolder.save();
            }
        },
        UI("text.autoconfig.hdr_mod.option.uiBrightness", 0.0F, 1000.0F){
            @Override
            public float getCurrentValue() {
                return HDRMod.colorManagementInfoProvider.getCurrentNonHudUIBrightness(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            public void valueAdjust(float delta) {
                HDRModConfig config = HDRMod.configHolder.getConfig();
                if(config.uiBrightness < 0) config.uiBrightness = getCurrentValue() + delta;
                else config.uiBrightness += delta;
                if(config.uiBrightness < delta) config.uiBrightness = delta;
                HDRMod.configHolder.save();
            }
        },
        HUD("text.autoconfig.hdr_mod.option.hudBrightness", 0.0F, 1000.0F){
            @Override
            public float getCurrentValue() {
                return HDRMod.colorManagementInfoProvider.getCurrentHudUIBrightness(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            public void valueAdjust(float delta) {
                HDRModConfig config = HDRMod.configHolder.getConfig();
                if(config.hudBrightness < 0) config.hudBrightness = getCurrentValue() + delta;
                else config.hudBrightness += delta;
                if(config.hudBrightness < delta) config.hudBrightness = delta;
                HDRMod.configHolder.save();
            }
        },
        EOTFEMULATE("text.autoconfig.hdr_mod.option.customEotfEmulate", 0.0F, 1000.0F){
            @Override
            public float getCurrentValue() {
                return HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            public void valueAdjust(float delta) {
                HDRModConfig config = HDRMod.configHolder.getConfig();
                if(config.customEotfEmulate < 0) config.customEotfEmulate = getCurrentValue() + delta;
                else config.customEotfEmulate += delta;
                if(config.customEotfEmulate < delta) config.customEotfEmulate = delta;
                HDRMod.configHolder.save();
            }
        };
        private final String translationKey;
        private final float minValue;
        private final float maxValue;

        BrightnessValue(String translationKey, float minValue, float maxValue) {
            this.translationKey = translationKey;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public float getMinValue() { return minValue; }

        public float getMaxValue() { return maxValue; }

        public float getCurrentValue() {
            return 0.0F;
        }

        public void valueAdjust(float delta){
        }
    }

    private static final List<BrightnessValue> valuesWithShortcut = List.of(BrightnessValue.PEAK, BrightnessValue.PAPERWHITE, BrightnessValue.UI, BrightnessValue.HUD);
    private static int currentIndex = 0;

    private static LerpingBossEvent currentEvent;
    public static int currentEventTick = 40;

    public static void valueAdjust(float delta){
        valuesWithShortcut.get(currentIndex).valueAdjust(delta);
        displayCurrentValue();
    }
    public static void valueSwitchAdjusted(){
        currentIndex = Math.floorMod(currentIndex + 1, valuesWithShortcut.size());
        displayCurrentValue();
    }
    public static void valueSwitchAdjustedBackwards(){
        currentIndex = Math.floorMod(currentIndex - 1, valuesWithShortcut.size());
        displayCurrentValue();
    }
    public static BrightnessValue getCurrentValueEnum(){
        return valuesWithShortcut.get(currentIndex);
    }

    public static float getCurrentValue(){
        return getCurrentValueEnum().getCurrentValue();
    }

    public static void displayCurrentValue(){
        // Minecraft.getInstance().gui.setOverlayMessage(Component.translatable(getCurrentValueEnum().translationKey).append(MessageFormat.format(": {0}", (int) getCurrentValue())),false);
        if(currentEvent == null){
            currentEvent = new LerpingBossEvent(
                    Mth.createInsecureUUID(),
                    Component.translatable(getCurrentValueEnum().translationKey).append(MessageFormat.format(": {0}", (int) getCurrentValue())),
                    getCurrentValue()/(getCurrentValueEnum().maxValue - getCurrentValueEnum().minValue),
                    BossEvent.BossBarColor.BLUE,
                    BossEvent.BossBarOverlay.PROGRESS,
                    false,false,false);
        }
        Map<UUID, LerpingBossEvent> events = ((BossHealthOverlayAccessor) Minecraft.getInstance().gui.getBossOverlay()).getEvents();
        if(events == null) return;
        if(!events.containsKey(currentEvent.getId())) events.put(currentEvent.getId(), currentEvent);

        currentEvent.setName(Component.translatable(getCurrentValueEnum().translationKey).append(MessageFormat.format(": {0}", (int) getCurrentValue())));
        currentEvent.setProgress(getCurrentValue()/(getCurrentValueEnum().maxValue - getCurrentValueEnum().minValue));
        currentEventTick = 40;
    }

    public static void consumeClick() {
        if(currentEvent == null) return;
        if(currentEventTick == 0){
            Map<UUID, LerpingBossEvent> events = ((BossHealthOverlayAccessor) Minecraft.getInstance().gui.getBossOverlay()).getEvents();
            if(events == null) return;
            events.remove(currentEvent.getId());
        }
        else currentEventTick--;
    }

    public static BrightnessValue getPreviousValueEnum(){
        return valuesWithShortcut.get(Math.floorMod(currentIndex - 1, valuesWithShortcut.size()));
    }
    public static BrightnessValue getNextValueEnum(){
        return valuesWithShortcut.get(Math.floorMod(currentIndex + 1, valuesWithShortcut.size()));
    }
}
