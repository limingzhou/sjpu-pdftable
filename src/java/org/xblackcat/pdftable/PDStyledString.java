package org.xblackcat.pdftable;

import java.util.*;
import java.util.stream.Stream;

/**
 * 22.04.2016 16:56
 *
 * @author xBlackCat
 */
public class PDStyledString implements CharSequence {
    private final CharSequence text;
    private final StylePart[] styleParts;

    public PDStyledString(CharSequence str, PDTextStyle style) {
        this(str, new StylePart(0, str.length(), style));
    }

    private PDStyledString(CharSequence text, StylePart... styleParts) {
        if (text == null) {
            throw new NullPointerException("String can't be null");
        }
        if (styleParts == null || styleParts.length == 0) {
            throw new IllegalArgumentException("At least one style should be specified");
        }
        this.text = text;
        this.styleParts = styleParts;
    }

    public PDTextPart[] getParts() {
        return Stream.of(styleParts).map(p -> new PDTextPart(text.subSequence(p.beginIdx, p.endIdx), p.style)).toArray(PDTextPart[]::new);
    }

    public String getText() {
        return text.toString();
    }

    @Override
    public int length() {
        return getText().length();
    }

    @Override
    public char charAt(int index) {
        return getText().charAt(index);
    }

    @Override
    public PDStyledString subSequence(int start, int end) {
        final List<StylePart> styleParts = Arrays.asList(this.styleParts);
        return getPdStyledSubstring(text, styleParts, start, end);
    }


    /**
     * Returns the index within this string of the first occurrence of
     * the specified character. If a character with value
     * {@code ch} occurs in the character sequence represented by
     * this {@code String} object, then the index (in Unicode
     * code units) of the first such occurrence is returned. For
     * values of {@code ch} in the range from 0 to 0xFFFF
     * (inclusive), this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string, then {@code -1} is returned.
     *
     * @param ch a character (Unicode code point).
     * @return the index of the first occurrence of the character in the
     * character sequence represented by this object, or
     * {@code -1} if the character does not occur.
     */
    public int indexOf(int ch) {
        return indexOf(ch, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified character, starting the search at the specified index.
     * <p>
     * If a character with value {@code ch} occurs in the
     * character sequence represented by this {@code String}
     * object at an index no smaller than {@code fromIndex}, then
     * the index of the first such occurrence is returned. For values
     * of {@code ch} in the range from 0 to 0xFFFF (inclusive),
     * this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &gt;= fromIndex)
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &gt;= fromIndex)
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string at or after position {@code fromIndex}, then
     * {@code -1} is returned.
     * <p>
     * <p>
     * There is no restriction on the value of {@code fromIndex}. If it
     * is negative, it has the same effect as if it were zero: this entire
     * string may be searched. If it is greater than the length of this
     * string, it has the same effect as if it were equal to the length of
     * this string: {@code -1} is returned.
     * <p>
     * <p>All indices are specified in {@code char} values
     * (Unicode code units).
     *
     * @param ch        a character (Unicode code point).
     * @param fromIndex the index to start the search from.
     * @return the index of the first occurrence of the character in the
     * character sequence represented by this object that is greater
     * than or equal to {@code fromIndex}, or {@code -1}
     * if the character does not occur.
     */
    public int indexOf(int ch, int fromIndex) {
        final int max = text.length();
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            // Note: fromIndex might be near -1>>>1.
            return -1;
        }

