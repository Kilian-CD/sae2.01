package saejulie;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Point2D;

public class CustomWaypointRenderer implements WaypointRenderer<CustomWaypoint> {

    /**
     * Permet d'afficher les waypoints
     * @param graphics2D
     * @param jxMapViewer
     * @param customWaypoint 
     */
    @Override
    public void paintWaypoint(Graphics2D graphics2D, JXMapViewer jxMapViewer, CustomWaypoint customWaypoint) {
        Point2D point = jxMapViewer.getTileFactory().geoToPixel(customWaypoint.getPosition(), jxMapViewer.getZoom());
        int x = (int) point.getX();
        int y = (int) point.getY();
        graphics2D.setColor(customWaypoint.getCouleur());
        graphics2D.fillOval(x - 5, y - 5, 10, 10);
    }
}
