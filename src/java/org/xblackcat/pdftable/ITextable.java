package org.xblackcat.pdftable;

/**
 * 22.04.2016 17:00
 *
 * @author xBlackCat
 */
public interface ITextable extends CharSequence {
    String getText();

    @Override
    default int length() {
        return getText().length();
    }

    @Override
    default char charAt(int index) {
        return getText().charAt(index);
    }

    @Override
    default CharSequence subSequence(int start, int end) {
        return getText().subSequence(start, end);
    }
}
