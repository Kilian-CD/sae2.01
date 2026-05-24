package newpackage;

import org.junit.Test;
import static org.junit.Assert.*;
import saejulie.Coord;

public class CoordTest {
    
    // Classe concrète pour tester la classe abstraite Coord
    private class CoordConcrete extends Coord {
        public CoordConcrete(double x, double y) {
            super(x, y);
        }
        
        public CoordConcrete(double x, double y, String name) {
            super(x, y, name);
        }
        
        @Override
        public double getDistance(Coord c) {
            return Math.sqrt(Math.pow(this.getX() - c.getX(), 2) + Math.pow(this.getY() - c.getY(), 2));
        }
    }
    
    @Test
    public void testConstructeurDeuxParams() {
        Coord c = new CoordConcrete(1.0, 2.0);
        assertEquals(1.0, c.getX(), 0.001);
        assertEquals(2.0, c.getY(), 0.001);
    }
    
    @Test
    public void testConstructeurTroisParams() {
        Coord c = new CoordConcrete(1.0, 2.0, "Point");
        assertEquals(1.0, c.getX(), 0.001);
        assertEquals(2.0, c.getY(), 0.001);
    }
    
    @Test
    public void testSetters() {
        Coord c = new CoordConcrete(0.0, 0.0);
        c.setX(3.0);
        c.setY(4.0);
        assertEquals(3.0, c.getX(), 0.001);
        assertEquals(4.0, c.getY(), 0.001);
    }
    
    @Test
    public void testToString() {
        Coord c = new CoordConcrete(1.5, 2.5);
        assertEquals("(1.5, 2.5)", c.toString());
    }
}
