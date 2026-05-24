package saejulie;

public class CoordEuc extends Coord{
    
    /**
     * Constructeur avec paramètres qui initialise les coordonnées du point
     * @param x
     * @param y 
     */
    public CoordEuc(double x, double y){
        super(x, y);
    }

    /**
     * Constructeur avec paramètres qui initialise les coordonnées du point
     * @param x
     * @param y 
     * @param nom
     */
    public CoordEuc(double x, double y, String nom) {
        super(x, y, nom);
    }
    
    /**
     * Permet de récupérer la distance d'un point A à un point B
     * @param coord les coordonnées du deuxième point
     * @return la distance séparant ces deux points
     */
    @Override
    public double getDistance(Coord coord) {
        return Math.sqrt(Math.pow(coord.getX()-this.getX(), 2) + Math.pow(coord.getY()-this.getY(), 2));
    }
}
