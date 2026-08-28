package xyz.rrtt217.HDRMod.api.color;

/**
 * Collection of enums used by the HDR color management API.
 *
 * <p>The {@link Primaries} and {@link TransferFunction} enums mirror the values
 * defined by the Wayland color-management-v1 protocol, and provide conversion
 * helpers to/from the SMPTE ST 273 colorimetry IDs used by the display pipeline.</p>
 */
public class Enums {
    /**
     * Color primaries (chromaticity of the color space).
     *
     * <p>The IDs correspond to the {@code wp_color_management_v1.primaries}
     * values of the Wayland color-management-v1 protocol.</p>
     */
    // These enums are the same as Wayland color-management-v1 protocol.
    public enum Primaries{
        /** Unspecified / unknown primaries (id 0). */
        UNSPECIFIED(0),
        /** sRGB (Rec. 709 ITU-R BT.709) primaries (id 1). */
        SRGB(1),
        /** PAL-M primaries (id 2). */
        PAL_M(2),
        /** PAL (EBU Tech 3213, BT.470 System B/G) primaries (id 3). */
        PAL(3),
        /** NTSC (BT.601) primaries (id 4). */
        NTSC(4),
        /** Generic film primaries (id 5). */
        GENERIC_FILM(5),
        /** BT.2020 wide-gamut primaries (id 6). */
        BT2020(6),
        /** CIE 1931 XYZ primaries (id 7). */
        CIE1931_XYZ(7),
        /** DCI-P3 primaries (id 8). */
        DCI_P3(8),
        /** Display-P3 primaries (id 9). */
        DISPLAY_P3(9),
        /** Adobe RGB primaries (id 10). */
        ADOBE_RGB(10);
        private final int id;

        Primaries(int id) {
            this.id = id;
        }

        /**
         * Returns the protocol ID of this primaries value.
         *
         * @return the numeric ID
         */
        public int getId() {
            return id;
        }

        /**
         * Returns the {@link Primaries} matching the given protocol ID.
         *
         * @param id the protocol ID
         * @return the matching {@link Primaries}
         * @throws IllegalArgumentException if the ID is not a valid primaries value
         */
        public static Primaries fromId(int id) {
            for (Primaries p : Primaries.values()) {
                if (p.getId() == id) {
                    return p;
                }
            }
            throw new IllegalArgumentException(id + " is not a valid Primaries");
        }

        /**
         * Returns the {@link Primaries} matching the given CICP/SMPTE ST 273 ID.
         *
         * @param H273Id the SMPTE ST 273 color primaries ID
         * @return the matching {@link Primaries}, or {@link #UNSPECIFIED} if unknown
         */
        public static Primaries fromH273Id(int H273Id) {
            return switch (H273Id) {
                case 1 -> Primaries.SRGB;
                case 4 -> Primaries.PAL_M;
                case 5 -> Primaries.PAL;
                case 6, 7 -> Primaries.NTSC;
                case 8 -> Primaries.GENERIC_FILM;
                case 9 -> Primaries.BT2020;
                case 10 -> Primaries.CIE1931_XYZ;
                case 11 -> Primaries.DCI_P3;
                case 12 -> Primaries.DISPLAY_P3;
                default -> Primaries.UNSPECIFIED;
            };
        }

        /**
         * Converts this {@link Primaries} to its SMPTE ST 273 ID.
         *
         * @param p the primaries to convert
         * @return the SMPTE ST 273 ID (defaults to 1 / sRGB for {@link #UNSPECIFIED})
         */
        public static int toH273Id(Primaries p) {
            return switch (p) {
                case SRGB -> 1;
                case PAL_M -> 4;
                case PAL -> 5;
                case NTSC -> 6;
                case GENERIC_FILM -> 8;
                case BT2020 -> 9;
                case CIE1931_XYZ -> 10;
                case DCI_P3 -> 11;
                case DISPLAY_P3 -> 12;
                default -> 1;
            };
        }
    }


