package xyz.rrtt217.HDRMod.util.color;

public class Enums {
    // These enums are the same as Wayland color-management-v1 protocol.
    public enum Primaries{
        UNSPECIFIED(0),
        SRGB(1),
        PAL_M(2),
        PAL(3),
        NTSC(4),
        GENERIC_FILM(5),
        BT2020(6),
        CIE1931_XYZ(7),
        DCI_P3(8),
        DISPLAY_P3(9),
        ADOBE_RGB(10);
        private final int id;

        Primaries(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Primaries fromId(int id) {
            for (Primaries p : Primaries.values()) {
                if (p.getId() == id) {
                    return p;
                }
            }
            throw new IllegalArgumentException(id + " is not a valid Primaries");
        }
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


    public enum TransferFunction {
        UNSPECIFIED(0),
        BT1886(1),
        GAMMA22(2),
        GAMMA28(3),
        ST240(4),
        EXT_LINEAR(5),
        LOG_100(6),
        LOG_316(7),
        XVYCC(8),
        SRGB(9),
        EXT_SRGB(10),
        ST2084_PQ(11),
        ST428(12),
        HLG(13);

        private final int id;

        TransferFunction(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
        public static TransferFunction fromId(int id) {
            for (TransferFunction transferFunction : TransferFunction.values()) {
                if (transferFunction.getId() == id) {
                    return transferFunction;
                }
            }
            throw new IllegalArgumentException("No enum constant " + TransferFunction.class.getName() + "#" + id);
        }
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

    public enum BehaviorOnVanillaScreenshotCalled{
        ONLY_VANILLA,
        BOTH,
        ONLY_HDR
    }
}
