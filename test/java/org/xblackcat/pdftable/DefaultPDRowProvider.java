package org.xblackcat.pdftable;

import java.awt.*;

/**
 * 25.04.2016 15:22
 *
 * @author xBlackCat
 */
public class DefaultPDRowProvider implements IPDRowProvider {
    private final Color background;
    private final PDTableTextCellDef[] columns;
    private final PDBorderStyle rowBorderStyle;

    public DefaultPDRowProvider(
            PDInsets padding,
            PDTableCellTextGetter renderer,
            PDBorderStyle cellBorderStyle,
            Color background,
            float... widths
    ) {
        this(null, padding, renderer, cellBorderStyle, background, widths);
    }

    public DefaultPDRowProvider(
            PDBorderStyle rowBorderStyle,
            PDInsets padding,
            PDTableCellTextGetter renderer,
            PDBorderStyle cellBorderStyle,
            Color background,
            float... widths
    ) {
        this.rowBorderStyle = rowBorderStyle;
        this.background = background;
        columns = new PDTableTextCellDef[widths.length];
        int i = 0;
        while (i < columns.length) {
            columns[i] = new PDTableTextCellDef(
                    i,
                    widths[i],
                    padding,
                    null,
                    0,
                    renderer,
                    cellBorderStyle,
                    HorizontalAlign.values()[i % 4],
                    VerticalAlign.values()[i % 3],
                    VerticalAlign.Bottom
            );
            i++;
        }
    }

    @Override
    public PDTableRowDef getRowDefinition(Object rowObject, int level, int groupRow, int row, int page) {
        return new PDTableRowDef(rowBorderStyle, background, columns);
    }

    public APDTableCellDef[] getColumns() {
        return columns;
    }

    public PDBorderStyle getRowBorderStyle() {
        return rowBorderStyle;
    }
}
