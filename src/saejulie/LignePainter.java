package saejulie;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class LignePainter implements Painter<JXMapViewer> {
    private final List<GeoPosition> positions;

    /**
     * Initialise toutes les positions des différents points
     * @param positions 
     */
    public LignePainter(List<GeoPosition> positions) {
        this.positions = positions;
    }

    /**
     * Permet d'afficher chaque ligne reliant les points sur la JXMapViewer
     * @param graph
     * @param map
     * @param largeur
     * @param hauteur 
     */
    @Override
    public void paint(Graphics2D graph, JXMapViewer map, int largeur, int hauteur) {
        graph = (Graphics2D) graph.create();
        graph.setColor(Color.RED);
        graph.setStroke(new BasicStroke(3));

        Point2D prev = null;
        for (GeoPosition pos : positions) {
            Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());
            Point2D viewport = map.getViewportBounds().getLocation();
            int x = (int) (pt.getX() - viewport.getX());
            int y = (int) (pt.getY() - viewport.getY());

            if (prev != null) {
                graph.drawLine((int) prev.getX(), (int) prev.getY(), x, y);
            }
            prev = new Point2D.Double(x, y);
        }
        graph.dispose();
    }
}
