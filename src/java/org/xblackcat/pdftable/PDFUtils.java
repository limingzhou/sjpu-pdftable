package org.xblackcat.pdftable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * 11.04.2016 11:28
 *
 * @author xBlackCat
 */
public class PDFUtils {
    protected static PDTableTextCell.CellLine[] wrapLines(float desiredWidth, PDStyledString... textLines) throws IOException {
        if (ArrayUtils.isEmpty(textLines)) {
            return new PDTableTextCell.CellLine[0];
        }

        Collection<PDTableTextCell.CellLine> lines = new ArrayList<>();

        for (PDStyledString l : textLines) {
            for (PDStyledString ll : l.split('\n')) {
                lines.addAll(wrapLine(desiredWidth, ll));
            }
        }

        return lines.stream().toArray(PDTableTextCell.CellLine[]::new);
    }

    protected static PDTableTextCell.CellLine[] toCell(PDStyledString... textLines) throws IOException {
        if (ArrayUtils.isEmpty(textLines)) {
            return new PDTableTextCell.CellLine[0];
        }

        Collection<PDStyledString> lines = new ArrayList<>();

        for (PDStyledString l : textLines) {
            lines.addAll(Arrays.asList(l.split('\n')));
        }

        return lines.stream().map(PDStyledString::getParts).map(PDTableTextCell.CellLine::new).toArray(PDTableTextCell.CellLine[]::new);
    }

    protected static Collection<PDTableTextCell.CellLine> wrapLine(float desiredWidth, PDStyledString textLine) throws IOException {
        Collection<PDTableTextCell.CellLine> lines = new ArrayList<>();
        Collection<PDTextPart> currentLine = new ArrayList<>();

        float offset = 0;
        for (PDTextPart p : textLine.getParts()) {
            String text = p.getText();
            float stringWidth = p.getWidth();

            if (offset + stringWidth < desiredWidth) {
                currentLine.add(p);
                offset += stringWidth;
                continue;
            }

            PDTextPart part = p;
            do {
                int expectedIndex = Math.min((int) (text.length() * (desiredWidth - offset) / stringWidth), text.length());
                int spaceIdx = text.lastIndexOf(' ', expectedIndex);
                final int cutIdx;
                final int partIdx;
                if (spaceIdx < 0) {
                    cutIdx = expectedIndex;
                    partIdx = expectedIndex;
                } else {
                    cutIdx = spaceIdx;
                    partIdx = spaceIdx + 1;
                }

                currentLine.add(part.withText(text.substring(0, cutIdx)));
                lines.add(new PDTableTextCell.CellLine(currentLine.stream().toArray(PDTextPart[]::new)));
                currentLine = new ArrayList<>();

                text = text.substring(partIdx);
                part = part.withText(text);
                stringWidth = part.getWidth();
                offset = 0;
            } while (stringWidth > desiredWidth);
            offset = stringWidth;
            currentLine.add(part);
        }

        if (!currentLine.isEmpty()) {
            lines.add(new PDTableTextCell.CellLine(currentLine.stream().toArray(PDTextPart[]::new)));
        }
        return lines;
    }

    public static float maxHeightOf(IPDMeasurable... objects) throws IOException {
        float height = 0;

        for (IPDMeasurable m : objects) {
            if (height < m.getHeight()) {
                height = m.getHeight();
            }
        }

        return height;
    }

    public static float maxWidthOf(IPDMeasurable... objects) throws IOException {
        float width = 0;

        for (IPDMeasurable m : objects) {
            if (width < m.getWidth()) {
                width = m.getWidth();
            }
        }

        return width;
    }

    public static float totalHeight(IPDMeasurable... objects) throws IOException {
        float height = 0;

        for (IPDMeasurable m : objects) {
            height += m.getHeight();
        }

        return height;
    }

    public static float totalWidth(IPDMeasurable... objects) throws IOException {
        float width = 0;

        for (IPDMeasurable m : objects) {
            width += m.getWidth();
        }

        return width;
    }

    public static PDRectangle getRowSize(IPDMeasurable... objects) throws IOException {
        float height = 0;
        float width = 0;

        for (IPDMeasurable m : objects) {
            if (height < m.getHeight()) {
                height = m.getHeight();
            }
            width += m.getWidth();
        }

        return new PDRectangle(width, height);
    }

    public static PDRectangle getColSize(IPDMeasurable... objects) throws IOException {
        float height = 0;
        float width = 0;

        for (IPDMeasurable m : objects) {
            if (width < m.getWidth()) {
                width = m.getWidth();
            }
            height += m.getHeight();
        }

        return new PDRectangle(width, height);
    }
}
