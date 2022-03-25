import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.*;

/**
 * @author Daniel Xu
 */

public class Game implements IGame {

    private int max_depth;
    private IBlock root;
    private Color target;
    private IBlock[][] gameGrid;


    public static final Color[] COLORS = 
        { Color.BLUE, Color.RED, Color.WHITE, 
          Color.YELLOW, Color.CYAN, Color.GRAY, 
          Color.GREEN, Color.PINK };

    /*
     * This method constructs the game with max_depth and color that will be used
     * to count the points
     * 
     * @param max_depth
     * @param target
     */
    Game(int maxDepth, Color target) {
        this.max_depth = maxDepth;
        this.target = target;
        this.root = random_init();
    }

    /*
     * This sets the max depth of the game
     */
    @Override
    public int max_depth() {
        return this.max_depth;
    }

    /*
     * This method will initiate a game as described in the handout. This
     * will exit once one of the blocks are at the max_depth as decided
     * by the user. This method calls the smash() method to break the boxes
     * into greater depths. This returns the root block or the parent root
     * of the game tree 
     * 
     * @param return 
     */
    @Override
    public IBlock random_init() { 
        //Set corners
        Point topLeft = new Point(0,0);
        Point botRight = new Point(8,8);
        //Init Random
        Random rand = new Random();

        //Create block
        Block root = new Block(topLeft, botRight, 0, null);
        this.root = root;
        root.smash(this.max_depth);

        //Block
        int randPickBlock;
        //Upperbound for picking random int
        int upperBound = 5;
        int checkDepth = 1;

        while (checkDepth < this.max_depth) {
            //randPickBlock = rand.nextInt(upperBound) + 1;
            randPickBlock = rand.nextInt(upperBound);

            if (randPickBlock == 0) {
                continue;
            }

            IBlock tmp = this.getBlock(randPickBlock);

            //Smash and update depth for exit condition
            if (tmp.depth() < max_depth && tmp.isleaf()) {
                tmp.smash(this.max_depth);

                upperBound += 4;
                checkDepth = tmp.depth() + 1;
            } 
        }
        return this.root;
    }


    /*
     * This method will get a block as a certain location determined
     * during the BFS traversal. This method will then return the block
     * at the position chosen by the user
     * 
     * @param pos 
     * @return IBlock
     */
    @Override
    public IBlock getBlock(int pos) {
        //Deque 10-12 lines
        List<IBlock> container = new ArrayList<>();
        ArrayDeque<IBlock> newQ = new ArrayDeque<>();

        if (this.getRoot() != null) {
            newQ.offer(this.getRoot());
        }

        //Adds until is empty
        while (!newQ.isEmpty()) {
            IBlock root = newQ.poll();
            if (root != null) {
                container.add(root);
                if (root.getTopLeftTree() != null) {
                    //NW
                    newQ.offer(root.getTopLeftTree());
                }
                if (root.getTopRightTree() != null) {
                    //NE
                    newQ.offer(root.getTopRightTree());
                }
                if (root.getBotRightTree() != null) {
                    //SE
                    newQ.offer(root.getBotRightTree());
                }
                if (root.getBotLeftTree() != null) {
                    //SW
                    newQ.offer(root.getBotLeftTree());
                }
            }
        }
        //int index = pos;
        int index = pos;
        return container.get(index);
    }

    /*
     * This method gets the root of the current game
     * or the parent node 
     */
    @Override
    public IBlock getRoot() {
        return this.root;
    }

    /*
     * This method will swap two blocks are locations of equal depth. 
     * This first updates the topLeft/botRight coordinates of each the two blocks. 
     * Then it calls updatePos() to update the parent nodes of the two
     * blocks being swapped. Then The method calls updateXY() method within
     * the Block class to recursively update the topLeft/botRight of the children
     * 
     * @param x
     * @param y 
     */
    @Override
    public void swap(int x, int y) {
        Block blockOne = ((Block) this.getBlock(x));
        Block blockTwo = ((Block) this.getBlock(y));

        if (blockOne == null && blockTwo == null) {
            return;
        }

        if (blockOne.depth() == (blockTwo).depth()) {      
            Point oneTL = blockOne.getTopLeft();
            Point oneBR = blockOne.getBotRight();
            Point twoTL = blockTwo.getTopLeft();
            Point twoBR = blockTwo.getBotRight();
            Block tmp = blockOne;

            //Set the new parents
            Block parentNode1 = ((Block)blockOne.getParent());
            Block parentNode2 = ((Block)blockTwo.getParent());
            blockOne.setParent(parentNode2);
            blockTwo.setParent(parentNode1);

            //Swap the pointers using helper function
            updatePos(x, parentNode1, blockTwo);
            updatePos(y, parentNode2, blockOne);

            //Swap the actual blocks
            blockOne = blockTwo;
            blockTwo = tmp; 

            //Update topLeft/botRight
            blockOne.setTopLeft(oneTL);
            blockOne.setBotRight(oneBR);
            blockTwo.setTopLeft(twoTL);
            blockTwo.setBotRight(twoBR);

            //Call recursive method 
            blockOne.updateXY(blockOne, blockOne.getTopLeft(), blockOne.getBotRight());
            blockTwo.updateXY(blockTwo, blockTwo.getTopLeft(), blockTwo.getBotRight());
        }
    }

