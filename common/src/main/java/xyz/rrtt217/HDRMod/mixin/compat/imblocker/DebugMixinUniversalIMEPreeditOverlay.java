package xyz.rrtt217.HDRMod.mixin.compat.imblocker;

import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FocusableObject;
import io.github.reserveword.imblocker.common.gui.FocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.compat.imblocker.IMManagerLinuxEnhanced;

@Mixin(UniversalIMEPreeditOverlay.class)
public class DebugMixinUniversalIMEPreeditOverlay {
    @Shadow
    private int caretY;
    @Shadow
    private int caretX;
    @Shadow
    private String preEditText;
    @Shadow
    private int preEditTextWidth;

    @Inject(method = "updatePreeditArea", at = @At("RETURN"))
    private void hdr_mod$updatePreeditCursorPos(CallbackInfo ci){
        FocusableObject focusOwner = FocusManager.getFocusOwner();
        if(focusOwner != null && preEditText != null) {
            double widgetGuiScale = focusOwner.getGuiScale();
            int widgetFontSize = focusOwner.getFontHeight();
            int containerFontSize;
            double containerGuiScale;
            Rectangle compositionBorder;
            if (focusOwner instanceof FocusableWidget focusedWidget) {
                containerFontSize = focusedWidget.getFocusContainer().getFontHeight();
                containerGuiScale = focusedWidget.getFocusContainer().getGuiScale();
                compositionBorder = focusedWidget.getFocusContainer().getBoundsAbs();
            } else {
                containerFontSize = widgetFontSize;
                containerGuiScale = widgetGuiScale;
                compositionBorder = focusOwner.getBoundsAbs();
            }

            int inputHeight = (int) (widgetFontSize * widgetGuiScale + 5 * containerGuiScale);
            int compositionX = caretX, compositionY = caretY + inputHeight,
                    compositionWidth = (int) (preEditTextWidth * containerGuiScale),
                    compositionHeight = (int) (containerFontSize * containerGuiScale);
            if (compositionX + compositionWidth > compositionBorder.width()) {
                compositionX = compositionBorder.width() - compositionWidth;
            }
            if (compositionY + compositionHeight > compositionBorder.height()) {
                compositionY = (int) (caretY - (6 + containerFontSize) * containerGuiScale);
            }

            compositionX += compositionBorder.x();
            compositionY += compositionBorder.y();
            int scaledMargin = (int) (2 * containerGuiScale);
            Rectangle preeditCursorRect;
            preeditCursorRect = new Rectangle(compositionX - scaledMargin, Math.min(caretY, compositionY) - scaledMargin,
                    compositionWidth + scaledMargin * 2, compositionHeight + inputHeight + scaledMargin * 2);
            IMManagerLinuxEnhanced.updatePreeditCursorRectanglePosition(preeditCursorRect.x(), preeditCursorRect.y(), preeditCursorRect.width(), preeditCursorRect.height());
        }
    }
}
