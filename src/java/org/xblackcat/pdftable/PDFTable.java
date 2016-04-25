package org.xblackcat.pdftable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 22.04.2016 16:52
 *
 * @author xBlackCat
 */
public class PDFTable {
    private final IPDPageProvider pageProvider;
    private final IPDRowProvider rowProvider;

    public PDFTable(IPDPageProvider pageProvider, IPDRowProvider rowProvider) {
        this.pageProvider = pageProvider;
        this.rowProvider = rowProvider;
    }

    public Drawer drawTable(PDDocument doc) {
        return new Drawer(doc);
    }

    public class Drawer {
        private final PDDocument doc;

        private final PDInsets drawMargins = pageProvider.getDrawMargins();
        private final float curPageHeight = pageProvider.getPageHeight();
        private final float curPageWidth = pageProvider.getPageWidth();

        private int curPage = 0;
        private int curRow = 0;
        private PDPageContentStream stream = null;
        private float remainHeight;

        public Drawer(PDDocument doc) {
            this.doc = doc;
        }

        public void drawTable(DataGroup[] data) throws IOException {
            startNewPage();

            for (DataGroup g : data) {
                drawRow(0, g);
            }

            if (stream != null) {
                stream.close();
            }
        }

        private void startNewPage() throws IOException {
            if (stream != null) {
                stream.close();
            }

            curPage++;
            stream = pageProvider.buildPage(doc, curPage);
            remainHeight = curPageHeight - drawMargins.bottom - drawMargins.top;
        }

        private void drawRow(int level, DataGroup g) throws IOException {
            PDTableColumn[] rowDef = rowProvider.getRowCellInfo(g.getKey(), level, curRow, curPage);
            PDTableCell[] rowCells = new PDTableCell[rowDef.length];
            float rowHeight = 0;
            float rowWidth = 0;
            {
                int i = 0;
                while (i < rowDef.length) {
                    PDTableColumn col = rowDef[i];
                    PDInsets padding = col.getPadding();
                    final PDTextLine[] lines;
                    PDTextLine value = col.getRenderer().getValue(g.getKey(), i, curRow, curPage);
                    if (col.getWidth() >= 0) {
                        lines = PDFUtils.toFixedWidthCell(col.getWidth() - padding.left - padding.right, value);
                    } else {
                        lines = PDFUtils.toCell(value);
                    }
                    PDTableCell cell = new PDTableCell(col.getWidth(), lines);
                    rowCells[i] = cell;
                    float cellHeight = cell.getHeight();
                    rowWidth += col.getWidth();
                    i++;
                    if (rowHeight < cellHeight) {
                        rowHeight = cellHeight;
                    }
                }
            }
            if (rowHeight > remainHeight) {
                startNewPage();
            }

            stream.moveTo(drawMargins.left, remainHeight);
            stream.lineTo(drawMargins.left + rowWidth, remainHeight);
            stream.stroke();

            float x = drawMargins.left;
            for (int i = 0; i < rowCells.length; i++) {
                final PDTableCell cell = rowCells[i];
                PDTableColumn rowCelDef = rowDef[i];

                stream.moveTo(x, remainHeight);
                stream.lineTo(x, remainHeight - rowHeight);
                stream.stroke();

                drawCell(cell, x);
                x += rowCelDef.getWidth();
            }
            stream.moveTo(x, remainHeight);
            stream.lineTo(x, remainHeight - rowHeight);
            stream.stroke();

            remainHeight -= rowHeight;
            stream.moveTo(drawMargins.left, remainHeight);
            stream.lineTo(drawMargins.left + rowWidth, remainHeight);
            stream.stroke();

            curRow++;

            if (ArrayUtils.isNotEmpty(g.getValues())) {
                for (DataGroup sg : g.getValues()) {
                    drawRow(level + 1, sg);
                }
            }
        }

        private void drawCell(PDTableCell cell, float x) throws IOException {
            float textSpacing = cell.getTextSpacing();

            float heightOffset = remainHeight - cell.getPadding().top + textSpacing;

            for (PDTextLine l : cell.getLines()) {
                float lineHeight = l.getHeight();
                heightOffset -= lineHeight - textSpacing;
                float xx = x + cell.getPadding().left;
                for (PDTextPart p : l.getParts()) {
                    stream.setFont(p.getFont(), p.getFontSize());
                    stream.beginText();
                    stream.newLineAtOffset(xx, heightOffset);
                    stream.showText(p.getText());
                    stream.endText();
                    xx += p.getWidth();
                }
            }
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<PDTableColumn> columns = new ArrayList<>();

        private Builder() {
        }


    }

}
