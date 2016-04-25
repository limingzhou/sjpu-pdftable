package org.xblackcat.pdftable;

/**
 * 25.04.2016 13:02
 *
 * @author xBlackCat
 */
public interface PDTableCellRenderer {
    PDTextLine getValue(Object cellObj, int col, int row, int page);
}
