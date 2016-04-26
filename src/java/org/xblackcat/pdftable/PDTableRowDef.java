package org.xblackcat.pdftable;

/**
 * 25.04.2016 12:10
 *
 * @author xBlackCat
 */
public class PDTableRowDef {
    private final PDBorderStyle borderStyle;
    private final PDTableColumn cellDefs[];

    public PDTableRowDef(PDBorderStyle borderStyle, PDTableColumn... cellDefs) {
        this.borderStyle = borderStyle;
        this.cellDefs = cellDefs;
    }

    public PDBorderStyle getBorderStyle() {
        return borderStyle;
    }

    public PDTableColumn[] getCellDefs() {
        return cellDefs;
    }
}
