package org.xblackcat.pdftable;

import java.util.stream.Stream;

/**
 * 25.04.2016 12:40
 *
 * @author xBlackCat
 */
public class DataGroup {
    private final Object key;
    private final DataGroup[] values;

    public DataGroup(Object key, DataGroup... values) {
        this.key = key;
        this.values = values;
    }

    public DataGroup(Object key, Object... values) {
        this(key, Stream.of(values).map(v -> new DataGroup(v)).toArray(DataGroup[]::new));
    }

    public Object getKey() {
        return key;
    }

    public DataGroup[] getValues() {
        return values;
    }
}
