package org.xblackcat.pdftable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
    public static PDTextLine[] split(PDTextLine text, char separator) {
        if (text.getText().indexOf(separator) == -1) {
            return new PDTextLine[]{text};
        }

        Collection<PDTextLine> lines = new ArrayList<>();

        Collection<PDTextPart> parts = new ArrayList<>();
        for (PDTextPart p : text.getParts()) {
            if (text.getText().indexOf(separator) != -1) {
                PDTextPart[] split = split(p, separator);
                if (split[0].length() > 0) {
                    parts.add(split[0]);
                }
                if (!parts.isEmpty()) {
                    lines.add(new PDTextLine(parts));
                    parts = new ArrayList<>();
                }
                int i = 1;
                while (i < split.length - 2) {
                    if (split[i].length() > 0) {
                        lines.add(new PDTextLine(split[i]));
                    }
                    i++;
                }
                PDTextPart lastPart = split[split.length - 1];
                if (lastPart.length() > 0) {
                    parts.add(lastPart);
                }
            }
        }

        if (!parts.isEmpty()) {
            lines.add(new PDTextLine(parts));
        }
        return lines.stream().toArray(PDTextLine[]::new);
    }

    public static PDTextPart[] split(PDTextPart text, char separator) {
        if (text == null) {
            throw new NullPointerException("Text is null");
        }
        String[] split = StringUtils.split(text.getText(), separator);
        return Stream.of(split).map(text::withText).toArray(PDTextPart[]::new);
    }

    public static PDTableCell toFixedWidthCell(float desiredWidth, PDTextPart... textLine) throws IOException {
        return toFixedWidthCell(desiredWidth, new PDTextLine(textLine));
    }

    public static PDTableCell toFixedWidthCell(float desiredWidth, PDTextLine... textLines) throws IOException {
        if (ArrayUtils.isEmpty(textLines)) {
            return new PDTableCell();
        }

        Collection<PDTextLine> lines = new ArrayList<>();

        for (PDTextLine l : textLines) {
            for (PDTextLine ll : split(l, '\n')) {
                lines.addAll(wrapLine(desiredWidth, ll));
            }
        }

        return new PDTableCell(lines.stream().toArray(PDTextLine[]::new));
    }

    public static PDTableCell toCell(PDTextPart... textParts) throws IOException {
        return toCell(new PDTextLine(textParts));
    }

    public static PDTableCell toCell(PDTextLine... textLines) throws IOException {
        if (ArrayUtils.isEmpty(textLines)) {
            return new PDTableCell();
        }

        Collection<PDTextLine> lines = new ArrayList<>();

        for (PDTextLine l : textLines) {
            lines.addAll(Arrays.asList(split(l, '\n')));
        }

        return new PDTableCell(lines.stream().toArray(PDTextLine[]::new));
    }

    protected static Collection<PDTextLine> wrapLine(float desiredWidth, PDTextLine textLine) throws IOException {
        Collection<PDTextLine> lines = new ArrayList<>();
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
                lines.add(new PDTextLine(currentLine.stream().toArray(PDTextPart[]::new)));
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
            lines.add(new PDTextLine(currentLine.stream().toArray(PDTextPart[]::new)));
        }
        return lines;
    }
}
