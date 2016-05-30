package org.xblackcat.pdftable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

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

        return lines.stream().map(PDFUtils::getParts).map(PDTableTextCell.CellLine::new).toArray(PDTableTextCell.CellLine[]::new);
    }

    protected static PDTableTextCell.PDTextPart[] getParts(PDStyledString text) {
        return Stream.of(text.getStyle()).map(
                p -> new PDTableTextCell.PDTextPart(text.getText().subSequence(p.getBeginIdx(), p.getEndIdx()), p.getStyle())
        ).toArray(PDTableTextCell.PDTextPart[]::new);
    }


    protected static Collection<PDTableTextCell.CellLine> wrapLine(float desiredWidth, PDStyledString textLine) throws IOException {
        Collection<PDTableTextCell.CellLine> lines = new ArrayList<>();
        Collection<PDTableTextCell.PDTextPart> currentLine = new ArrayList<>();

        float offset = 0;
        for (PDTableTextCell.PDTextPart p : getParts(textLine)) {
            String text = p.getText();
            float stringWidth = p.getWidth();

            if (offset + stringWidth < desiredWidth) {
                currentLine.add(p);
                offset += stringWidth;
                continue;
            }

            PDTableTextCell.PDTextPart part = p;
            do {
                PDTableTextCell.PDTextPart textPart;
                int expectedIndex = Math.min((int) (text.length() * (desiredWidth - offset) / stringWidth), text.length());
                int partIdx;
                do {
                    int spaceIdx = text.lastIndexOf(' ', expectedIndex);
                    final int cutIdx;
                    if (spaceIdx < 0) {
                        cutIdx = expectedIndex;
                        partIdx = expectedIndex;
                    } else {
                        cutIdx = spaceIdx;
                        partIdx = spaceIdx;
                    }

                    textPart = part.withText(text.substring(0, cutIdx));
                    expectedIndex--;
                } while (textPart.getWidth() > desiredWidth - offset);

                while (partIdx < text.length() && Character.isSpaceChar(text.charAt(partIdx))) {
                    partIdx++;
                }

                currentLine.add(textPart);
                lines.add(new PDTableTextCell.CellLine(currentLine.stream().toArray(PDTableTextCell.PDTextPart[]::new)));
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
            lines.add(new PDTableTextCell.CellLine(currentLine.stream().toArray(PDTableTextCell.PDTextPart[]::new)));
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
