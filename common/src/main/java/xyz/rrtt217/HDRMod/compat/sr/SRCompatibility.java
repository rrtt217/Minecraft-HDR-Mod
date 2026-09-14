package xyz.rrtt217.HDRMod.compat.sr;

public class SRCompatibility {
    private static Boolean vulkanPresentationRequested;
    public static boolean isUsingVulkanPresentation(){
        if (vulkanPresentationRequested == null) {
            try {
                // Before 0.9.1-alpha.2
                Class<?> clazz = Class.forName("io.homo.superresolution.common.presentation.vulkan.VulkanPresentationFeature");
                vulkanPresentationRequested = (boolean) clazz.getMethod("isRequested").invoke(null);
            } catch (Throwable t) {
                try{
                    // 0.9.1-alpha.2
                    Class<?> clazz = Class.forName("io.homo.superresolution.common.presentation.PresentationBackendManager");
                    vulkanPresentationRequested = (boolean) clazz.getMethod("isVulkanPresentationRequested").invoke(null);
                }
                catch (Throwable t2){
                    vulkanPresentationRequested = false;
                }
            }
        }
        return vulkanPresentationRequested;
    }
}
