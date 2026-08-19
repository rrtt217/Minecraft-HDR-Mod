package xyz.rrtt217.HDRMod.core.color;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import java.text.MessageFormat;
import java.util.List;

public class BrightnessValueControl {
    public enum BrightnessValue {
        PAPERWHITE("text.autoconfig.hdr_mod.option.customGamePaperWhiteBrightness"){
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
        PEAK("text.autoconfig.hdr_mod.option.customGamePeakBrightness"){
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
        MINIMUM("text.autoconfig.hdr_mod.option.customGameMinimumBrightness"){
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
        UI("text.autoconfig.hdr_mod.option.uiBrightness"){
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
        HUD("text.autoconfig.hdr_mod.option.hudBrightness"){
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
        EOTFEMULATE("text.autoconfig.hdr_mod.option.customEotfEmulate"){
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

        BrightnessValue(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public float getCurrentValue() {
            return 0.0F;
        }

        public void valueAdjust(float delta){
        }
    }
    private static final List<BrightnessValue> valuesWithShortcut = List.of(BrightnessValue.PEAK, BrightnessValue.PAPERWHITE, BrightnessValue.UI, BrightnessValue.HUD);
    private static int currentIndex = 0;
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
        Minecraft.getInstance().gui.setOverlayMessage(Component.translatable(getCurrentValueEnum().translationKey).append(MessageFormat.format(": {0}", (int) getCurrentValue())),false);
    }

    public static BrightnessValue getPreviousValueEnum(){
        return valuesWithShortcut.get(Math.floorMod(currentIndex - 1, valuesWithShortcut.size()));
    }
    public static BrightnessValue getNextValueEnum(){
        return valuesWithShortcut.get(Math.floorMod(currentIndex + 1, valuesWithShortcut.size()));
    }
}