        if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            // handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point))
            for (int i = fromIndex; i < max; i++) {
                if (text.charAt(i) == ch) {
                    return i;
                }
            }
            return -1;
        } else {
            return indexOfSupplementary(ch, fromIndex);
        }
    }

    /**
     * Handles (rare) calls of indexOf with a supplementary character.
     */
    private int indexOfSupplementary(int ch, int fromIndex) {
        if (Character.isValidCodePoint(ch)) {
            final char hi = Character.highSurrogate(ch);
            final char lo = Character.lowSurrogate(ch);
            final int max = text.length() - 1;
            for (int i = fromIndex; i < max; i++) {
                if (text.charAt(i) == hi && text.charAt(i + 1) == lo) {
                    return i;
                }
            }
        }
        return -1;
    }


    public PDStyledString[] split(char separator) {
        int separatorIdx = indexOf(separator);
        if (separatorIdx == -1) {
            return new PDStyledString[]{this};
        }

        Collection<PDStyledString> lines = new ArrayList<>();
        final List<StylePart> styleParts = Arrays.asList(this.styleParts);
        int idxOffset = 0;
        do {
            // Avoid producing empty strings
            if (idxOffset != separatorIdx + 1) {
                lines.add(getPdStyledSubstring(text, styleParts, idxOffset, separatorIdx));
            }
            idxOffset = separatorIdx + 1;
            separatorIdx = indexOf(separator, idxOffset);
        } while (separatorIdx != -1);

        if (idxOffset < text.length() - 1) {
            lines.add(getPdStyledSubstring(text, styleParts, idxOffset, text.length()));
        }

        return lines.stream().toArray(PDStyledString[]::new);
    }

    protected static PDStyledString getPdStyledSubstring(
            CharSequence string,
            Collection<StylePart> styleParts,
            int start,
            int end
    ) {
        CharSequence text = string.subSequence(start, end);
        Collection<StylePart> newParts = new ArrayList<>();
        final Iterator<StylePart> it = styleParts.iterator();

        StylePart next;
        if (it.hasNext()) {
            do {
                next = it.next();
                if (next.endIdx > start) {
                    break;
                }
            } while (it.hasNext());
            if (next.endIdx >= end) {
                // One-style substring
                newParts.add(new StylePart(0, end - start, next.style));
            } else {
                newParts.add(new StylePart(0, next.endIdx - start, next.style));
                while (it.hasNext() && (next = it.next()).endIdx < end) {
                    newParts.add(new StylePart(0, next.endIdx - start, next.style));
                }
                newParts.add(new StylePart(next.beginIdx - start, end - start, next.style));
            }
        }

        return new PDStyledString(text, newParts.stream().toArray(StylePart[]::new));
    }

    private final static class StylePart {
        private final int beginIdx;
        private final int endIdx;
        private final PDTextStyle style;

        private StylePart(int beginIdx, int endIdx, PDTextStyle style) {
            this.beginIdx = beginIdx;
            this.endIdx = endIdx;
            this.style = style;
        }

        private StylePart shift(int offset) {
            return new StylePart(beginIdx + offset, endIdx + offset, style);
        }

        public StylePart enlarge(int sizeShift) {
            return new StylePart(beginIdx, sizeShift + sizeShift, style);
        }

        public StylePart cutHead(int idx) {
            return new StylePart(beginIdx, idx, style);
        }

        public StylePart cutTail(int idx) {
            return new StylePart(0, endIdx - idx, style);
        }
    }

    public final static class Builder implements Appendable, CharSequence {
        private final StringBuilder str = new StringBuilder();
        private final List<StylePart> parts = new ArrayList<>();

        private final PDTextStyle defaultStyle;

        public Builder(PDTextStyle defaultStyle) {
            if (defaultStyle == null) {
                throw new NullPointerException("Default string style can't be null");
            }
            this.defaultStyle = defaultStyle;
        }

        @Override
        public Appendable append(CharSequence csq) {
            if (csq instanceof PDStyledString) {
                return append((PDStyledString) csq);
            }
            return append(csq, defaultStyle);
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) {
            if (csq instanceof PDStyledString) {
                return append((PDStyledString) csq, start, end);
            }
            return append(csq, defaultStyle, start, end);
        }

        public Appendable append(CharSequence csq, PDTextStyle style) {
            return append(new PDStyledString(csq, style));
        }

        public Appendable append(CharSequence csq, PDTextStyle style, int start, int end) {
            return append(new PDStyledString(csq, style), start, end);
        }

        @Override
        public Appendable append(char c) {
            return append(c, defaultStyle);
        }

        public Appendable append(PDStyledString csq) {
            final int initLength = str.length();
            str.append(csq);
            if (parts.isEmpty()) {
                parts.addAll(Arrays.asList(csq.styleParts));
            } else {
                final int lastIdx = parts.size() - 1;
                final StylePart lastPart = parts.get(lastIdx);

                final StylePart firstNewPart = csq.styleParts[0];
                if (lastPart.style.equals(firstNewPart.style)) {
                    parts.set(lastIdx, lastPart.enlarge(firstNewPart.endIdx));

                    Stream.of(csq.styleParts).skip(1).map(s -> s.shift(initLength)).forEachOrdered(parts::add);
                } else {
                    Stream.of(csq.styleParts).map(s -> s.shift(initLength)).forEachOrdered(parts::add);
                }
            }
            return this;
        }

        public Appendable append(PDStyledString csq, int start, int end) {
            return append(csq.subSequence(start, end));
        }

        public Appendable append(char c, PDTextStyle style) {
            final int initLength = str.length();
            str.append(c);
            if (parts.isEmpty()) {
                parts.add(new StylePart(0, 1, style));
            } else {
                final int lastIdx = parts.size() - 1;
                final StylePart lastPart = parts.get(lastIdx);

                if (lastPart.style.equals(style)) {
                    parts.set(lastIdx, lastPart.enlarge(1));
                } else {
                    parts.add(new StylePart(initLength, initLength + 1, style));
                }
            }
            return this;
        }

        @Override
        public int length() {
            return str.length();
        }

        @Override
        public char charAt(int index) {
            return str.charAt(index);
        }

        @Override
        public PDStyledString subSequence(int start, int end) {
            return getPdStyledSubstring(str, parts, start, end);
        }

        public PDStyledString toStyledString() {
            return new PDStyledString(str.toString(), parts.stream().toArray(StylePart[]::new));
        }
    }
}
