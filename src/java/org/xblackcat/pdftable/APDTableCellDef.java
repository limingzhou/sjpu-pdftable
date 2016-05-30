package org.xblackcat.pdftable;

import java.awt.*;
import java.io.IOException;

/**
 * 30.05.2016 13:04
 *
 * @author xBlackCat
 */
public abstract class APDTableCellDef {
    protected final int idx;
    protected final float width;
    protected final PDInsets padding;
    protected final Color background;
    protected final PDBorderStyle cellBorderStyle;

    public APDTableCellDef(
            PDInsets padding,
            Color background,
            float width,
            PDBorderStyle cellBorderStyle,
            int idx
    ) {
        this.padding = padding;
        this.background = background;
        this.width = width;
        this.cellBorderStyle = cellBorderStyle;
        this.idx = idx;
    }

    public int getIdx() {
        return idx;
    }

    public float getWidth() {
        return width;
    }

    public Color getBackground() {
        return background;
    }

    public PDInsets getPadding() {
        return padding;
    }

    public PDBorderStyle getCellBorderStyle() {
        return cellBorderStyle;
    }

    public abstract APDTableCell buildCell(Object valueObj, int i, int curRow, int curPage) throws IOException;
}
