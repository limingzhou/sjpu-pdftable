package org.xblackcat.pdftable;

/**
 * 25.04.2016 15:22
 *
 * @author xBlackCat
 */
public class DefaultPDRowProvider implements IPDRowProvider {
    private final PDTableColumn[] columns;
    private final PDBorderStyle rowBorderStyle;

    public DefaultPDRowProvider(PDInsets padding, PDTableCellRenderer renderer, PDBorderStyle cellBorderStyle, float... widths) {
        this(null, padding, renderer, cellBorderStyle, widths);
    }

    public DefaultPDRowProvider(
            PDBorderStyle rowBorderStyle,
            PDInsets padding,
            PDTableCellRenderer renderer,
            PDBorderStyle cellBorderStyle,
            float... widths
    ) {
        this.rowBorderStyle = rowBorderStyle;
        columns = new PDTableColumn[widths.length];
        int i = 0;
        while (i < columns.length) {
            columns[i] = new PDTableColumn(i, widths[i], padding, renderer, cellBorderStyle);
            i++;
        }
    }

    @Override
    public PDTableRowDef getRowCellInfo(Object rowObject, int level, int row, int page) {
        return new PDTableRowDef(rowBorderStyle, columns);
    }

    public PDTableColumn[] getColumns() {
        return columns;
    }

    public PDBorderStyle getRowBorderStyle() {
        return rowBorderStyle;
    }
}
