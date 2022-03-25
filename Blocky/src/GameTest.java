import static org.junit.Assert.*;

import java.awt.Color;
import org.junit.Test;


/**
 * @author Daniel Xu
 */

public class GameTest {
    /*
     * This method checks the game constructor and 
     * it's accessors 
     */
    @Test
    public void gameConstructor() {
        Game game = new Game(2, Color.red);
        assertNotNull(game.getRoot());
    
        game.setMaxDepth(3);
        assertEquals(3, game.getMaxDepth());
        
        Point topLeft = new Point(0,0);
        Point botRight = new Point(8,8);
        Block newRoot = new Block(topLeft, botRight, 0, null);
        
        game.setRoot(newRoot);
        assertEquals(newRoot, game.getRoot());   
    }
    
    /*
     * This method checks if the swap() method, flatten() method, 
     * and the perimeter() method all works as expected. Because of
     * the way the code is structured, it is all tested in one test because
     * it would need to be one instantiation of all the blocks. Creating a new
     * game without setting specific blocks would be randomly created grid with
     * varying blocks of sizes, colors, and depths. 
     * 
     */
    @Test
    public void swapFlattenPerimeter() {
        Game game = new Game(1, Color.red);
        
        Point topLeft = new Point(0,0);
        Point botRight = new Point(8,8);
        Block root = new Block(topLeft, botRight, 0, null);
        
        //===========children blocks===========================
        Point nwLeft = new Point(0,0);
        Point nwRight = new Point(4,4);
        Block northWest = new Block(nwLeft, nwRight, 1, root);
        northWest.setColor(Color.red);
        
        IBlock tmp1 = northWest;
        
        Point neLeft = new Point(4,0);
        Point neRight = new Point(8,4);
        Block northEast = new Block(neLeft, neRight, 1, root);
        northEast.setColor(Color.blue);
        
        IBlock tmp2 = northEast;
        
        Point seLeft = new Point(0,4);
        Point seRight = new Point(4,8);
        Block southEast = new Block(seLeft, seRight, 1, root);
        southEast.setColor(Color.white);
        
        IBlock tmp3 = southEast;
        
        Point swLeft = new Point(4,4);
        Point swRight = new Point(8,8);
        Block southWest = new Block(swLeft , swRight, 1, root);
        southWest.setColor(Color.cyan);
        
        IBlock tmp4 = southWest;
        
        //Set children
        root.setTopLeftTree(northWest);
        root.setTopRightTree(northEast);
        root.setBotRightTree(southEast);
        root.setBotLeftTree(southWest);
        //Update game root
        game.setRoot(root);
        //Swap 
        game.swap(1, 2);
        game.swap(3, 4);

        //Old tmp blocks with updated colors
        tmp1.setColor(Color.blue);
        tmp2.setColor(Color.red);
        
        tmp3.setColor(Color.cyan);
        tmp4.setColor(Color.white);

        //This checks for the blocks are the same blocks
        assertEquals(tmp2, game.getRoot().getTopLeftTree());
        assertEquals(tmp1, game.getRoot().getTopRightTree());
        assertEquals(tmp3, game.getRoot().getBotLeftTree());
        assertEquals(tmp4, game.getRoot().getBotRightTree());
        
        IBlock[][] grid = game.flatten();
        IBlock tmpNW = grid[0][0];
        IBlock tmpNE = grid[0][1];
        IBlock tmpSE = grid[1][0];
        IBlock tmpSW = grid[1][1];
        
        //This checks if the blocks are the same as the blocks stored in grid
        assertEquals(tmp2, tmpNW);
        assertEquals(tmp1, tmpNE);
        assertEquals(tmp3, tmpSW);
        assertEquals(tmp4, tmpSE);
        
        int testScore = game.perimeter_score();
        
        //This checks if the perimeter score is calculated as expected
        assertEquals(2, testScore);
    } 
}
