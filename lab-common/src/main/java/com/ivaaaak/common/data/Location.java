package com.ivaaaak.common.data;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {

    private static final long serialVersionUID = 6036432690104823382L;

    private final Long x; //не null
    private final Integer y; //не null
    private final int z;

    public Location(Long x, Integer y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Long getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "Location{"
                + "x=" + x
                + ", y=" + y
                + ", z=" + z
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return z == location.z && Objects.equals(x, location.x) && Objects.equals(y, location.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