    /*
     * This is the helper function used in swap() to update the parent nodes of
     * the two blocks that are being swapped. This will take in the parent node to swap to,
     * the block that needs to have be updated, and the position.
     * 
     * @param position
     * @param parent
     * @param child
     */
    private void updatePos(int position, IBlock parent, Block child) {
        switch (position % 4) {
            default:
                break;
            case 0: 
            //SW block
                parent.setBotLeftTree(child);
                break;
            case 1: 
            //NW block
                parent.setTopLeftTree(child);
                break;
            case 2: 
            //NE block
                parent.setTopRightTree(child);
                break;
            case 3:
            //SE block
                parent.setBotRightTree(child);
                break;

        }
    }

    /*
     * This method essentially makes a 2d grid that is dependent on the max_depth as
     * the lengths (horizontal/vertical) are determined by 2^{max_depth}.
     * Then to fit the actual grid, we must divide 8 (of (8,8) size grid) 
     * by the 2^{max_depth} to appropriately fit the coordinates of each block. 
     * 
     * i.e. if the max depth is 2 then 2^2 = 4. The actual block coordinates 
     * (from 0,0 to 8,8) need to be divided by 4 or (8/4) to fit the matrix of
     * a 4x4 grid. 
     * 
     * This method also does a BFS traversal to add each respective children node in
     * BFS order onto the grid, using a queue to store each following level of children nodes. 
     * 
     * Then, it finally returns a IBlock[][] 2d array with blocks stored at each cell of the 
     * 2d array. 
     * 
     * @return grid 
     */
    @Override
    public IBlock[][] flatten() {
        int gridSize = (int) Math.pow(2, this.max_depth);
        //Match 8x8 grid and change size depending on the gridSize
        int unitS = 8 / gridSize;
 

        IBlock[][] grid = new IBlock[gridSize][gridSize];
//        IBlock curBlock;

        ArrayDeque<IBlock> newQ = new ArrayDeque<IBlock>();
        if (this.getRoot() != null) {
            newQ.offer(this.getRoot());
        }

        while (!newQ.isEmpty()) {
            IBlock curBlock = newQ.poll();

            //Put into grid
            if (curBlock.isleaf()) {
                for (int i = curBlock.getTopLeft().getY(); i < curBlock.getBotRight().getY(); i++) {
                    for (int j = curBlock.getTopLeft().getX(); 
                            j < curBlock.getBotRight().getX(); j++) {
                        grid[i / unitS][j / unitS] = curBlock;
                    }
                }

            }
            newQ.addAll(curBlock.children());
        }
        this.setGrid(grid);  
        return grid;
    }

    /*
     * This method will score the perimeter of 2d grid returned by
     * flatten() method. This essentially scores all 4 exterior lengths of the
     * grid in one forloop and returns the points of the current game grid. 
     * 
     * The point is determined with a block of a certain color exists at the 
     * exterior of the 2d array returned from flatten().
     * 
     * @param gamePoints
     */
    @Override
    public int perimeter_score() {
        Color gameColor = this.target;
        IBlock[][] grid = this.flatten();
        int gamePoints = 0;

        //Calculate gamePoints
        for (int i = 0; i < this.getGrid().length; i++) {
            //Top Horizontal
            if (grid[0][i].getColor() == gameColor) {
                gamePoints++;
            }
            //Bot Horizontal
            if (grid[grid.length - 1][i].getColor() == gameColor) {
                gamePoints++;
            }
            //Left Vertical
            if (grid[i][0].getColor() == gameColor) {
                gamePoints++;
            }
            //Right Horizontal
            if (grid[i][grid.length - 1].getColor() == gameColor) {
                gamePoints++;
            }
        }
        return gamePoints;
    }

    /*
     * This method sets the root or parent node of the game
     * 
     * @param root 
     */
    @Override
    public void setRoot(IBlock root) {
        this.root = root;
    }


    /*
     * This method sets the maxDepth of the game
     * 
     * @param depth
     */
    public void setMaxDepth(int depth) {
        this.max_depth = depth;
    }
    
    /*
     * This method returns the maxDepth of the game
     * 
     * @return depth
     */
    public int getMaxDepth() {
        return this.max_depth;
    }

    /*
     * This method sets the Grid of the game 
     * 
     * @param grid
     */
    public void setGrid(IBlock[][] grid) {
        this.gameGrid = grid;
    }

    /*
     * This method returns the most recent
     * 2d grid of the current game
     * 
     * @return gameGride
     */
    public IBlock[][] getGrid() {
        return this.gameGrid;
    }
    
}