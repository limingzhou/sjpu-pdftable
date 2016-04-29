package org.xblackcat.pdftable;

import java.io.IOException;

/**
 * 29.04.2016 12:34
 *
 * @author xBlackCat
 */
public abstract class APDMeasurable implements IPDMeasurable {
    private Float width;
    private Float height;

    @Override
    public final float getWidth() throws IOException {
        if (width == null) {
            width = measureWidth();
        }
        return width;
    }

    @Override
    public final float getHeight() throws IOException {
        if (height == null) {
            height = measureHeight();
        }
        return height;
    }

    protected abstract float measureWidth() throws IOException;

    protected abstract float measureHeight() throws IOException;
}
