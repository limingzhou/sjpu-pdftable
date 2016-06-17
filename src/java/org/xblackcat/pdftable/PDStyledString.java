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
    final StylePart[] styleParts;

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

    public StylePart[] getStyle() {
        return styleParts.clone();
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

    @Override
    public String toString() {
        return getText();
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

    public final static class StylePart {
        private final int beginIdx;
        private final int endIdx;
        private final PDTextStyle style;

        StylePart(int beginIdx, int endIdx, PDTextStyle style) {
            this.beginIdx = beginIdx;
            this.endIdx = endIdx;
            this.style = style;
        }

        private StylePart shift(int offset) {
            return new StylePart(beginIdx + offset, endIdx + offset, style);
        }

        public StylePart enlarge(int sizeShift) {
            return new StylePart(beginIdx, endIdx + sizeShift, style);
        }

        public StylePart cutHead(int idx) {
            return new StylePart(beginIdx, idx, style);
        }

        public StylePart cutTail(int idx) {
            return new StylePart(0, endIdx - idx, style);
        }

        public StylePart setEnd(int endIdx) {
            if (endIdx <= beginIdx) {
                throw new IllegalArgumentException("End index should be greater than begin index");
            }
            return new StylePart(beginIdx, endIdx, style);
        }

        public PDTextStyle getStyle() {
            return style;
        }

        public int getEndIdx() {
            return endIdx;
        }

        public int getBeginIdx() {
            return beginIdx;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final StylePart stylePart = (StylePart) o;
            return beginIdx == stylePart.beginIdx &&
                    endIdx == stylePart.endIdx &&
                    Objects.equals(style, stylePart.style);
        }

        @Override
        public int hashCode() {
            return Objects.hash(beginIdx, endIdx, style);
        }

        @Override
        public String toString() {
            return "[" + beginIdx + ", " + endIdx + ") for style " + style;
        }
    }

    public final static class Builder implements Appendable, CharSequence {
        final StringBuilder str = new StringBuilder();
        final List<StylePart> parts = new ArrayList<>();

        private final PDTextStyle defaultStyle;

        public Builder(PDTextStyle defaultStyle) {
            if (defaultStyle == null) {
                throw new NullPointerException("Default string style can't be null");
            }
            this.defaultStyle = defaultStyle;
        }

        @Override
        public Builder append(CharSequence csq) {
            if (csq instanceof PDStyledString) {
                return append((PDStyledString) csq);
            }
            return append(csq, defaultStyle);
        }

        @Override
        public Builder append(CharSequence csq, int start, int end) {
            if (csq instanceof PDStyledString) {
                return append((PDStyledString) csq, start, end);
            }
            return append(csq, defaultStyle, start, end);
        }

        public Builder append(CharSequence csq, PDTextStyle style) {
            return append(new PDStyledString(csq, style));
        }

        public Builder append(CharSequence csq, PDTextStyle style, int start, int end) {
            return append(new PDStyledString(csq, style), start, end);
        }

        @Override
        public Builder append(char c) {
            return append(c, defaultStyle);
        }

        public Builder append(PDStyledString csq) {
            if (csq.length() == 0) {
                // Got nothing - do nothing
                return this;
            }
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

        public Builder append(PDStyledString csq, int start, int end) {
            return append(csq.subSequence(start, end));
        }

        public Builder append(char c, PDTextStyle style) {
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

        /**
         * Apply default style to entire string.
         *
         * @return this builder object
         */
        public Builder clearStyle() {
            return setStyle(defaultStyle);
        }

        /**
         * Apply default style to specified range
         *
         * @param beginIndex begin index (inclusive)
         * @param endIndex   end index (exclusive)
         * @return this builder object
         */
        public Builder clearStyle(int beginIndex, int endIndex) {
            return setStyle(defaultStyle, beginIndex, endIndex);
        }

        /**
         * Apply a style to entire string.
         *
         * @param style style to set
         * @return this builder object
         */
        public Builder setStyle(PDTextStyle style) {
            parts.clear();
            parts.add(new StylePart(0, str.length(), style));
            return this;
        }

        /**
         * Apply a style to specified range
         *
         * @param style      style to set
         * @param beginIndex begin index (inclusive)
         * @param endIndex   end index (exclusive)
         * @return this builder object
         */
        public Builder setStyle(PDTextStyle style, int beginIndex, int endIndex) {
            if (style == null) {
                throw new NullPointerException("Style can't be null");
            }
            if (beginIndex < 0) {
                throw new StringIndexOutOfBoundsException(beginIndex);
            }
            if (endIndex > str.length()) {
                throw new StringIndexOutOfBoundsException(endIndex);
            }
            int subLen = endIndex - beginIndex;
            if (subLen < 0) {
                throw new StringIndexOutOfBoundsException(subLen);
            }
            if (beginIndex == 0 && endIndex == str.length()) {
                return setStyle(style);
            }

            List<StylePart> newParts = new ArrayList<>();
            Iterator<StylePart> it = parts.iterator();
            StylePart p = null;
            while (it.hasNext()) {
                p = it.next();
                if (beginIndex < p.endIdx) {
                    break;
                }
                newParts.add(p);
            }
            if (p != null) {
                if (p.beginIdx < beginIndex) {
                    newParts.add(new StylePart(p.beginIdx, beginIndex, p.style));
                }

                newParts.add(new StylePart(beginIndex, endIndex, style));

                if (endIndex > p.endIdx) {
                    while (it.hasNext()) {
                        p = it.next();
                        if (endIndex <= p.endIdx) {
                            break;
                        }
                    }
                }

                if (endIndex < p.endIdx) {
                    newParts.add(new StylePart(endIndex, p.endIdx, p.style));
                }
                it.forEachRemaining(newParts::add);

                parts.clear();
                parts.addAll(newParts);
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
            // Merge parts before building string
            List<StylePart> pp = new ArrayList<>();
            StylePart lastPart = null;
            for (StylePart p : parts) {
                if (lastPart == null) {
                    lastPart = p;
                    continue;
                }

                if (lastPart.style.equals(p.style)) {
                    lastPart = lastPart.setEnd(p.endIdx);
                } else {
                    pp.add(lastPart);
                    lastPart = p;
                }
            }
            pp.add(lastPart);

            return new PDStyledString(str.toString(), pp.stream().toArray(StylePart[]::new));
        }
    }
}
