package org.xblackcat.pdftable;

/**
 * 25.04.2016 13:05
 *
 * @author xBlackCat
 */
public interface IPDRowProvider {
    PDTableRowDef getRowDefinition(Object rowObject, int level, int groupRow, int row, int page);
}
