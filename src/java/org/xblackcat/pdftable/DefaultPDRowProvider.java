package org.xblackcat.pdftable;

import java.awt.*;

/**
 * 25.04.2016 15:22
 *
 * @author xBlackCat
 */
public class DefaultPDRowProvider implements IPDRowProvider {
    private final Color background;
    private final PDTableColumn[] columns;
    private final PDBorderStyle rowBorderStyle;

    public DefaultPDRowProvider(
            PDInsets padding,
            PDTableCellRenderer renderer,
            PDBorderStyle cellBorderStyle,
            Color background,
            float... widths
    ) {
        this(null, padding, renderer, cellBorderStyle, background, widths);
    }

    public DefaultPDRowProvider(
            PDBorderStyle rowBorderStyle,
            PDInsets padding,
            PDTableCellRenderer renderer,
            PDBorderStyle cellBorderStyle,
            Color background,
            float... widths
    ) {
        this.rowBorderStyle = rowBorderStyle;
        this.background = background;
        columns = new PDTableColumn[widths.length];
        int i = 0;
        while (i < columns.length) {
            columns[i] = new PDTableColumn(i, widths[i], padding, null, renderer, cellBorderStyle);
            i++;
        }
    }

    @Override
    public PDTableRowDef getRowCellInfo(Object rowObject, int level, int groupRow, int row, int page) {
        return new PDTableRowDef(rowBorderStyle, background, columns);
    }

    public PDTableColumn[] getColumns() {
        return columns;
    }

    public PDBorderStyle getRowBorderStyle() {
        return rowBorderStyle;
    }
}
