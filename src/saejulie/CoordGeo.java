package saejulie;

public class CoordGeo extends Coord {
    
    /**
     * Constructeur avec paramètres qui initialise les coordonnées du point
     * @param x
     * @param y 
     */
    public CoordGeo(double x, double y) {
        super(x, y);
    }

    /**
     * Constructeur avec paramètres qui initialise les coordonnées du point et donne un nom au point
     * @param x
     * @param y
     * @param nom 
     */
    public CoordGeo(double x, double y, String nom) {
        super(x, y, nom);
    }

    /**
     * Permet de récupérer la distance d'un point A à un point B
     * @param coord les coordonnées du deuxième point
     * @return la distance séparant ces deux points
     */
    @Override
    public double getDistance(Coord coord) {
        double lat1R = Math.toRadians(this.getX());
        double lon1R = Math.toRadians(this.getY());
        double lat2R = Math.toRadians(coord.getX());
        double lon2R = Math.toRadians(coord.getY());

        return Coord.rTerre * Math.acos(
            Math.sin(lat1R) * Math.sin(lat2R) + 
            Math.cos(lat1R) * Math.cos(lat2R) * Math.cos(lon1R - lon2R)
        );
    }
}