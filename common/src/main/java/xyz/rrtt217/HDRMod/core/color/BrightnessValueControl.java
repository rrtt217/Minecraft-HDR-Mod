package xyz.rrtt217.HDRMod.core.color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.mixin.features.BossHealthOverlayAccessor;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.DoubleUnaryOperator;

public class BrightnessValueControl {
    public static class ProgressDisplayMapper{
        public static DoubleUnaryOperator rangeMapper(double inMin, double inMax, double outMin, double outMax, DoubleUnaryOperator mapper){
            double mappedInMin = mapper.applyAsDouble(inMin);
            double mappedInMax = mapper.applyAsDouble(inMax);
            double a = (outMax - outMin) / (mappedInMax - mappedInMin);
            double b = outMin - a * mappedInMin;
            DoubleUnaryOperator inOutAffineMapper = (x) -> (a * x + b);
            return mapper.andThen(inOutAffineMapper);
        }

        public static DoubleUnaryOperator rangeMapper(double inMin, double inMax, DoubleUnaryOperator mapper) {
            return rangeMapper(inMin, inMax, 0.0, 1.0, mapper);
        }

        static DoubleUnaryOperator logMapper(double base){
            return  (x) -> (Math.log(x) / Math.log(base));
        }

        static final double m1 = 2610.0 / 16384.0;
        static final double m2 = 2523.0f / 4096.0f * 128.0f;
        static final double c1 = 3424.0f / 4096.0f;         // 0.8359375
        static final double c2 = 2413.0f / 4096.0f * 32.0f; // 18.8515625
        static final double c3 = 2392.0f / 4096.0f * 32.0f; // 18.6875

        static DoubleUnaryOperator pqOetf = (o) -> {
            double Y = o / 10000.0;
            double Ypow = Math.pow(Y, m1);
            double num = c1 + c2 * Ypow;
            double den = 1.0f + c3 * Ypow;
            return Math.pow(num / den, m2);
        };

        public static DoubleUnaryOperator linear(double min, double max) {
            return rangeMapper(min, max, DoubleUnaryOperator.identity());
        }

        public static DoubleUnaryOperator log(double min, double max, double base) {
            return rangeMapper(min, max, logMapper(base));
        }

