package xyz.rrtt217.HDRMod.compat.ixeris;

import xyz.rrtt217.HDRMod.util.ime.*;

public class CommonCallbacks_334 {
    public static GLFWIMEStatusCallback iMEStatusCallback;
    public static GLFWPreeditCallback preeditCallback;
    public static GLFWPreeditCandidateCallback preeditCandidateCallback;

    static {
        initCallbacks();
    }

    public static void initCallbacks() {
        iMEStatusCallback = GLFWIMEStatusCallback.create(CommonCallbacks_334::onImeStatusCallback);
        preeditCallback = GLFWPreeditCallback.create(CommonCallbacks_334::onPreeditCallback);
        preeditCandidateCallback = GLFWPreeditCandidateCallback.create(CommonCallbacks_334::onPreeditCandidateCallback);
    }

    private static void onPreeditCallback(long window, int preedit_count, long preedit_string, int block_count, long block_sizes, int focused_block, int caret) {
        PreeditCallbackDispatcher.get(window).onCallback(window, preedit_count, preedit_string, block_count, block_sizes, focused_block, caret);
    }

    private static void onPreeditCandidateCallback(long window, int candidates_count, int selected_index, int page_start, int page_size) {
        PreeditCandidateCallbackDispatcher.get(window).onCallback(window, candidates_count, selected_index, page_start, page_size);
    }

    private static void onImeStatusCallback(long window) {
        IMEStatusCallbackDispatcher.get(window).onCallback(window);
    }
}
