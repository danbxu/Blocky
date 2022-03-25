import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author Daniel Xu
 */

public class Block implements IBlock {
    

    //Constructor variables 
    private Point topLeft;
    private Point botRight;
    private int depth;
    private Color color;

    private IBlock topLeftTree = null;
    private IBlock topRightTree = null;
    private IBlock botRightTree = null;
    private IBlock botLeftTree = null;
    private IBlock parentRoot;



    public static final Color[] COLORS = 
        { Color.BLUE, Color.RED, Color.WHITE, 
          Color.YELLOW, Color.CYAN, Color.GRAY, 
          Color.GREEN, Color.PINK };

    /*
     * This method is the constructor for creating a block
     * 
     * @param topLeft
     * @param botRight
     * @param depth
     * @param parent
     */
    Block(Point topLeft, Point botRight, int depth, IBlock parent) { 
        this.topLeft = topLeft;
        this.botRight = botRight; 
        this.depth = depth; 
        this.parentRoot = parent;
        this.color = null;
    }

    /*
     * This method returns the depth of the current block
     * 
     * @return depth
     */
    @Override
    public int depth() { 
        return this.depth;
    }

    /*
     * This method divides a block into 4 subblocks and updates the topLeft, botRight of each
     * block by first using the center (midpoint) of topLeft/botRight then using center, topLeft,
     * botRight, as keep points of references when creating child nodes in NW, NE, SE, SW.
     * This also uses a random class to set the color chosen from COLORS array. 
     * 
     * This method takes a maxDepth and smashes the current block only if the current block's 
     * depth is lower than the maxDepth and is also a left. 
     * A block that is already a parent node of 4
     * subblocks can't be smashed again. 
     * 
     * After creating the subblocks, these are set to the current nodes
     *NW, NE, SE, and SW children nodes
     */
    @Override
    public void smash(int maxDepth) {
        Point curTopLeft = this.getTopLeft();
        Point curBotRight = this.getBotRight();

        if (this.depth < maxDepth && this.isleaf()) {
            //Colors
            Random rand = new Random();
            int pick1 = rand.nextInt(COLORS.length);
            int pick2 = rand.nextInt(COLORS.length);
            int pick3 = rand.nextInt(COLORS.length);
            int pick4 = rand.nextInt(COLORS.length);


            //remove color===================================
            this.setColor(null); 

            //set up the sub-blocks
            //Get center(X,Y) coordinates first and use as reference
            int centerX = (curTopLeft.getX() + curBotRight.getX()) / 2;
            int centerY = (curTopLeft.getY() + curBotRight.getY()) / 2;
            Point center = new Point(centerX, centerY);

            //mini-Block TopLeft [DONE] ==============================================
            IBlock topLeft = new Block(curTopLeft, center, this.depth() + 1 , this);
            topLeft.setColor(COLORS[pick1]);

            //mini-Block BotRight [DONE]==============================================
            IBlock botRight = new Block(center, curBotRight, this.depth() + 1, this);
            botRight.setColor(COLORS[pick2]);


            //CenterX can be used for x coordinate for the NE TOPLEFT
            //Original topLeft y can be used as the NE Y coordinate 
            //Updated NE
            int upMidX = center.getX();
            int upMidY = curTopLeft.getY();

            int rightMidX = curBotRight.getX();
            int rightMidY = center.getY();

            Point upMid = new Point(upMidX, upMidY); 
            Point rightMid = new Point(rightMidX, rightMidY);

            IBlock topRight = new Block(upMid, rightMid, this.depth() + 1, this);
            topRight.setColor(COLORS[pick3]);

            //Updated SW
            int leftMidX = curTopLeft.getX();
            int leftMidY = center.getY();

            int botMidX = center.getX();
            int botMidY = curBotRight.getY();

            Point leftMid = new Point(leftMidX, leftMidY); 
            Point botMid = new Point(botMidX, botMidY);

            IBlock botLeft = new Block(leftMid, botMid, this.depth() + 1, this);
            botLeft.setColor(COLORS[pick4]);

            //New children nodes 
            this.setTopLeftTree(topLeft);
            this.setTopRightTree(topRight);
            this.setBotRightTree(botRight);
            this.setBotLeftTree(botLeft);

        }
    }

