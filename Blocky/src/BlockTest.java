import static org.junit.Assert.*;
import java.awt.Color;
import org.junit.Before;
import org.junit.Test;



/**
 * @author Daniel Xu
 */

public class BlockTest {
    

    public static final Color[] COLORS = 
        { Color.BLUE, Color.RED, Color.WHITE,
        Color.YELLOW, Color.CYAN, Color.GRAY, Color.GREEN, Color.PINK };
    
    @Before
    public void setUp() throws Exception {
    }

    /*
     * This checks if the Block constructor works as intended, as 
     * well as the accessors
     */
    @Test
    public void constructorAndAccessors() {
        Point topLeft = new Point(0,0);
        Point botRight = new Point(8,8);
        Block parent = new Block(topLeft, botRight, 0, null);
        parent.setColor(COLORS[2]);
        int depth = parent.depth();
        Color color = parent.getColor();
        
        assertNotNull(parent); 
        assertEquals(0, depth);
        assertNotNull(color);
        assertNotNull(parent.getTopLeft());
        assertNotNull(parent.getBotRight()); 
        
    }
    
    /*
     * Checks if smash() method works as intended, and checks
     * if parent node starts as a leaf, and when smash() is called, 
     * checks if parent is no longer a leaf and checks if the children
     * nodes are leaves. This also checks if the child sub-block has a 
     * depth greater than parent 
     * 
     */
    @Test
    public void smash() {
        Point topLeft = new Point(0,0);
        Point botRight = new Point(8,8);
        Block parent = new Block(topLeft, botRight, 0, null);
        parent.setColor(COLORS[2]);
        
        //Check parent isLeaf before smashing 
        assertTrue(parent.isleaf());

        //Smash and check parent isLeaf and check children isLeaf
        parent.smash(3);

        int blockDepth = parent.getTopLeftTree().depth();
        assertEquals(1, blockDepth);
        assertNotNull(parent.getTopRightTree());
        assertFalse(parent.isleaf());
        assertEquals(4, parent.children().size());
        assertTrue(parent.getBotLeftTree().isleaf());
    }
    
    /**
     * Checks if the rotate() method works as intended, and when a rotation is called,
     * this checks of the coordinates are appropriately updated
     *
     */
    @Test
    public void rotate() {
        //Create block and smash into depth 1 of all blocks
        Point topLeft = new Point(0,0);
        Point botRight = new Point(8,8);
        Block root = new Block(topLeft, botRight, 0, null);
        root.smash(3);
        root.getBotRightTree().smash(3);
        root.rotate();

        Point checkTopLeft = new Point(0,0);
        Point checkBotRight = new Point(4,4);
        //Retrieve smashed block at depth 1 coordinates
        Point thisTopLeft = root.getTopLeftTree().getTopLeft();
        Point thisBotRight = root.getTopLeftTree().getBotRight();
        
        //Check coordinates XY of topLeft
        assertEquals(checkTopLeft.getX(), thisTopLeft.getX());
        assertEquals(checkTopLeft.getY(), thisTopLeft.getY());
        //Check coordinates XY of botRight
        assertEquals(checkBotRight.getX(), thisBotRight.getX());
        assertEquals(checkBotRight.getY(), thisBotRight.getY());
        
    }
    
    /*
     * This method checks the setter/getter for assigning the parent node.
     * The following also overridden the equals method to allow this test
     * to check if two blocks are the same block. Also checks root1 Block
     * against a root3 Block, which should return false because they have different
     * Points()
     */
    @Test
    public void testSetParent() {
        Point topLeft = new Point(0,0);
        Point botRight = new Point(8,8);
        
        Point topLeft3 = new Point(1,1);
        Point botRight3 = new Point(3,3);
        Block root1 = new Block(topLeft, botRight, 0, null);
        Block root2 = new Block(topLeft, botRight, 0, null);
        Block root3 = new Block(topLeft3, botRight3, 0, null);
        
        root2.setParent(root1);
        
        assertEquals(root1, root2.getParent());
        assertNotEquals(root1, root3);
        assertNotEquals(root1, topLeft);
        
    }
}
