package xyz.rrtt217.HDRMod.compat.ixeris;

public class CallbackDispatchers_334 {
    public static void validateAll(long window) {
        IMEStatusCallbackDispatcher.get(window).validate();
        PreeditCallbackDispatcher.get(window).validate();
        PreeditCandidateCallbackDispatcher.get(window).validate();
    }
}
