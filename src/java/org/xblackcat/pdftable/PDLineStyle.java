package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.*;
import java.io.IOException;

/**
 * 26.04.2016 9:44
 *
 * @author xBlackCat
 */
public class PDLineStyle {
    public static final float DEFAULT_WIDTH = 1;

    public static PDLineStyle ofWidth(float width) {
        return ofColor(width, null);
    }

    public static PDLineStyle ofColor(Color color) {
        return ofColor(DEFAULT_WIDTH, color);
    }

    public static PDLineStyle ofColor(float width, Color color) {
        return ofColor(width, color, PDLineCapStyle.ButtCap);
    }

    public static PDLineStyle ofColor(float width, Color color, PDLineCapStyle capStyle) {
        return new PDLineStyle(width, color, capStyle, 0, null);
    }

    public static PDLineStyle ofPattern(float phase, float... pattern) {
        return ofPattern(null, phase, pattern);
    }

    public static PDLineStyle ofPattern(Color color, float phase, float... pattern) {
        return ofPattern(color, PDLineCapStyle.ButtCap, phase, pattern);
    }

    public static PDLineStyle ofPattern(Color color, PDLineCapStyle capStyle, float phase, float... pattern) {
        return new PDLineStyle(DEFAULT_WIDTH, color, capStyle, phase, pattern);
    }

    private final float width;
    private final float[] pattern;
    private final float phase;
    private final Color color;
    private final PDLineCapStyle capStyle;

    public PDLineStyle(float width, Color color, PDLineCapStyle capStyle, float phase, float... pattern) {
        this.width = width;
        this.capStyle = capStyle;
        this.pattern = pattern;
        this.phase = phase;
        this.color = color;
    }

    public float getWidth() {
        return width;
    }

    public float[] getPattern() {
        return pattern;
    }

    public float getPhase() {
        return phase;
    }

    public Color getColor() {
        return color;
    }

    public PDLineCapStyle getCapStyle() {
        return capStyle;
    }

    public PDLineStyle withCap(PDLineCapStyle newStyle) {
        return new PDLineStyle(
                width,
                color,
                newStyle,
                phase,
                pattern
        );
    }

    public void applyToStream(PDPageContentStream stream) throws IOException {
        if (pattern != null) {
            stream.setLineDashPattern(pattern, phase);
        }

        if (color != null) {
            stream.setStrokingColor(color);
        }

        if (capStyle != null) {
            stream.setLineCapStyle(capStyle.getValue());
        }
        stream.setLineWidth(width);
    }
}