    /*
     * This method returns blockList, which is a list of the current block's children nodes
     * This list will always either be size 4 or zero, depending if the current block is 
     * a leaf node or not. 
     * 
     * @return blockList
     */
    @Override
    public List<IBlock> children() {
        List<IBlock> blockList = new ArrayList<>();

        if (this.getTopLeftTree() != null) {
            blockList.add(this.getTopLeftTree());
        }
        if (this.getTopRightTree() != null) {
            blockList.add(this.getTopRightTree());
        }
        if (this.getBotRightTree() != null) {
            blockList.add(this.getBotRightTree());
        }
        if (this.getBotLeftTree() != null) {
            blockList.add(this.getBotLeftTree());
        }
        return blockList;
    }

    /*
     * This method will essential rotate the current block's subblocks by 1 step
     * (clockwise), which means all the topLeft/botRight points need to be shifted and 
     * updated, and the location of each subblock needs to be updated as well, by reassigning
     * each of those values. Once the blocks have been rotated, 
     * the subblocks also need to be shifted,
     * meaning that the topLeft, botRight needs to be reset or 
     * updated to match the current location of the parent
     * blocks.
     * 
     * The updating of the subblocks' topLeft/botRight are done recursively by calling
     * updateXY() method, which takes in a subblock, topLeft, and botRight. 
     * This will then be all updated according.
     */
    @Override
    public void rotate() {
        if (!this.isleaf()) { 
            //NW 
            Point nwTL = this.topLeftTree.getTopLeft();
            Point nwBR = this.topLeftTree.getBotRight();
            //NE
            Point neTL = this.topRightTree.getTopLeft();
            Point neBR = this.topRightTree.getBotRight();
            //SE
            Point seTL = this.botRightTree.getTopLeft();
            Point seBR = this.botRightTree.getBotRight();
            //SW
            Point swTL = this.botLeftTree.getTopLeft();
            Point swBR = this.botLeftTree.getBotRight();

            //Rotations
            IBlock tmp = this.getBotLeftTree();
            this.setBotLeftTree(this.getBotRightTree());
            this.setBotRightTree(this.getTopRightTree());
            this.setTopRightTree(this.getTopLeftTree());
            this.setTopLeftTree(tmp);


            //Update topLeft and botRight
            this.getBotLeftTree().setTopLeft(swTL);
            this.getBotLeftTree().setBotRight(swBR);

            this.getBotRightTree().setTopLeft(seTL);
            this.getBotRightTree().setBotRight(seBR);

            this.getTopRightTree().setTopLeft(neTL);
            this.getTopRightTree().setBotRight(neBR);

            this.getTopLeftTree().setTopLeft(nwTL);
            this.getTopLeftTree().setBotRight(nwBR);

            //Recursive calls
            updateXY(this.getTopLeftTree(), 
                    this.getTopLeftTree().getTopLeft(), 
                    this.getTopLeftTree().getBotRight());
            updateXY(this.getTopRightTree(), 
                    this.getTopRightTree().getTopLeft(), 
                    this.getTopRightTree().getBotRight());
            updateXY(this.getBotLeftTree(), 
                    this.getBotLeftTree().getTopLeft(), 
                    this.getBotLeftTree().getBotRight());
            updateXY(this.getBotRightTree(), 
                    this.getBotRightTree().getTopLeft(), 
                    this.getBotRightTree().getBotRight());
        }
    }


    /*
     * This method is called by rotate() method to shift and update the subblock's 
     * coordinates of topLeft,botRight to match its new position in the tree. Similar to
     * smash method, this creates center (midpoint of topLeft/botRight) and uses the center
     * as the key point of reference when updating blocks in NW, NE, SE, SW position. Once updated,
     * this current block will call updateXY recursively until there are 
     * no subblocks to update anymore. 
     */
    public void updateXY(IBlock block, Point topLeft, Point botRight) {
        if (!block.isleaf()) {
            int centerX = (topLeft.getX() + botRight.getX()) / 2;
            int centerY = (topLeft.getY() + botRight.getY()) / 2;

            Point center = new Point(centerX, centerY);
            //NW===================================== 
            block.getTopLeftTree().setTopLeft(topLeft);
            block.getTopLeftTree().setBotRight(center);
            updateXY(block.getTopLeftTree(), topLeft, center);

            //NE=====================================

            int upMidX = center.getX();
            int upMidY = topLeft.getY();

            int rightMidX = botRight.getX();
            int rightMidY = center.getY();

            Point upMid = new Point(upMidX, upMidY); 
            Point rightMid = new Point(rightMidX, rightMidY);

            block.getTopRightTree().setTopLeft(upMid);
            block.getTopRightTree().setBotRight(rightMid);
            updateXY(block.getTopRightTree(), upMid, rightMid);

            //SE=======================================
            block.getBotRightTree().setTopLeft(center);
            block.getBotRightTree().setBotRight(botRight);
            updateXY(block.getBotRightTree(), center, botRight);

            //SW=======================================
            int leftMidX = topLeft.getX();
            int leftMidY = center.getY();

            int botMidX = center.getX();
            int botMidY = botRight.getY();

            Point leftMid = new Point(leftMidX, leftMidY); 
            Point botMid = new Point(botMidX, botMidY);

            block.getBotLeftTree().setTopLeft(leftMid);
            block.getBotLeftTree().setBotRight(botMid);
            updateXY(block.getBotLeftTree(), leftMid, botMid);

        }
    }
    
