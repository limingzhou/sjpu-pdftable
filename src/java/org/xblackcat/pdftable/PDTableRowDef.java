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
    private final PDTableColumn cellDefs[];

    public PDTableRowDef(PDBorderStyle borderStyle, PDTableColumn... cellDefs) {
        this(borderStyle, null, cellDefs);
    }

    public PDTableRowDef(PDTableColumn... cellDefs) {
        this(null, null, cellDefs);
    }

    public PDTableRowDef(PDBorderStyle borderStyle, Color background, PDTableColumn... cellDefs) {
        this.borderStyle = borderStyle;
        this.cellDefs = cellDefs;
        this.background = background;
    }

    public PDBorderStyle getBorderStyle() {
        return borderStyle;
    }

    public Color getBackground() {
        return background;
    }

    public PDTableColumn[] getCellDefs() {
        return cellDefs;
    }
}
