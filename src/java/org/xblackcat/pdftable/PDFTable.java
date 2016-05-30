package org.xblackcat.pdftable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
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
    private final Color background;

    public PDFTable(IPDPageProvider pageProvider, IPDRowProvider rowProvider, PDBorderStyle borderStyle, Color background) {
        this(pageProvider, rowProvider, borderStyle, background, Integer.MAX_VALUE);
    }

    public PDFTable(
            IPDPageProvider pageProvider,
            IPDRowProvider rowProvider,
            PDBorderStyle borderStyle,
            Color background,
            int headersAmount
    ) {
        this.pageProvider = pageProvider;
        this.rowProvider = rowProvider;
        this.borderStyle = borderStyle;
        this.background = background;
        this.headersAmount = headersAmount;
    }

    public Drawer applyToDocument(PDDocument doc) {
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

        private Drawer(PDDocument doc) {
            this.doc = doc;
        }

        public void drawTable(DataGroup[] data) throws IOException {
            startNewPage();

            int rowInGroup = 0;
            for (DataGroup g : data) {
                drawGroup(g, rowInGroup++);
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

        private void drawGroup(DataGroup g, int groupRowIdx) throws IOException {
            Object valueObj = g.getKey();
            PDTableRowDef rowInfo = rowProvider.getRowDefinition(valueObj, headersStack.size(), groupRowIdx, curRow, curPage);
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
                int rowInGroup = 0;
                for (DataGroup sg : g.getValues()) {
                    drawGroup(sg, rowInGroup++);
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
                final IPDTableCell cell = rr.rowCells[i];
                APDTableCellDef rowCelDef = rr.rowInfo.getCellDefs()[i];
                drawCellBackground(x, drawY, rowCelDef.getWidth(), rr.rowHeight, rr.rowInfo.getBackground(), rowCelDef.getBackground());

                cell.drawCell(stream, x, drawY, rr.rowHeight);
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

        private void drawCellBackground(float x, float y, float width, float height, Color rowBG, Color cellBG) throws IOException {
            final Color background;
            if (cellBG != null) {
                background = cellBG;
            } else if (rowBG != null) {
                background = rowBG;
            } else {
                background = PDFTable.this.background;
            }

            if (background == null) {
                return;
            }

            stream.setNonStrokingColor(background);
            stream.addRect(x, y - height, width, height);
            stream.fill();
        }

        private PDRenderedRow preRenderedRow(Object valueObj, PDTableRowDef rowInfo) throws IOException {
            APDTableCellDef[] rowDef = rowInfo.getCellDefs();
            IPDTableCell[] rowCells = new IPDTableCell[rowDef.length];
            float rowHeight = 0;
            float rowWidth = 0;
            {
                int i = 0;
                while (i < rowDef.length) {
                    APDTableCellDef col = rowDef[i];
                    final IPDTableCell cell = col.buildCell(valueObj, i, curRow, curPage);
                    rowCells[i] = cell;
                    i++;

                    float cellHeight = cell.getHeight();
                    rowWidth += Math.max(col.getWidth(), cell.getWidth());
                    if (rowHeight < cellHeight) {
                        rowHeight = cellHeight;
                    }
                }
            }
            return new PDRenderedRow(rowInfo, rowCells, rowHeight, rowWidth);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<APDTableCellDef> columns = new ArrayList<>();

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
        private final IPDTableCell[] rowCells;
        private final float rowHeight;
        private final float rowWidth;

        PDRenderedRow(PDTableRowDef rowInfo, IPDTableCell[] rowCells, float rowHeight, float rowWidth) {
            this.rowInfo = rowInfo;
            this.rowCells = rowCells;
            this.rowHeight = rowHeight;
            this.rowWidth = rowWidth;
        }
    }
}
