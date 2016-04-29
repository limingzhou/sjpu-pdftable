package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;

/**
 * 25.04.2016 15:15
 *
 * @author xBlackCat
 */
public class DefaultPDPageProvider implements IPDPageProvider {
    private final PDRectangle pageSize;
    private final PDInsets defaultPadding;

    public DefaultPDPageProvider(PDRectangle pageSize) {
        this(pageSize, PDTableTextCell.DEFAULT_PADDING);
    }

    public DefaultPDPageProvider(PDRectangle pageSize, PDInsets padding) {
        this.pageSize = pageSize;
        defaultPadding = padding;
    }

    @Override
    public PDPageContentStream buildPage(PDDocument doc, int pageNum) throws IOException {
        final PDPage page = new PDPage(pageSize);
        doc.addPage(page);

        return new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, false);
    }

    @Override
    public PDInsets getDrawMargins(int pageNum) {
        return defaultPadding;
    }

    @Override
    public PDRectangle getPageSize(int pageNum) {
        return pageSize;
    }
}
