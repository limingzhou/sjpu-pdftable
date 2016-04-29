package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

/**
 * 29.04.2016 12:19
 *
 * @author xBlackCat
 */
public interface IPDTableCell extends IPDMeasurable {
    void drawCell(PDPageContentStream stream, float x, float y) throws IOException;

    PDInsets getPadding();
}
