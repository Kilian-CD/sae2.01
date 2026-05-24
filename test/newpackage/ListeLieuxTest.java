package newpackage;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import saejulie.ListeLieux;
import saejulie.Coord;
import saejulie.CoordEuc;
import saejulie.TypeCoord;
import java.util.ArrayList;

public class ListeLieuxTest {
    
    private ListeLieux liste;
    private Coord point1;
    private Coord point2;
    
    @Before
    public void setUp() {
        liste = new ListeLieux("Test", "Type de test", "Commentaire", TypeCoord.EUC_2D);
        point1 = new CoordEuc(0.0, 0.0);
        point2 = new CoordEuc(3.0, 4.0);
    }
    
    @Test
    public void testConstructeurSimple() {
        ListeLieux l = new ListeLieux();
        assertNotNull(l.getLieux());
        assertEquals(0, l.getLieux().size());
    }
    
    @Test
    public void testConstructeurComplet() {
        ListeLieux l = new ListeLieux("Nom", "Type", "Commentaire", TypeCoord.EUC_2D);
        assertEquals("Nom", l.getNom());
        assertEquals("Type", l.getType());
        assertEquals("Commentaire", l.getCommentaire());
        assertEquals(TypeCoord.EUC_2D, l.getTypeCoord());
    }
    
    @Test
    public void testGettersSetters() {
        liste.setNom("Nouveau nom");
        liste.setType("Nouveau type");
        liste.setCommentaire("Nouveau commentaire");
        
        assertEquals("Nouveau nom", liste.getNom());
        assertEquals("Nouveau type", liste.getType());
        assertEquals("Nouveau commentaire", liste.getCommentaire());
    }
    
    @Test
    public void testAjoutLieu() {
        liste.addLieu(point1);
        assertEquals(1, liste.getLieux().size());
        assertEquals(point1, liste.getLieu(0));
        
        liste.addLieu(point2);
        assertEquals(2, liste.getLieux().size());
        assertEquals(point2, liste.getLieu(1));
    }
    
    @Test
    public void testAjoutLieuAvecIndex() {
        liste.addLieu(point1);
        liste.addLieu(point2, 0); // Insérer au début
        assertEquals(point2, liste.getLieu(0));
        assertEquals(point1, liste.getLieu(1));
    }
    
    @Test
    public void testSuppressionLieu() {
        liste.addLieu(point1);
        liste.addLieu(point2);
        liste.removeLieu(point1);
        
        assertEquals(1, liste.getLieux().size());
        assertEquals(point2, liste.getLieu(0));
    }
    
    @Test
    public void testDistanceTotal() {
        liste.addLieu(point1); // (0,0)
        liste.addLieu(point2); // (3,4)
        liste.addLieu(new CoordEuc(0, 0)); // Retour au point de départ
        
        // Distance: 5 (de point1 à point2) + 5 (de point2 à point3) = 10
        assertEquals(10.0, liste.getDistanceTotal(), 0.001);
    }
    
    @Test
    public void testMinMax() {
        liste.addLieu(new CoordEuc(-1, -2));
        liste.addLieu(new CoordEuc(3, 4));
        
        assertEquals(-1.000, liste.getXMin(),0.001);
        assertEquals(3.000, liste.getXMax(),0.001);
        assertEquals(-2.000, liste.getYMin(),0.001);
        assertEquals(4.000, liste.getYMax(),0.001);
    }
}
