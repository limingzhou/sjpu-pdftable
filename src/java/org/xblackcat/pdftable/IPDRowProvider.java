package org.xblackcat.pdftable;

/**
 * 25.04.2016 13:05
 *
 * @author xBlackCat
 */
public interface IPDRowProvider {
    PDTableRowDef getRowCellInfo(Object rowObject, int level, int row, int page);
}
