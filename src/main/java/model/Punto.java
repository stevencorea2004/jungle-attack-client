package model;

import java.util.Objects;

public class Punto {
    private double x;
    private double y;

    public Punto() {
        this(0, 0);
    }

    public Punto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double distancia(Punto otro) {
        if (otro == null) {
            return Double.MAX_VALUE;
        }
        return Math.hypot(x - otro.x, y - otro.y);
    }

    public Punto interpolar(Punto destino, double t) {
        if (destino == null) {
            return this;
        }
        double factor = Math.max(0.0, Math.min(1.0, t));
        return new Punto(
            x + (destino.x - x) * factor,
            y + (destino.y - y) * factor
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Punto)) return false;
        Punto other = (Punto) obj;
        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Punto{" + "x=" + x + ", y=" + y + '}';
    }
}
