import static org.junit.Assert.*;
import org.junit.Test;

public class PointTest {
    
     /*
      * This method checks if the Point constructor works
      * as expected. When the (X,Y) coordinates are updated, 
      * this checks if the setters/getters work as appropriate, 
      * as well as the toString() is not null
      */
    @Test
    public void testConstructor() {
        Point one = new Point(0, 0);
        one.setX(1);
        one.setY(2);
        int x = one.getX();
        int y = one.getY();      
        
        assertEquals(1,x);
        assertEquals(2,y);
        
        String a = one.toString();
        assertNotNull(a);  
    }

}
