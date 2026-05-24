package newpackage;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import saejulie.TypeCoord;

public class TypeCoordTest {
    
    @Test
    public void testEnumValues() {
        assertEquals(2, TypeCoord.values().length);
        assertEquals(TypeCoord.EUC_2D, TypeCoord.valueOf("EUC_2D"));
        assertEquals(TypeCoord.GEO, TypeCoord.valueOf("GEO"));
    }
}
