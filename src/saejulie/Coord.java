package saejulie;

import java.util.ArrayList;

public abstract class Coord {
    private double x;
    private double y;
    private String nom;
    public static int rTerre = 6378;

    /**
     * Constructeur avec paramètres
     * @param x la coordonnée en x
     * @param y la coordonnée en y
     * @param nom le nom du point
     */
    public Coord(double x, double y, String nom) {
        this.x = x;
        this.y = y;
        this.nom = nom;
    }

    /**
     * Constructeur avec paramètres
     * @param x la coordonnée en x
     * @param y la coordonnée en y
     */
    public Coord(double x, double y) {
        this.x = x;
        this.y = y;
        this.nom = "Point (" + x + ", " + y + ")";
    }

    /**
     * Getter
     * @return x la coordonnée en x
     */
    public double getX() {
        return x;
    }

    /**
     * Permet de remplacer la coordonnée en x
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Getter
     * @return y la coordonnée en y
     */
    public double getY() {
        return y;
    }

    /**
     * Permet de remplacer la coordonnée en y
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * Getter
     * @return nom le nom du point
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * Permet de remplacer le nom du point
     * @param nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Méthode à adapter en fonction des classes filles
     * @param coord
     * @return double qui correspondra à la distance entre ce point et le point passé en paramètre
     */
    public abstract double getDistance(Coord coord);

    /**
     * Permet de transformer les coordonnées en une chaîne de caractères String
     * @return la chaîne de caractères contenant les coordonnées du point
     */
    @Override
    public String toString(){
        return this.nom + " : (" + x + ", " + y + ")";
    }

    /**
     * Permet de comparer si des points possèdent les mêmes coordonnées
     * @param coord
     * @return un booléen
     */
    public boolean compareTo(Coord coord) {
        return this.x == coord.getX() && this.y == coord.getY() && this.nom.equals(coord.getNom());
    }
}
