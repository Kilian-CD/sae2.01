package saejulie;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import java.awt.*;

public class CustomWaypoint implements Waypoint {
    private final GeoPosition position;
    private final Color couleur;

    /**
     * Constructeur avec paramètres
     * @param x
     * @param y
     * @param couleur 
     */
    public CustomWaypoint(double x, double y, Color couleur) {
        this.position = new GeoPosition(x, y);
        this.couleur = couleur;
    }

    /**
     * Getter
     * @return position du point
     */
    @Override
    public GeoPosition getPosition() {
        return position;
    }

    /**
     * getter
     * @return couleur du point 
     */
    public Color getCouleur() {
        return couleur;
    }
}
