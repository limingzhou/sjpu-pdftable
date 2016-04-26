package org.xblackcat.pdftable;

/**
 * 25.04.2016 15:22
 *
 * @author xBlackCat
 */
public class DefaultLevelPDRowProvider implements IPDRowProvider {
    private final IPDRowProvider[] rowStyles;

    public DefaultLevelPDRowProvider(IPDRowProvider... rowStyles) {
        if (rowStyles == null || rowStyles.length == 0) {
            throw new IllegalArgumentException("At least one style should be set");
        }
        this.rowStyles = rowStyles;
    }

    @Override
    public PDTableRowDef getRowCellInfo(Object rowObject, int level, int row, int page) {
        if (level > rowStyles.length) {
            return rowStyles[rowStyles.length - 1].getRowCellInfo(rowObject, level, row, page);
        }
        return rowStyles[level].getRowCellInfo(rowObject, level, row, page);
    }
}