    /*
     * This method is overridden because this is used to test if two blocks
     * are the same, depending if it is a instanceof a block, the depth, and it's 
     * points(x,y) in topLeft, botRight, and the colors of the blocks
     * 
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof IBlock) {
            IBlock o1 = ((IBlock) o);
            return this.depth() == o1.depth() &&
                    this.getTopLeft() == o1.getTopLeft() &&
                    this.getBotRight() == o1.getBotRight() &&
                    this.getColor() == o1.getColor();
        }
        return false;

    }

    /*
     * This sets the parentNode of the current block
     * 
     * @param parent
     */
    public void setParent(IBlock parent) {
        this.parentRoot = parent;
    }

    /*
     * This gets the parentNode of the current block
     * 
     * @return parentRoot
     */
    public IBlock getParent() {
        return this.parentRoot;
    }

    /*
     * This gets the color of the current block
     * 
     * @return color
     */
    @Override
    public Color getColor() {
        return this.color;
    }

    /*
     * This sets the color of the current block
     * 
     * @param c
     */
    @Override
    public void setColor(Color c) {
        this.color = c;
    }

    /*
     * This sets the topLeft of the current block
     * 
     * @param topLeft
     */
    @Override
    public void setTopLeft(Point topLeft) {
        this.topLeft = topLeft;
    }
   
    /*
     * This gets the topLeft of the current block
     * 
     * @return topLeft
     */
    @Override
    public Point getTopLeft() {
        return this.topLeft;
    }

    /*
     * This sets the botRight of the current block
     * 
     * @param botRight
     * 
     */
    @Override
    public void setBotRight(Point botRight) {
        this.botRight = botRight;
    }

    /*
     * This gets the botRight of the current block
     * 
     * @return botRight
     */
    @Override
    public Point getBotRight() {
        return this.botRight;
    }

    /*
     * This checks if the current block is a leaf
     * 
     * @return boolean
     */
    @Override
    public boolean isleaf() {
        return this.children().size() == 0;
    }

    //==============================Getters
    /*
     * This returns topLeftTree of the current block
     * 
     * @return topLeftTree
     */
    @Override
    public IBlock getTopLeftTree() {
        return this.topLeftTree;
    }
    
    /*
     * This returns topRightTree of the current block
     * 
     * @return topRightTree
     */
    @Override
    public IBlock getTopRightTree() {
        return this.topRightTree;
    }

    /*
     * This returns botLeftTree of the current block
     * 
     * @return botLeftTree
     */
    @Override
    public IBlock getBotLeftTree() {
        return this.botLeftTree;
    }

    /*
     * This returns botRightTree of the current block
     * 
     * @return botRightTree
     */
    @Override
    public IBlock getBotRightTree() {
        return this.botRightTree;
    }

    //==============================Setters
    /*
     * This gets topLeftTree of the current block
     * 
     * @param topleftTree
     */
    
    @Override
    public void setTopLeftTree(IBlock block) {
        this.topLeftTree = block;
    }

    /*
     * This gets topRightTree of the current block
     * 
     * @return topRightTree
     */
    
    @Override
    public void setTopRightTree(IBlock block) {
        this.topRightTree = block;
    }

    /*
     * This gets botLeftTree of the current block
     * 
     * @return botLefttTree
     */
    @Override
    public void setBotLeftTree(IBlock block) {
        this.botLeftTree = block;
    }

    /*
     * This gets botRightTree of the current block
     * 
     * @return topRightTree
     */
    @Override
    public void setBotRightTree(IBlock block) {
        this.botRightTree = block;

    }
}
