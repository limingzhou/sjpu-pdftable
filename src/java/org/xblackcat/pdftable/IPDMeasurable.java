package org.xblackcat.pdftable;

import java.io.IOException;

/**
 * 22.04.2016 17:25
 *
 * @author xBlackCat
 */
public interface IPDMeasurable {
    float getWidth() throws IOException;

    float getHeight() throws IOException;
}
