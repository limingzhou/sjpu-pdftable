package org.xblackcat.pdftable;

import java.awt.*;
import java.io.IOException;

/**
 * 25.04.2016 13:02
 *
 * @author xBlackCat
 */
public class PDTableTextCellDef extends APDTableCellDef {
    private final float textSpacing;
    private final PDTableCellTextGetter textGetter;
    private final HorizontalAlign horizontalTextAlign;
    private final VerticalAlign verticalTextAlign;
    private final VerticalAlign verticalTextLineAlign;

    public PDTableTextCellDef(
            int idx,
            float width,
            PDInsets padding,
            Color background,
            float textSpacing,
            PDTableCellTextGetter textGetter,
            PDBorderStyle cellBorderStyle,
            HorizontalAlign horizontalTextAlign,
            VerticalAlign verticalTextAlign,
            VerticalAlign verticalTextLineAlign
    ) {
        super(padding, background, width, cellBorderStyle, idx);
        this.textSpacing = textSpacing;
        this.textGetter = textGetter;
        this.horizontalTextAlign = horizontalTextAlign;
        this.verticalTextAlign = verticalTextAlign;
        this.verticalTextLineAlign = verticalTextLineAlign;
    }

    public float getTextSpacing() {
        return textSpacing;
    }

    public PDTableCellTextGetter getTextGetter() {
        return textGetter;
    }

    public HorizontalAlign getHorizontalTextAlign() {
        return horizontalTextAlign;
    }

    public VerticalAlign getVerticalTextAlign() {
        return verticalTextAlign;
    }

    @Override
    public APDTableCell buildCell(Object valueObj, int i, int curRow, int curPage) throws IOException {
        PDStyledString value = getTextGetter().getValue(valueObj, i, curRow, curPage);
        if (getWidth() >= 0) {
            return PDTableTextCell.toFixedWidthCell(
                    textSpacing,
                    padding,
                    width,
                    horizontalTextAlign,
                    verticalTextAlign,
                    verticalTextLineAlign,
                    value
            );
        } else {
            return PDTableTextCell.toCell(textSpacing, padding, horizontalTextAlign, verticalTextAlign, verticalTextLineAlign, value);
        }
    }
}