        public static DoubleUnaryOperator pq(double min, double max) {
            return rangeMapper(min, max, pqOetf);
        }
    }
    public enum BrightnessValue {
        PAPERWHITE("text.autoconfig.hdr_mod.option.customGamePaperWhiteBrightness", 15.0F, 1000.0F, ProgressDisplayMapper::pq){
            @Override
            protected double pullFromSystem() {
                return HDRMod.colorManagementInfoProvider.getCurrentGamePaperWhiteBrightness(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            protected float getConfigValue(HDRModConfig config) {
                return config.customGamePaperWhiteBrightness;
            }

            @Override
            protected void setConfigValue(HDRModConfig config, float value) {
                config.customGamePaperWhiteBrightness = value;
            }
        },
        PEAK("text.autoconfig.hdr_mod.option.customGamePeakBrightness", 100.0F, 5000.0F, ProgressDisplayMapper::pq){
            @Override
            protected double pullFromSystem() {
                return HDRMod.colorManagementInfoProvider.getCurrentGamePeakBrightness(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            protected float getConfigValue(HDRModConfig config) {
                return config.customGamePeakBrightness;
            }

            @Override
            protected void setConfigValue(HDRModConfig config, float value) {
                config.customGamePeakBrightness = value;
            }
        },
        MINIMUM("text.autoconfig.hdr_mod.option.customGameMinimumBrightness", 0.0F, 1.0F, ProgressDisplayMapper::pq){
            @Override
            protected double pullFromSystem() {
                return HDRMod.colorManagementInfoProvider.getCurrentGameMinimumBrightness(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            protected float getConfigValue(HDRModConfig config) {
                return config.customGameMinimumBrightness;
            }

            @Override
            protected void setConfigValue(HDRModConfig config, float value) {
                config.customGameMinimumBrightness = value;
            }
        },
        UI("text.autoconfig.hdr_mod.option.uiBrightness", 15.0F, 1000.0F, ProgressDisplayMapper::pq){
            @Override
            protected double pullFromSystem() {
                return HDRMod.colorManagementInfoProvider.getCurrentNonHudUIBrightness(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            protected float getConfigValue(HDRModConfig config) {
                return config.uiBrightness;
            }

            @Override
            protected void setConfigValue(HDRModConfig config, float value) {
                config.uiBrightness = value;
            }
        },
        HUD("text.autoconfig.hdr_mod.option.hudBrightness", 15.0F, 1000.0F, ProgressDisplayMapper::pq){
            @Override
            protected double pullFromSystem() {
                return HDRMod.colorManagementInfoProvider.getCurrentHudUIBrightness(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            protected float getConfigValue(HDRModConfig config) {
                return config.hudBrightness;
            }

            @Override
            protected void setConfigValue(HDRModConfig config, float value) {
                config.hudBrightness = value;
            }
        },
        EOTFEMULATE("text.autoconfig.hdr_mod.option.customEotfEmulate", 0.0F, 1000.0F){
            @Override
            protected double pullFromSystem() {
                return HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(Minecraft.getInstance().getWindow().handle());
            }

            @Override
            protected float getConfigValue(HDRModConfig config) {
                return config.customEotfEmulate;
            }

            @Override
            protected void setConfigValue(HDRModConfig config, float value) {
                config.customEotfEmulate = value;
            }
        };

        private final String translationKey;
        private final double minValue;
        private final double maxValue;
        private final DoubleUnaryOperator displayMapper;

        protected abstract double pullFromSystem();

        protected abstract float getConfigValue(HDRModConfig config);

        protected abstract void setConfigValue(HDRModConfig config, float value);

        BrightnessValue(String translationKey, double minValue, double maxValue, BiFunction<Double, Double, DoubleUnaryOperator> displayMapperProvider) {
            this.translationKey = translationKey;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.displayMapper = displayMapperProvider.apply(minValue, maxValue);
        }

        BrightnessValue(String translationKey, double minValue, double maxValue, DoubleUnaryOperator displayMapper) {
            this.translationKey = translationKey;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.displayMapper = displayMapper;
        }

        BrightnessValue(String translationKey, double minValue, double maxValue) {
            this.translationKey = translationKey;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.displayMapper = ProgressDisplayMapper.linear(minValue, maxValue);
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public double getCurrentValue() {
            HDRModConfig config = HDRMod.configHolder.getConfig();
            float value = getConfigValue(config);
            return value < 0 ? pullFromSystem() : value;
        }

        public void valueAdjust(double delta){
            setConfigValue(HDRMod.configHolder.getConfig(), (float) Math.min(Math.max(getCurrentValue() + delta, minValue), maxValue));
            HDRMod.configHolder.save();
        }

        public double getCurrentProgress(){
            return this.displayMapper.applyAsDouble(getCurrentValue());
        }

        public boolean isPullFromSystem(){
            return getConfigValue(HDRMod.configHolder.getConfig()) < 0.0F;
        }
    }

    private static final List<BrightnessValue> valuesWithShortcut = List.of(BrightnessValue.PEAK, BrightnessValue.PAPERWHITE, BrightnessValue.UI, BrightnessValue.HUD);
    private static int currentIndex = 0;

    private static LerpingBossEvent currentEvent;
    private static int currentEventTick = 40;

    public static int currentValueUpTick = 0;
    public static int currentValueDownTick = 0;

    public static void valueAdjust(double delta){
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

    public static double getCurrentValue(){
        return getCurrentValueEnum().getCurrentValue();
    }

    public static double getCurrentProgress(){return getCurrentValueEnum().getCurrentProgress(); }

    public static boolean isPullFromSystem(){
        return getCurrentValueEnum().isPullFromSystem();
    }

    public static void displayCurrentValue(){
        if(!HDRMod.configHolder.getConfig().displayBrightnessBar) {Minecraft.getInstance().gui.hud.setOverlayMessage(Component.translatable(getCurrentValueEnum().translationKey).append(MessageFormat.format(": {0}", (int) getCurrentValue())).append(isPullFromSystem() ? Component.translatable("text.hdr_mod.brightness_control.auto") : Component.empty()),false); return;}
        if(currentEvent == null){
            currentEvent = new LerpingBossEvent(
                    Mth.createInsecureUUID(RandomSource.create()),
                    Component.translatable(getCurrentValueEnum().translationKey).append(MessageFormat.format(": {0}", (int) getCurrentValue())).append(isPullFromSystem() ? Component.translatable("text.hdr_mod.brightness_control.auto") : Component.empty()),
                    (float) getCurrentProgress(),
                    BossEvent.BossBarColor.BLUE,
                    BossEvent.BossBarOverlay.PROGRESS,
                    false,false,false);
        }
        getCurrentValue();
        Map<UUID, LerpingBossEvent> events = ((BossHealthOverlayAccessor) Minecraft.getInstance().gui.hud.getBossOverlay()).getEvents();
        if(events == null) return;
        if(!events.containsKey(currentEvent.getId())) events.put(currentEvent.getId(), currentEvent);

        currentEvent.setName(Component.translatable(getCurrentValueEnum().translationKey).append(MessageFormat.format(": {0}", (int) getCurrentValue())).append(isPullFromSystem() ? Component.translatable("text.hdr_mod.brightness_control.auto") : Component.empty()));
        currentEvent.setProgress((float) getCurrentProgress());
        currentEventTick = 40;
    }

    public static void consumeClick() {
        if(currentEvent == null) return;
        if(currentEventTick == 0){
            Map<UUID, LerpingBossEvent> events = ((BossHealthOverlayAccessor) Minecraft.getInstance().gui.hud.getBossOverlay()).getEvents();
            if(events == null) return;
            events.remove(currentEvent.getId());
        }
        else currentEventTick--;
    }

    public static void clearUpTick(){
        currentValueUpTick = 0;
    }

    public static void clearDownTick(){
        currentValueDownTick = 0;
    }

    public static void stepUpTick(){
        if(currentValueUpTick < HDRMod.configHolder.getConfig().maxTicks) currentValueUpTick++;
    }
    public static void stepDownTick(){
        if(currentValueDownTick < HDRMod.configHolder.getConfig().maxTicks) currentValueDownTick++;
    }
}
