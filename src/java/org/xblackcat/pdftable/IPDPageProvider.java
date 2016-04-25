package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

/**
 * 25.04.2016 13:12
 *
 * @author xBlackCat
 */
public interface IPDPageProvider {
    PDPageContentStream buildPage(PDDocument doc, int pageNum) throws IOException;

    PDInsets getDrawMargins();

    float getPageWidth();

    float getPageHeight();
}