    /**
     * Transfer functions (opto-electronic / electro-optical transfer curves).
     *
     * <p>The IDs correspond to the {@code wp_color_management_v1.transfer_function}
     * values of the Wayland color-management-v1 protocol.</p>
     */
    public enum TransferFunction {
        /** Unspecified / unknown transfer function (id 0). */
        UNSPECIFIED(0),
        /** BT.1886 (sRGB display curve) transfer function (id 1). */
        BT1886(1),
        /** Gamma 2.2 transfer function (id 2). */
        GAMMA22(2),
        /** Gamma 2.8 transfer function (id 3). */
        GAMMA28(3),
        /** SMPTE 240M transfer function (id 4). */
        ST240(4),
        /** Extended linear (scRGB-nl linear segment) transfer function (id 5). */
        EXT_LINEAR(5),
        /** Cineon Log 100 transfer function (id 6). */
        LOG_100(6),
        /** Cineon Log 316 transfer function (id 7). */
        LOG_316(7),
        /** xvYCC (BT.709 extended gamut) transfer function (id 8). */
        XVYCC(8),
        /** sRGB transfer function (id 9). */
        SRGB(9),
        /** Extended sRGB (negative values) transfer function (id 10). */
        EXT_SRGB(10),
        /** ST 2084 / PQ (Perceptual Quantizer) HDR transfer function (id 11). */
        ST2084_PQ(11),
        /** SMPTE 428 (absolute luminance transform) transfer function (id 12). */
        ST428(12),
        /** HLG (Hybrid Log-Gamma) HDR transfer function (id 13). */
        HLG(13);

        private final int id;

        TransferFunction(int id) {
            this.id = id;
        }

        /**
         * Returns the protocol ID of this transfer function.
         *
         * @return the numeric ID
         */
        public int getId() {
            return id;
        }

        /**
         * Returns the {@link TransferFunction} matching the given protocol ID.
         *
         * @param id the protocol ID
         * @return the matching {@link TransferFunction}
         * @throws IllegalArgumentException if the ID is not a valid transfer function
         */
        public static TransferFunction fromId(int id) {
            for (TransferFunction transferFunction : TransferFunction.values()) {
                if (transferFunction.getId() == id) {
                    return transferFunction;
                }
            }
            throw new IllegalArgumentException("No enum constant " + TransferFunction.class.getName() + "#" + id);
        }

        /**
         * Returns the {@link TransferFunction} matching the given CICP/SMPTE ST 273 ID.
         *
         * @param H273Id the SMPTE ST 273 transfer function ID
         * @return the matching {@link TransferFunction}, or {@link #UNSPECIFIED} if unknown
         */
        public static TransferFunction fromH273Id(int H273Id) {
            return switch (H273Id){
                case 1, 6, 14, 15 -> TransferFunction.BT1886;
                case 4 -> TransferFunction.GAMMA22;
                case 5 -> TransferFunction.GAMMA28;
                case 7 -> TransferFunction.ST240;
                case 8 -> TransferFunction.EXT_LINEAR;
                case 9 -> TransferFunction.LOG_100;
                case 10 -> TransferFunction.LOG_316;
                case 11 -> TransferFunction.XVYCC;
                case 13 -> TransferFunction.SRGB;
                case 16 -> TransferFunction.ST2084_PQ;
                case 17 -> TransferFunction.ST428;
                case 18 -> TransferFunction.HLG;
                default -> TransferFunction.UNSPECIFIED;
            };
        }

        /**
         * Converts this {@link TransferFunction} to its SMPTE ST 273 ID.
         *
         * @param transferFunction the transfer function to convert
         * @return the SMPTE ST 273 ID (defaults to 13 / sRGB for compatible values)
         */
        public static int toH273Id(TransferFunction transferFunction) {
            return switch (transferFunction){
                case BT1886 -> 1;
                case GAMMA22 -> 4;
                case GAMMA28 -> 5;
                case ST240 -> 6;
                case EXT_LINEAR -> 8;
                case LOG_100 -> 9;
                case LOG_316 -> 10;
                case XVYCC -> 11;
                case SRGB -> 13;
                case EXT_SRGB -> 13;
                case ST2084_PQ -> 16;
                case ST428 -> 17;
                case HLG -> 18;
                default -> 13;
            };
        }
    }
}
