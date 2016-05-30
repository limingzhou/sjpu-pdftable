package org.xblackcat.pdftable;

import java.util.Objects;

public class PDInsets implements Cloneable, java.io.Serializable {

    /**
     * The inset from the top.
     * This value is added to the Top of the rectangle
     * to yield a new location for the Top.
     *
     * @serial
     * @see #clone()
     */
    public final float top;

    /**
     * The inset from the left.
     * This value is added to the Left of the rectangle
     * to yield a new location for the Left edge.
     *
     * @serial
     * @see #clone()
     */
    public final float left;

    /**
     * The inset from the bottom.
     * This value is subtracted from the Bottom of the rectangle
     * to yield a new location for the Bottom.
     *
     * @serial
     * @see #clone()
     */
    public final float bottom;

    /**
     * The inset from the right.
     * This value is subtracted from the Right of the rectangle
     * to yield a new location for the Right edge.
     *
     * @serial
     * @see #clone()
     */
    public final float right;

    /**
     * Creates and initializes a new <code>PDInsets</code> object with the
     * specified top, left, bottom, and right insets.
     *
     * @param top    the inset from the top.
     * @param left   the inset from the left.
     * @param bottom the inset from the bottom.
     * @param right  the inset from the right.
     */
    public PDInsets(float top, float left, float bottom, float right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PDInsets pdInsets = (PDInsets) o;
        return Float.compare(pdInsets.top, top) == 0 &&
                Float.compare(pdInsets.left, left) == 0 &&
                Float.compare(pdInsets.bottom, bottom) == 0 &&
                Float.compare(pdInsets.right, right) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(top, left, bottom, right);
    }

    /**
     * Returns a string representation of this <code>PDInsets</code> object.
     * This method is intended to be used only for debugging purposes, and
     * the content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not be
     * <code>null</code>.
     *
     * @return a string representation of this <code>PDInsets</code> object.
     */
    public String toString() {
        return getClass().getName() + "[top=" + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right + "]";
    }

    /**
     * Create a copy of this object.
     *
     * @return a copy of this <code>PDInsets</code> object.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
}
