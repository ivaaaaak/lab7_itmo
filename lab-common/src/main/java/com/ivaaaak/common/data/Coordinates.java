package com.ivaaaak.common.data;


import java.io.Serializable;
import java.util.Objects;

    public class Coordinates implements Serializable {

        private static final long serialVersionUID = -8565238074315162779L;

        private final Integer x; //Максимальное значение: 172, не null
        private final Double y; //не null

        public Coordinates(Integer x, Double y) {
            this.x = x;
            this.y = y;
        }

        public Integer getX() {
            return x;
        }

        public Double getY() {
            return y;
        }

        @Override
        public String toString() {
            return "Coordinates{"
                    + "x=" + x
                    + ", y=" + y
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
            Coordinates that = (Coordinates) o;
            return Objects.equals(x, that.x) && Objects.equals(y, that.y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

