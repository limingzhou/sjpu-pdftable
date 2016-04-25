package org.xblackcat.pdftable;

/**
 * 25.04.2016 15:22
 *
 * @author xBlackCat
 */
public class DefaultPDRowProvider implements IPDRowProvider {
    private final PDTableColumn[] columns;

    public DefaultPDRowProvider(PDInsets padding, PDTableCellRenderer renderer, float... widths) {
        columns = new PDTableColumn[widths.length];
        int i = 0;
        while (i < columns.length) {
            columns[i] = new PDTableColumn(i, widths[i], padding, renderer);
            i++;
        }
    }

    @Override
    public PDTableColumn[] getRowCellInfo(Object rowObject, int level, int row, int page) {
        return columns;
    }
}
