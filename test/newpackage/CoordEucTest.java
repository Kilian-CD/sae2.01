package newpackage;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import saejulie.Coord;
import saejulie.CoordEuc;

public class CoordEucTest {
    
    @Test
    public void testConstructeurDeuxParams() {
        CoordEuc c = new CoordEuc(3.0, 4.0);
        assertEquals(3.0, c.getX(), 0.001);
        assertEquals(4.0, c.getY(), 0.001);
    }
    
    @Test
    public void testConstructeurTroisParams() {
        CoordEuc c = new CoordEuc(3.0, 4.0, "Point");
        assertEquals(3.0, c.getX(), 0.001);
        assertEquals(4.0, c.getY(), 0.001);
    }
    
    @Test
    public void testGetDistance() {
        CoordEuc c1 = new CoordEuc(0.0, 0.0);
        CoordEuc c2 = new CoordEuc(3.0, 4.0);
        assertEquals(5.0, c1.getDistance(c2), 0.001);
        assertEquals(5.0, c2.getDistance(c1), 0.001);
    }
    
    @Test
    public void testGetDistanceMemesCoordonnees() {
        CoordEuc c = new CoordEuc(1.0, 1.0);
        assertEquals(0.0, c.getDistance(c), 0.001);
    }
    
    @Test
    public void testHeritage() {
        CoordEuc c = new CoordEuc(1.0, 2.0);
        assertTrue(c instanceof Coord);
    }
}
