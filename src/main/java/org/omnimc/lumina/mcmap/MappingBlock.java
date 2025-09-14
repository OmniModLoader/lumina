package org.omnimc.lumina.mcmap;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class MappingBlock {

    private final long offset;

    private final String mappedClassName;
    private final String unmappedClassName;

    public MappingBlock(long offset, String mappedClassName, String unmappedClassName) {
        this.offset = offset;
        this.mappedClassName = mappedClassName;
        this.unmappedClassName = unmappedClassName;
    }

    public String getMappedClassName() {
        return mappedClassName;
    }

    public long getOffset() {
        return offset;
    }

    public String getUnmappedClassName() {
        return unmappedClassName;
    }

    @Override
    public String toString() {
        return "MappingBlock{" +
                "offset=" + offset +
                ", mappedClassName='" + mappedClassName + '\'' +
                ", unmappedClassName='" + unmappedClassName +
                '}';
    }
}