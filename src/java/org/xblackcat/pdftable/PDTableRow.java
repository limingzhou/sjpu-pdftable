package org.xblackcat.pdftable;

import java.io.IOException;

/**
 * 25.04.2016 12:10
 *
 * @author xBlackCat
 */
public class PDTableRow implements IPDMeasurable {
    private final PDTableCell cells[];

    public PDTableRow(PDTableCell... cells) {
        this.cells = cells;
    }

    @Override
    public float getWidth() throws IOException {
        return 0;
    }

    @Override
    public float getHeight() throws IOException {
        float height = 0;
        for (PDTableCell l : cells) {
            height += l.getHeight();
        }
        return height + (cells.length - 1);
    }
}
