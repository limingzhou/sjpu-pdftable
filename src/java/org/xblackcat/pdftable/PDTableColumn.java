package org.xblackcat.pdftable;

/**
 * 25.04.2016 13:02
 *
 * @author xBlackCat
 */
public class PDTableColumn {
    private final int idx;
    private final float width;
    private final PDInsets padding;
    private final PDTableCellRenderer renderer;

    public PDTableColumn(int idx, float width, PDInsets padding, PDTableCellRenderer renderer) {
        this.idx = idx;
        this.width = width;
        this.padding = padding;
        this.renderer = renderer;
    }

    public int getIdx() {
        return idx;
    }

    public float getWidth() {
        return width;
    }

    public PDTableCellRenderer getRenderer() {
        return renderer;
    }

    public PDInsets getPadding() {
        return padding;
    }
}
