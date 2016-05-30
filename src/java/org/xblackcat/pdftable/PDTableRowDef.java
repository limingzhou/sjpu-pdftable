package org.xblackcat.pdftable;

import java.awt.*;

/**
 * 25.04.2016 12:10
 *
 * @author xBlackCat
 */
public class PDTableRowDef {
    private final PDBorderStyle borderStyle;
    private final Color background;
    private final PDTableTextCellDef cellDefs[];

    public PDTableRowDef(PDBorderStyle borderStyle, PDTableTextCellDef... cellDefs) {
        this(borderStyle, null, cellDefs);
    }

    public PDTableRowDef(PDTableTextCellDef... cellDefs) {
        this(null, null, cellDefs);
    }

    public PDTableRowDef(PDBorderStyle borderStyle, Color background, PDTableTextCellDef... cellDefs) {
        this.borderStyle = borderStyle;
        this.cellDefs = cellDefs;
        this.background = background;
    }

    /**
     * Border style of the whole row. Row border is painted after cell row borders.
     *
     * @return row border style
     */
    public PDBorderStyle getBorderStyle() {
        return borderStyle;
    }

    /**
     * Whole row background. Could be overridden by cell background
     *
     * @return row background.
     */
    public Color getBackground() {
        return background;
    }

    public PDTableTextCellDef[] getCellDefs() {
        return cellDefs;
    }
}
