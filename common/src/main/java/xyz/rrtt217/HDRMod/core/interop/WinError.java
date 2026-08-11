package xyz.rrtt217.HDRMod.core.interop;

// Copied from https://github.com/sidit77/perfect_presentation.
/*
    The MIT License (MIT)

    Copyright (c) 2025 sidit77

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
            in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

            THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/

import windows.win32.foundation.WIN32_ERROR;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemorySegment.NULL;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static windows.win32.foundation.Apis.LocalFree;
import static windows.win32.system.diagnostics.debug.Apis.FormatMessageW;
import static windows.win32.system.diagnostics.debug.FORMAT_MESSAGE_OPTIONS.*;
import static windows.win32.system.libraryloader.Apis.GetModuleHandleW;

public class WinError {

    // address layout pointing to an unbounded memory segment
    private static final AddressLayout ADDRESS_UNBOUNDED = ADDRESS.withTargetLayout(
            MemoryLayout.sequenceLayout(Long.MAX_VALUE, JAVA_BYTE));

    private static final MemoryLayout errorStateLayout = Linker.Option.captureStateLayout();
    private static final VarHandle callStateGetLastErrorVarHandle =
            errorStateLayout.varHandle(MemoryLayout.PathElement.groupElement("GetLastError"));

    private static final MemorySegment ntModuleHandle;

    static {
        var arena = Arena.ofAuto();
        var ntModuleName = arena.allocateFrom("NTDLL.DLL", UTF_16LE);
        var errorState = arena.allocate(errorStateLayout);
        ntModuleHandle = GetModuleHandleW(errorState, ntModuleName);
    }

    /**
     * Returns the error code captured using the call state.
     *
     * @param callState the call state
     * @return the error code
     */
    public static int getLastError(MemorySegment callState) {
        return (int) callStateGetLastErrorVarHandle.get(callState, 0);
    }

    /**
     * Checks that the previous Windows API call was successful.
     * <p>
     * Throws an exception with the error message otherwise.
     * </p>
     */
    public static void checkSuccessful(MemorySegment errorState) {
        var lastError = getLastError(errorState);
        if (lastError != WIN32_ERROR.ERROR_SUCCESS)
            throw new IllegalStateException(getErrorMessage(lastError));
    }

    /**
     * Checks that {@code errorCode} is a successful code.
     * <p>
     * Throws an exception with the error message otherwise.
     * </p>
     */
    public static void checkSuccessful(int errorCode) {
        if (errorCode < 0)
            throw new IllegalStateException("HRESULT 0x%x: %s".formatted(errorCode, getErrorMessage(errorCode)));
    }

    /**
     * Gets the error message for the specified Windows error code.
     *
     * @param errorCode error code
     */
    public static String getErrorMessage(int errorCode) {
        try (var arena = Arena.ofConfined()) {
            var errorState = arena.allocate(errorStateLayout);
            var messagePointerHolder = arena.allocate(ADDRESS);

            // First try: Win32 error code
            var res = FormatMessageW(
                    errorState,
                    FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
                    NULL,
                    errorCode,
                    0,
                    messagePointerHolder,
                    0,
                    NULL
            );

            // Second try: NTSTATUS error code
            if (res == 0) {
                res = FormatMessageW(
                        errorState,
                        FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_HMODULE | FORMAT_MESSAGE_IGNORE_INSERTS,
                        ntModuleHandle,
                        errorCode,
                        0,
                        messagePointerHolder,
                        0,
                        NULL
                );
            }

            // Fallback
            if (res == 0)
                return "unspecified error";

            var messagePointer = messagePointerHolder.get(ADDRESS_UNBOUNDED, 0);
            var message = messagePointer.getString(0, UTF_16LE);
            LocalFree(errorState, messagePointer);
            return message.trim();
        }
    }

    private WinError() { }

}
