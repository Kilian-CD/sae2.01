package newpackage;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import saejulie.Coord;
import saejulie.CoordGeo;

public class CoordGeoTest {
    
    @Test
    public void testConstructeurDeuxParams() {
        CoordGeo c = new CoordGeo(45.0, 2.0);
        assertEquals(45.0, c.getX(), 0.001);
        assertEquals(2.0, c.getY(), 0.001);
    }
    
    @Test
    public void testConstructeurTroisParams() {
        CoordGeo c = new CoordGeo(45.0, 2.0, "Point");
        assertEquals(45.0, c.getX(), 0.001);
        assertEquals(2.0, c.getY(), 0.001);
    }
    
    @Test
    public void testGetDistance() {
        CoordGeo paris = new CoordGeo(48.8566, 2.3522);
        CoordGeo lyon = new CoordGeo(45.7589, 4.8414);
        // La distance approximative entre Paris et Lyon est d'environ 392 km
        double distance = paris.getDistance(lyon);
        assertTrue(distance > 380 && distance < 400);
    }
    
    @Test
    public void testGetDistanceMemesCoordonnees() {
        CoordGeo c = new CoordGeo(45.0, 2.0);
        assertEquals(0.0, c.getDistance(c), 0.001);
    }
    
    @Test
    public void testHeritage() {
        CoordGeo c = new CoordGeo(1.0, 2.0);
        assertTrue(c instanceof Coord);
    }
    
    @Test
    public void testRayon() {
        // Vérifier que le rayon de la Terre est bien initialisé à 6378
        assertEquals(6378, CoordGeo.rTerre);
    }
}
