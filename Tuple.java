/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appguru;

import java.util.Objects;

/**
 *
 * @author lars
 */
public class Tuple<T,J> {
    public T v1;
    public J v2;
    public Tuple(T value1, J value2) {
        this.v1=value1;
        this.v2=value2;
    }

    @Override
    public int hashCode() {
        return v1.hashCode()+v2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tuple<?, ?> other = (Tuple<?, ?>) obj;
        if (!Objects.equals(this.v1, other.v1)) {
            return false;
        }
        return Objects.equals(this.v2, other.v2);
    }
    
    @Override
    public String toString() {
        return "("+v1.toString()+"|"+v2.toString()+")";
    }
}
