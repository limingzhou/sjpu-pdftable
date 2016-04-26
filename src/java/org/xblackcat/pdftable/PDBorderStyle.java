package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

/**
 * 26.04.2016 10:19
 *
 * @author xBlackCat
 */
public class PDBorderStyle {
    public static PDBorderStyle fullBorderOf(PDLineStyle lineStyle) {
        return new PDBorderStyle(true, true, true, true, lineStyle);
    }

    public static PDBorderStyle topBottomBorderOf(PDLineStyle lineStyle) {
        return new PDBorderStyle(true, true, false, false, lineStyle);
    }

    public static PDBorderStyle leftRightBorderOf(PDLineStyle lineStyle) {
        return new PDBorderStyle(false, false, true, true, lineStyle);
    }

    private final boolean drawTop;
    private final boolean drawBottom;
    private final boolean drawLeft;
    private final boolean drawRight;
    private final PDLineStyle lineStyle;

    public PDBorderStyle(boolean drawTop, boolean drawBottom, boolean drawLeft, boolean drawRight, PDLineStyle lineStyle) {
        this.drawTop = drawTop;
        this.drawBottom = drawBottom;
        this.drawLeft = drawLeft;
        this.drawRight = drawRight;
        this.lineStyle = lineStyle;
    }

    public boolean isDrawTop() {
        return drawTop;
    }

    public boolean isDrawBottom() {
        return drawBottom;
    }

    public boolean isDrawLeft() {
        return drawLeft;
    }

    public boolean isDrawRight() {
        return drawRight;
    }

    public PDLineStyle getLineStyle() {
        return lineStyle;
    }

    public void applyToStream(PDPageContentStream stream, float x, float y, float width, float height) throws IOException {
        if (lineStyle != null) {
            lineStyle.applyToStream(stream);
        }
        stream.setLineCapStyle(2);

        if (drawTop) {
            stream.moveTo(x, y);
            stream.lineTo(x + width, y);
            stream.stroke();
        }

        if (drawBottom) {
            stream.moveTo(x, y - height);
            stream.lineTo(x + width, y - height);
            stream.stroke();
        }

        if (drawLeft) {
            stream.moveTo(x, y);
            stream.lineTo(x, y - height);
            stream.stroke();
        }

        if (drawLeft) {
            stream.moveTo(x + width, y);
            stream.lineTo(x + width, y - height);
            stream.stroke();
        }
    }
}