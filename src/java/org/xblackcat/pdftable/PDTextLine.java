package org.xblackcat.pdftable;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * 22.04.2016 16:56
 *
 * @author xBlackCat
 */
public class PDTextLine implements ITextable, IPDMeasurable {
    private final PDTextPart[] parts;

    public PDTextLine(Collection<PDTextPart> parts) {
        if (parts == null) {
            throw new NullPointerException("Null value is not allowed. Pass empty collection instead");
        }
        this.parts = parts.stream().toArray(PDTextPart[]::new);
    }

    public PDTextLine(PDTextPart... parts) {
        if (parts == null) {
            throw new NullPointerException("Null value is not allowed. Pass empty array instead");
        }
        this.parts = parts;
    }

    public PDTextPart[] getParts() {
        return parts;
    }

    public String getText() {
        StringBuilder builder = new StringBuilder();
        for (CharSequence s : parts) {
            builder.append(s);
        }
        return builder.toString();
    }

    public PDTextLine append(PDTextPart... newParts) {
        return new PDTextLine(ArrayUtils.addAll(getParts(), newParts));
    }

    @Override
    public float getWidth() throws IOException {
        float width = 0;
        for (IPDMeasurable m : parts) {
            width += m.getWidth();
        }
        return width;
    }

    @Override
    public float getHeight() throws IOException {
        float height = 0;
        for (PDTextPart p : parts) {
            float h = p.getHeight();
            if (height < h) {
                height = h;
            }
        }
        return height;
    }

    @Override
    public int length() {
        return Stream.of(parts).mapToInt(CharSequence::length).sum();
    }

    @Override
    public char charAt(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Negative index");
        }
        int l = 0;
        for (PDTextPart p : parts) {
            int length = p.length();
            if (l + length > index) {
                return p.charAt(index - length);
            }
            l += length;
        }
        throw new IndexOutOfBoundsException("Index is greater than string length");
    }

    @Override
    public String toString() {
        return getText();
    }
}
