package org.xblackcat.pdftable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
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
    private final int headersAmount;

    public PDFTable(IPDPageProvider pageProvider, IPDRowProvider rowProvider, PDBorderStyle borderStyle) {
        this(pageProvider, rowProvider, borderStyle, Integer.MAX_VALUE);
    }

    public PDFTable(
            IPDPageProvider pageProvider,
            IPDRowProvider rowProvider,
            PDBorderStyle borderStyle,
            int headersAmount
    ) {
        this.pageProvider = pageProvider;
        this.rowProvider = rowProvider;
        this.borderStyle = borderStyle;
        this.headersAmount = headersAmount;
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
        private boolean firstRowOnPage = true;

        private final Deque<PDRenderedRow> headersStack = new LinkedList<>();

        public Drawer(PDDocument doc) {
            this.doc = doc;
        }

        public void drawTable(DataGroup[] data) throws IOException {
            startNewPage();

            for (DataGroup g : data) {
                drawGroup(g);
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
            firstRowOnPage = true;
        }

        private void drawGroup(DataGroup g) throws IOException {
            Object valueObj = g.getKey();
            PDTableRowDef rowInfo = rowProvider.getRowCellInfo(valueObj, headersStack.size(), curRow, curPage);
            PDRenderedRow rr = preRenderedRow(valueObj, rowInfo);
            if (firstRowOnPage) {
                firstRowOnPage = false;
            } else if (rr.rowHeight > drawY - curPageDrawMargins.bottom) {
                drawTableBorder();
                startNewPage();

                int i = 0;
                for (PDRenderedRow header : headersStack) {
                    if (i++ > headersAmount) {
                        break;
                    }

                    drawRow(header);
                }
            }

            drawRow(rr);

            curRow++;

            if (tableWidth < rr.rowWidth) {
                tableWidth = rr.rowWidth;
            }

            if (ArrayUtils.isNotEmpty(g.getValues())) {
                headersStack.addLast(rr);
                for (DataGroup sg : g.getValues()) {
                    drawGroup(sg);
                }
                if (headersStack.removeLast() != rr) {
                    throw new IllegalStateException("Headers stack is broken!");
                }
            }
        }

        private void drawTableBorder() throws IOException {
            if (borderStyle != null) {
                borderStyle.applyToStream(
                        stream,
                        curPageDrawMargins.left,
                        curPageHeight - curPageDrawMargins.top,
                        tableWidth,
                        curPageHeight - drawY - curPageDrawMargins.top
                );
            }
        }

        private void drawRow(PDRenderedRow rr) throws IOException {
            float x = curPageDrawMargins.left;
            for (int i = 0; i < rr.rowCells.length; i++) {
                final PDTableCell cell = rr.rowCells[i];
                PDTableColumn rowCelDef = rr.rowInfo.getCellDefs()[i];

                drawCellText(cell, x);
                PDBorderStyle borderStyle = rowCelDef.getCellBorderStyle();
                if (borderStyle != null) {
                    borderStyle.applyToStream(stream, x, drawY, rowCelDef.getWidth(), rr.rowHeight);
                }
                x += rowCelDef.getWidth();
            }

            if (rr.rowInfo.getBorderStyle() != null) {
                rr.rowInfo.getBorderStyle().applyToStream(stream, curPageDrawMargins.left, drawY, rr.rowWidth, rr.rowHeight);
            }

            drawY -= rr.rowHeight;
        }

        private PDRenderedRow preRenderedRow(Object valueObj, PDTableRowDef rowInfo) throws IOException {
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
                    PDTextLine value = col.getRenderer().getValue(valueObj, i, curRow, curPage);
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
            return new PDRenderedRow(rowInfo, rowCells, rowHeight, rowWidth);
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

    /**
     * 26.04.2016 12:39
     *
     * @author xBlackCat
     */
    private static class PDRenderedRow {
        private final PDTableRowDef rowInfo;
        private final PDTableCell[] rowCells;
        private final float rowHeight;
        private final float rowWidth;

        PDRenderedRow(PDTableRowDef rowInfo, PDTableCell[] rowCells, float rowHeight, float rowWidth) {
            this.rowInfo = rowInfo;
            this.rowCells = rowCells;
            this.rowHeight = rowHeight;
            this.rowWidth = rowWidth;
        }
    }
}
