package org.xblackcat.pdftable;

/**
 * 30.05.2016 13:36
 *
 * @author xBlackCat
 */
@FunctionalInterface
public interface IOffsetAdjuster {
    float measureOffset(float totalSize, float innerSize);
}
