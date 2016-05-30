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
    public PDTableRowDef getRowDefinition(Object rowObject, int level, int groupRow, int row, int page) {
        final int affectedLevel = level < rowStyles.length ? level : rowStyles.length - 1;
        final IPDRowProvider rowStyle = rowStyles[affectedLevel];

        return rowStyle.getRowDefinition(rowObject, level, groupRow, row, page);
    }
}
