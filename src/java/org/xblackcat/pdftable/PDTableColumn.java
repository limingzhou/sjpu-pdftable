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
    private final PDBorderStyle cellBorderStyle;

    public PDTableColumn(int idx, float width, PDInsets padding, PDTableCellRenderer renderer, PDBorderStyle cellBorderStyle) {
        this.idx = idx;
        this.width = width;
        this.padding = padding;
        this.renderer = renderer;
        this.cellBorderStyle = cellBorderStyle;
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

    public PDBorderStyle getCellBorderStyle() {
        return cellBorderStyle;
    }
}
