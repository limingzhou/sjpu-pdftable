package org.xblackcat.pdftable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

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
    private final PDBorderStyle borderStyle;

    public PDFTable(IPDPageProvider pageProvider, IPDRowProvider rowProvider, PDBorderStyle borderStyle) {
        this.pageProvider = pageProvider;
        this.rowProvider = rowProvider;
        this.borderStyle = borderStyle;
    }

    public Drawer drawTable(PDDocument doc) {
        return new Drawer(doc);
    }

    public class Drawer {
        private final PDDocument doc;

        private PDInsets curPageDrawMargins;
        private float curPageHeight;
        private float curPageWidth;

        private int curPage = 0;
        private int curRow = 0;
        private float tableWidth = 0;
        private PDPageContentStream stream = null;
        private float drawY;

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
            tableWidth = 0;
            stream = pageProvider.buildPage(doc, curPage);
            curPageDrawMargins = pageProvider.getDrawMargins(curPage);
            PDRectangle pageSize = pageProvider.getPageSize(curPage);
            curPageHeight = pageSize.getHeight();
            curPageWidth = pageSize.getWidth();
            drawY = curPageHeight - curPageDrawMargins.top;
        }

        private void drawRow(int level, DataGroup g) throws IOException {
            PDTableRowDef rowInfo = rowProvider.getRowCellInfo(g.getKey(), level, curRow, curPage);
            PDTableColumn[] rowDef = rowInfo.getCellDefs();
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
                    PDTableCell cell = new PDTableCell(lines);
                    rowCells[i] = cell;
                    float cellHeight = cell.getHeight();
                    rowWidth += col.getWidth();
                    i++;
                    if (rowHeight < cellHeight) {
                        rowHeight = cellHeight;
                    }
                }
            }
            if (rowHeight > drawY - curPageDrawMargins.bottom) {
                if (borderStyle != null) {
                    borderStyle.applyToStream(
                            stream,
                            curPageDrawMargins.left,
                            curPageHeight - curPageDrawMargins.top,
                            tableWidth,
                            curPageHeight - drawY - curPageDrawMargins.top
                    );
                }
                startNewPage();
            }

            float x = curPageDrawMargins.left;
            for (int i = 0; i < rowCells.length; i++) {
                final PDTableCell cell = rowCells[i];
                PDTableColumn rowCelDef = rowDef[i];

                drawCellText(cell, x);
                PDBorderStyle borderStyle = rowCelDef.getCellBorderStyle();
                if (borderStyle != null) {
                    borderStyle.applyToStream(stream, x, drawY, rowCelDef.getWidth(), rowHeight);
                }
                x += rowCelDef.getWidth();
            }

            if (rowInfo.getBorderStyle() != null) {
                rowInfo.getBorderStyle().applyToStream(stream, curPageDrawMargins.left, drawY, rowWidth, rowHeight);
            }

            drawY -= rowHeight;

            curRow++;

            if (tableWidth < rowWidth) {
                tableWidth = rowWidth;
            }

            if (ArrayUtils.isNotEmpty(g.getValues())) {
                for (DataGroup sg : g.getValues()) {
                    drawRow(level + 1, sg);
                }
            }
        }

        private void drawCellText(PDTableCell cell, float x) throws IOException {
            float textSpacing = cell.getTextSpacing();

            float heightOffset = drawY - cell.getPadding().top + textSpacing;

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
