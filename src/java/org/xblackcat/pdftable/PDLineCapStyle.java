package org.xblackcat.pdftable;

/**
 * 26.04.2016 10:47
 *
 * @author xBlackCat
 */
public enum PDLineCapStyle {
    ButtCap(0),
    RoundCap(1),
    SquareCap(2),
//    ---
    ;
    private final int value;

    PDLineCapStyle(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
