package org.xblackcat.pdftable;

/**
 * 29.04.2016 12:20
 *
 * @author xBlackCat
 */
public abstract class APDTableCell extends APDMeasurable implements IPDTableCell {
    public static final PDInsets DEFAULT_PADDING = new PDInsets(5, 5, 5, 5);
    protected final PDInsets padding;

    public APDTableCell(PDInsets padding) {
        if (padding != null) {
            this.padding = padding;
        } else {
            this.padding = DEFAULT_PADDING;
        }
    }

    @Override
    public PDInsets getPadding() {
        return padding;
    }
}
