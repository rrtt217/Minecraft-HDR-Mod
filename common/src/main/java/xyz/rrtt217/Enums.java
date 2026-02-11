package xyz.rrtt217;

public class Enums {
    // These enums are the same as Wayland color-management-v1 protocol.
    public enum Primaries{
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
    }


    public enum TransferFunction {
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
    }
}
