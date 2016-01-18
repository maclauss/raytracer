package com.surenot.raytracer.primitives;

/**
 * Created by surenot on 1/16/16.
 */
public class Surface {

    private final int color;
    private final double ambientReflectionCoefficient;
    private final double diffuseReflectionCoefficient;
    private final double specularReflectionCoefficient;
    private final double specularReflectionExponent;

    public Surface(
            int color,
            double ambientReflectionCoefficient,
            double diffuseReflectionCoefficient,
            double specularReflectionCoefficient,
            double specularReflectionExponent) {
        this.color = color;
        this.ambientReflectionCoefficient = ambientReflectionCoefficient;
        this.diffuseReflectionCoefficient = diffuseReflectionCoefficient;
        this.specularReflectionCoefficient = specularReflectionCoefficient;
        this.specularReflectionExponent = specularReflectionExponent;
    }

    public int getColor(){
        return color;
    }

    public double getAmbientReflectionCoefficient() {
        return ambientReflectionCoefficient;
    }

    public double getDiffuseReflectionCoefficient() {
        return diffuseReflectionCoefficient;
    }

    public double getSpecularReflectionCoefficient() {
        return specularReflectionCoefficient;
    }

    public double getSpecularReflectionExponent() {
        return specularReflectionExponent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Surface surface = (Surface) o;

        if (color != surface.color) return false;
        if (Double.compare(surface.ambientReflectionCoefficient, ambientReflectionCoefficient) != 0) return false;
        if (Double.compare(surface.diffuseReflectionCoefficient, diffuseReflectionCoefficient) != 0) return false;
        if (Double.compare(surface.specularReflectionCoefficient, specularReflectionCoefficient) != 0) return false;
        return Double.compare(surface.specularReflectionExponent, specularReflectionExponent) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = color;
        temp = Double.doubleToLongBits(ambientReflectionCoefficient);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(diffuseReflectionCoefficient);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(specularReflectionCoefficient);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(specularReflectionExponent);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Surface{" +
                "color=" + color +
                ", ambientReflectionCoefficient=" + ambientReflectionCoefficient +
                ", diffuseReflectionCoefficient=" + diffuseReflectionCoefficient +
                ", specularReflectionCoefficient=" + specularReflectionCoefficient +
                ", specularReflectionExponent=" + specularReflectionExponent +
                '}';
    }
}
