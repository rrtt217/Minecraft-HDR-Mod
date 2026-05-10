package xyz.rrtt217.HDRMod.util.forge;

import net.minecraftforge.fml.loading.FMLConfig;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class SetupBeforeGLFWInitImpl {
    public static void setup(){
        // Update the config just before crash point.
        if (FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL)){
            FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL, false);
            Locale locale = Locale.getDefault();
            ResourceBundle bundle = ResourceBundle.getBundle("assets.hdr_mod.lang.early_messages", locale);
            if(GraphicsEnvironment.isHeadless()){
                throw new IllegalStateException(bundle.getString("early_window_error_message"));
            }else {
                JOptionPane.showMessageDialog(null, bundle.getString("early_window_error_message"), bundle.getString("early_window_error_title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
