import java.util.ArrayList;
import java.util.Arrays;

//write in a file, to save the quadtree in a text file
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The class QuadTree represent an area quadtree, i.e. a global area of different "color"
 * each one represented by an integer value.
 * <p>
 * Here are the class' attributes :
 * <ul>
 *      <li>{@code value}    : the color value of the area (null if there are relevant sub-area)</li>
 *      <li>{@code northWest}: reference to the north-west squared sub-area</li>
 *      <li>{@code northEast}: reference to the north-east squared sub-area</li>
 *      <li>{@code southEast}: reference to the south-east squared sub-area</li>
 *      <li>{@code southWest}: reference to the south-west squared sub-area</li>
 * </ul>
 * <p>
 * Uses these following librairies :
 * {@link ArrayList}
 * {@link Arrays}
 * <p>
 * A node can either be set as full color or an area in itself if it contains more than one color. 
 * If a node is an area then we divide this area into four squared sub-area, referenced by the
 * sons of the node :
 * {@link #setNodeAsColor(int)}
 * {@link #setNodeAsArea(QuadTree, QuadTree, QuadTree, QuadTree)}
 * <p>
 * IMPORTANT :
 * <ul>
 * the area corresponding to the quadtree uses the reference frame whose :
 *      <li>the ORIGIN is in the TOP-LEFT CORNER</li>
 *      <li>the ABSCISSA axe 'x' is oriented from LEFT to RIGHT</li>
 *      <li>the ORDINATE axe 'y' is oriented from TOP to BOTTOM</li>
 * </ul>
 * <p>
 * @author Monique RIMBERT  (monique.rimbert@etu.univ-nantes.fr)
 * @author Aurel HAMON      (aurel.hamon@etu.univ-nantes.fr)
 * 
 * @version 1.1 on the 11/15/2023
 */
public class QuadTree {
    private Integer     value;
    private QuadTree    northWest, 
                        northEast, 
                        southEast, 
                        southWest;
    
    //------------------------------------------------------------------
    //------------------------------------------------------------------
    //CONSTRUCTOR-------------------------------------------------------
    /**
     * Create an empty instance of a QuadTree : all attributes are null.
     */
    public QuadTree(){
        this.value = null;

        this.northEast = null;
        this.northWest = null;
        this.southWest = null;
        this.southEast = null;
    }

    //-------------------------------------------------------------------
    /**
     * Recursively creates an instance of a QuadTree, from an area represented by
     * an integer matrix of size 2^n x 2^n.
     * @param area {@code ArrayList<ArrayList<Integer>>} represented by an integer matrix (2^n x 2^n)
     * @param begLine {@code int} the sub-area lowest line's index
     * @param begColumn {@code int} the sub-area lowest column's index
     * @param sideSize {@code int} sub-area is squared, all sides are equal in lenght 
     * 
     * @see #QuadTree(area)
     */
    private QuadTree(ArrayList<ArrayList<Integer>> area, int begLine, int begColumn, int sideSize){
        if(sideSize < 2){ 
            //cannot reduce the area anymore : it is a color
            this.value = (area.get(begLine)).get(begColumn);
            //it has no son :
            this.northEast = null; this.northWest = null; this.southEast = null; this.southWest = null;

        }else{  //the area might contain other color
            this.value     = null;

            this.northWest = new QuadTree(area, begLine, begColumn, sideSize/2);
            this.northEast = new QuadTree(area, begLine, begColumn + sideSize/2, sideSize/2);
            this.southEast = new QuadTree(area, begLine + sideSize/2, begColumn + sideSize/2, sideSize/2);
            this.southWest = new QuadTree(area, begLine + sideSize/2, begColumn, sideSize/2);

            this.fuseEqualSon(); //if the son are equal, fuse them
        }
    }

    //-------------------------------------------------------------------
    /**
     * Constructor over an area given by an integer matrix of size 2^n x 2^n.
     * <p>
     * Call the recursive constructor {@link QuadTree#QuadTree(ArrayList, Integer, Integer, Integer)}.
     * @param area represented by an (array of (array of integer : line) : column) of size 2^n x 2^n
     */
    public QuadTree(ArrayList<ArrayList<Integer>> area) {
        if(area.isEmpty()) {
            value = null; northEast = null; northWest = null; southEast = null; southWest = null;
        }else{
            this.value     = null;
            int sideSize   = area.size();

            this.northWest = new QuadTree(area, 0, 0, sideSize/2);
            this.northEast = new QuadTree(area, 0, sideSize/2, sideSize/2);
            this.southEast = new QuadTree(area, sideSize/2, sideSize/2, sideSize/2);
            this.southWest = new QuadTree(area, sideSize/2, 0, sideSize/2);

            this.fuseEqualSon(); //if the son are equal, fuse them
        }
    }

    //-------------------------------------------------------------------
    /**
     * Recursively creates an instance of a QuadTree, from an area represented by
     * an integer matrix of size 2^n x 2^n.
     * <p>
     * Also computes the number of "leaf / color" during the construction of the tree,
     * and store it inside a global variable that must be allocated outside the constructor.
     * @param area {@code ArrayList<ArrayList<Integer>>} represented by an integer matrix (2^n x 2^n)
     * @param begLine {@code int} the sub-area lowest line's index
     * @param begColumn {@code int} the sub-area lowest column's index
     * @param sideSize {@code int} sub-area is squared, all sides are equal in lenght
     * @param nodeNumber {@code int[]} the global number of node
     * 
     * @see #QuadTree(area, int[])
     */
    private QuadTree(ArrayList<ArrayList<Integer>> area, int begLine, int begColumn, int sideSize, int[] nodeNumber){
        if(sideSize < 2){ 
            //cannot reduce the area anymore : it is a leaf / color
            this.value = (area.get(begLine)).get(begColumn);
            //it has no son :
            this.northEast = null; this.northWest = null; this.southEast = null; this.southWest = null;
            //it is a node in istelf :
            nodeNumber[0] += 1;

        }else{  //the area might contain other color
            this.value     = null;
            nodeNumber[0] += 1;

            this.northWest = new QuadTree(area, begLine, begColumn, sideSize/2, nodeNumber);
            this.northEast = new QuadTree(area, begLine, begColumn + sideSize/2, sideSize/2, nodeNumber);
            this.southEast = new QuadTree(area, begLine + sideSize/2, begColumn + sideSize/2, sideSize/2, nodeNumber);
            this.southWest = new QuadTree(area, begLine + sideSize/2, begColumn, sideSize/2, nodeNumber);

            nodeNumber[0] -= 4*this.fuseEqualSon(); //if the son are equal, fuse them and decrease the number of node by 4
        }
    }

    //-------------------------------------------------------------------
    /**
     * Constructor over an area given by an integer matrix of size 2^n x 2^n.
     * <p>
     * Also computes the number of "leaf / color" during the construction of the tree,
     * and store it inside a global variable that must be allocated outside the constructor. 
     * <p>
     * Call the recursive constructor {@link QuadTree#QuadTree(ArrayList, Integer, Integer, Integer, Integer[])}.
     * @param area represented by an (array of (array of integer : line) : column) of size 2^n x 2^n
     */
    public QuadTree(ArrayList<ArrayList<Integer>> area, int[] nodeNumber) {
        nodeNumber[0] = 0; //initializing the node count
        if(area.isEmpty()) {
            value = null; northEast = null; northWest = null; southEast = null; southWest = null;
        }else{
            this.value     = null;
            int sideSize   = area.size();
            nodeNumber[0] += 1;

            this.northWest = new QuadTree(area, 0, 0, sideSize/2, nodeNumber);
            this.northEast = new QuadTree(area, 0, sideSize/2, sideSize/2, nodeNumber);
            this.southEast = new QuadTree(area, sideSize/2, sideSize/2, sideSize/2, nodeNumber);
            this.southWest = new QuadTree(area, sideSize/2, 0, sideSize/2, nodeNumber);

            nodeNumber[0] -= 4*this.fuseEqualSon(); //if the son are equal, fuse them then decrease the number of node by 4
        }
    }

    //------------------------------------------------------------------
    //------------------------------------------------------------------
    //GETTER------------------------------------------------------------
    /**
     * @return {@code Integer} The "color"/value of the current node.
     */
    public Integer  getValue()    { return this.value; }

    /**
     * @return {@code QuadTree} The reference to the squared sub-area int the north-west corner.
     */
    public QuadTree getNorthWest(){ return this.northWest; }

    /**
     * @return {@code QuadTree} The reference to the squared sub-area int the north-east corner.
     */
    public QuadTree getNorthEast(){ return this.northEast; }

    /**
     @return {@code QuadTree} The reference to the squared sub-area int the south-west corner.
     */
    public QuadTree getSouthWest(){ return this.southWest; }

    /**
     @return {@code QuadTree} The reference to the squared sub-area int the south-east corner.
     */
    public QuadTree getSouthEast(){ return this.southEast; }

    //------------------------------------------------------------------
    //------------------------------------------------------------------
    //SETTER------------------------------------------------------------
    /**
     * Set all the son to null and change the color of the node.
     * @param newValue {@code int} The new "value / color" of the node
     */
    public void setNodeAsColor(int newValue){ 
        value = newValue;
        northWest = null; northEast = null; southEast = null; southWest = null;
    }

    /**
     * Changes the color in a node that is already a color.
     * @param newValue {@code int} The new "value / color" of the node
     * @throws Exception if the node we try to change the value isn't already a color.
     */
    public void changeValue(int newValue) throws Exception{
        if  (!this.isColor()) throw new Exception("Changing the color of a node that isn't a color !");
        value = newValue;
    }

    /**
     * Set the value to null and changes the son
     * @param newNW {@code QuadTree} Reference to the north-west squared sub-area
     * @param newNE {@code QuadTree} Reference to the north-east squared sub-area
     * @param newSE {@code QuadTree} Reference to the south-east squared sub-area
     * @param newSW {@code QuadTree} Reference to the south-west squared sub-area
     */
    public void setNodeAsArea(QuadTree newNW, QuadTree newNE, QuadTree newSE, QuadTree newSW){ 
        value     = null;
        northWest = newNW;
        northEast = newNE;
        southEast = newSE;
        southWest = newSW;
    }

    /**
     * @param newNorthWest {@code QuadTree} The new sub-area in the north-west corner.
     */
    public void setNorthWest(QuadTree newNorthWest){ this.northWest = newNorthWest; }

    /**
     * @param newNorthEast {@code QuadTree} The new sub-area in the north-east corner.
     */
    public void setNorthEast(QuadTree newNorthEast){ this.northEast = newNorthEast; }

    /**
     * @param newSouthEast {@code QuadTree} The new sub-area in the south-east corner.
     */
    public void setSouthEast(QuadTree newSouthEast){ this.southEast = newSouthEast; }

    /**
     * @param newSouthWest {@code QuadTree} The new sub-area in the south-west corner.
     */
    public void setSouthWest(QuadTree newSouthWest){ this.southWest = newSouthWest; }

    //------------------------------------------------------------------
    //------------------------------------------------------------------
    //METHOD------------------------------------------------------------
    /**
     * Given a quadtree, check if it is empty or not
     * @return {@code boolean} False if the value isn't null or at least
     *         one son isn't null.
     */
    public boolean isEmpty(){
        return (
            value == null && 
            northWest == null && northEast == null &&
            southEast == null && southWest == null
        );
    }

    //------------------------------------------------------------------
    /**
     * Given a quadtree, check if the current node is a color.
     * @return {@code boolean} True if the value isn't null
     */
    public boolean isColor(){ return this.value != null; }

    //------------------------------------------------------------------
    /**
     * Given a quadtree, check if the current node is an area.
     * @return {@code boolean} True if the value is null and the sons aren't all null.
     */
    public boolean isArea(){ 
        return ( 
            !isColor() /* the node isn't a color */
            //we must check if the tree isn't empty
            && (northWest != null || northEast != null || southEast != null || southWest != null)
        );
    }

    //------------------------------------------------------------------
    /**
     * Given a quadtree, check if the current node is a twig i.e, all his 
     * son are color
     * @return {@code boolean} True if the node value is null and the son's value aren't null
     */
    public boolean isTwig(){
        return (
            !isColor() /* the node isn't a color */
            //however the son must all be color
            && northWest.isColor() && northEast.isColor() && southEast.isColor() && southWest.isColor()
        );
    }

    //------------------------------------------------------------------
    /**
     * Given a node that is an area, check if the son are all equal.
     * @return {@code boolean} true if all sub-area of a node have the same "value / color"
     *         and if they aren't area themselves.
     * 
     * @see #fuseEqualSon()
     */
    public boolean areAllSonEqual(){
        return (
            /* the son must be color themselves : we need to check if their values
             * aren't set to null in order to compare the values */
                northWest.isColor() && northEast.isColor()
            &&  northWest.value.equals(northEast.value)

            &&  southEast.isColor()
            &&  northEast.value.equals(southEast.value)

            &&  southWest.isColor()
            &&  southEast.value.equals(southWest.value)
        );
    }

    //------------------------------------------------------------------
    /**
     * If the son are equal, fuse them into the node itself and set their reference to null
     * @return {@code int} 1 if the son were fused, else 0
     */
    public int fuseEqualSon(){
        if(this.areAllSonEqual()){
            this.setNodeAsColor(northWest.value);
            return 1;
        }
        return 0;
    }

    //------------------------------------------------------------------
    /**
     * @return {@code int} The number of "leaf / color" (i.e. node without sons) of a quadtree.
     */
    public int leafNumber(){
        if(this.isColor()) return 1; //the tree is a leaf
        if(this.isEmpty()) return 0; //the tree is empty
        
        return(
            northWest.leafNumber() + northEast.leafNumber() +
            southEast.leafNumber() + southWest.leafNumber()
        );
    }

    //------------------------------------------------------------------
    /**
     * @return {@code int} The number of node in the quadtree
     */
    public int nodeNumber(){
        if(this.isColor()) return 1; //the tree is a node
        if(this.isEmpty()) return 0; //the tree is empty
        
        return(
            northWest.nodeNumber() + northEast.nodeNumber() +
            southEast.nodeNumber() + southWest.nodeNumber() + 1 //the node itself
        );
    }

    //
    //
    //
    //-----------------------------------------------------------------
    /**
     * Recursively convert a quadtree into an area.
     * <p>
     * The area must be allocated outside the procedure.
     * @param superArea {@code ArrayList<ArrayList<Integer>>} an integer matrix (2^n x 2^n)
     * @param begLine {@code int} the begining abscissa  of the area
     * @param begColumn {@code int} the begining ordinate of the area
     * @param sideSize {@code int} Size of the area (squared of 2^n x 2^n)
     *
     * @see #toArea(int)
     */
    private void toArea(ArrayList<ArrayList<Integer>> superArea, int begLine, int begColumn, int sideSize){
        //if the node contains only one color :
        if(this.isColor()){
            //filling the sub area with the corresponding value 
            for(int line = begLine; line < begLine + sideSize; line++){
                for(int column = begColumn; column < begColumn + sideSize; column++){
                    superArea.get(line).set(column, this.value);
                }
            }

        }else{ //the node contains more than one color, we must divide the superArea
            this.northWest.toArea(superArea, begLine, begColumn, sideSize/2);
            this.northEast.toArea(superArea, begLine, begColumn + sideSize/2, sideSize/2);
            this.southEast.toArea(superArea, begLine + sideSize/2, begColumn + sideSize/2, sideSize/2);
            this.southWest.toArea(superArea, begLine + sideSize/2, begColumn, sideSize/2);
        }
    }

    //-----------------------------------------------------------------
    /**
     * Convert a quadtree into a squared area given by the size of its side.
     * <p>
     * Call the recursive procedure {@link QuadTree#toArea(ArrayList, Integer, Integer, Integer)}
     * that convert recursively convert a non empty quadtree into an area.
     * <p>
     * @param sideSize {@code int} the side's size of the square area
     * @return {@code ArrayList<ArrayList<Integer>>} The area matching the QuadTree
     */
    public ArrayList<ArrayList<Integer>> toArea(int sideSize){
        //if the tree is empty, return an empty area
        if(this.isEmpty()) return new ArrayList<ArrayList<Integer>>();

        /* Otherwise allocate the area to fill, and set the size to nbElem+1
         * so the ArrayList won't be reallocated */
        ArrayList<ArrayList<Integer>>     area = new ArrayList<ArrayList<Integer>>(sideSize+1);
        for(int i = 0; i < sideSize; i++){
            area.add(new ArrayList<Integer>(sideSize+1));
            ArrayList<Integer> line = area.get(i);
            for(int j = 0; j < sideSize; j++) line.add(null);
        }

        this.toArea(area, 0, 0, sideSize);

        return area;
    } 

    //
    //
    //
    //
    
    /**
    * Computes the logarithmic mean of the luminosity of the children of a node.
    * 
    * @return {@code double} the logarithmic mean of the luminosity
    */
   protected double getLogarithmicMean() {
       /* Usually our code stops at the first condition since we are the programmers
        * We will tend to use only that function to compute the luminosity logarithmic
        * mean of a twig for the compression methods */
       if (this.isTwig()) {
           double mean = Math.log(0.1 + this.northWest.value);
           mean += Math.log(0.1 + this.northEast.value);
           mean += Math.log(0.1 + this.southEast.value);
           mean += Math.log(0.1 + this.southWest.value);

           mean /= 4.0;

           return Math.exp(mean);

       } 
       else if (this.isColor()){
           return this.getValue();

       } 
       /* We compute recursively the logarithmic mean */
       else {
           double mean = Math.log(0.1 + this.northWest.getLogarithmicMean());
           mean += Math.log(0.1 + this.northEast.getLogarithmicMean());
           mean += Math.log(0.1 + this.southEast.getLogarithmicMean());
           mean += Math.log(0.1 + this.southWest.getLogarithmicMean());

           mean /= 4.0;

           return Math.exp(mean);
       }
   }

    //
    //
    //
    //

    /**
     * Compress recursively a {@code QuadTree} by using a logarithmic mean of luminosity.
     * <p>
     * This method sets the root value to the logarithmic mean of its children 
     * luminosity and removes them. In the end, we expected only one layer at the
     * bottom of the {@code QuadTree} is removed.
     * 
     * @return {@code int} The variation in number of nodes from the initial tree and the resulting tree.
     * 
     * @see #getLogarithmicMean()
     */
    protected int compressLambda() {
        if (this.isArea()) {
            if (this.isTwig()) {
                this.setNodeAsColor((int) Math.round(this.getLogarithmicMean()));
                return -4;

            } else {
                int varNodeNumber = northWest.compressLambda();
                varNodeNumber += northEast.compressLambda();
                varNodeNumber += southEast.compressLambda();
                varNodeNumber += southWest.compressLambda();

                varNodeNumber -= 4 * this.fuseEqualSon();

                return varNodeNumber;
            }
        } else {
            return 0;
        }
    }

    //
    //
    //
    //

    /**
     * Computes the epsilon value of a {@code QuadTree}.
     * <p>
     * A node can either be a internal or external. A internal node has 4 children nodes, thus
     * following the preconditions of our specified {@code QuadTree}, we are able to compute
     * its epsilon value. An external value is a leaf so it doesn't have children. However, an area 
     * can be implicitely visualized as an internal linked to 4 children sharing the same color.
     * 
     * @return {@code double} being equal to {@code 0} if it's an external node, otherwise, the greater difference
     * between the logarithmic mean of its children and their luminosity
     * 
     * @see #getLogarithmicMean
     */
    protected double getTreeEpsilon() {
       /* Usually our code stops at the first condition since we are the programmers
        * We will tend to use only that function to compute the luminosity logarithmic
        * mean of a twig for the compression methods */
        if (this.isTwig()) {
            double lambda = this.getLogarithmicMean();
            return  Math.max(Math.abs(lambda - (double)this.getNorthWest().getValue()),
                    Math.max(Math.abs(lambda - (double)this.getNorthEast().getValue()),
                    Math.max(Math.abs(lambda - (double)this.getSouthEast().getValue()), 
                             Math.abs(lambda - (double)this.getSouthWest().getValue()))));
        } 
        
        else if (this.isColor()) {
            return 0;
        }
        
        else {
            double lambdaNW = northWest.getLogarithmicMean();
            double lambdaNE = northEast.getLogarithmicMean();
            double lambdaSE = southEast.getLogarithmicMean();
            double lambdaSW = southWest.getLogarithmicMean();
            double lambda = this.getLogarithmicMean();
            
            return  Math.max(Math.abs(lambda - lambdaNW),
                    Math.max(Math.abs(lambda - lambdaNE),
                    Math.max(Math.abs(lambda - lambdaSE), 
                             Math.abs(lambda - lambdaSW))));
        } 
            
    }

    //
    //
    //
    //

    /**
     * Compresses the {@code QuadTree} according to a factor rho and the initial number of nodes in the tree.
     * <p>
     * In the end, we verify if have reached the factor required. If not, we need
     * to merge the final and remaining children.
     * 
     * @param rho {@code int} between 0 and 100, it represents a factor of remaining leaves in the QuadTree
     * @param nbNodesInit {@code int} Le nombre initial de noeuds
     * 
     * @return The variation of number of nodes from the initial tree to the resulting tree
     * 
     * @see PGM#compressRho(int) 
     */
    public int compressRho(int rho, int nbNodesInit) {
        if (rho == 100) return 0;

        int nbNodesCurrent = nbNodesInit;
        TwigList    twigList        = new TwigList(this);

        while (!twigList.isEmpty() && nbNodesCurrent > ((double)(nbNodesInit * rho) / 100.0)) {
            /* We get the first twig to be compressed */
            QuadTree twig = twigList.getNodeToBeCompressed();

            /* We compress the twig */
            nbNodesCurrent += twig.compressLambda();

            /* We check its ancestors et maybe we will add one of them */
            nbNodesCurrent += twigList.update();
        }

        return (nbNodesCurrent - nbNodesInit);      //We return the variation of the number of nodes
    }

    //
    //
    //
    //-----------------------------------------------------------------
    /**
     * Recursively convert a quadtree into a string where each node is written like this :
     * <ul>     
     *      <li>if it is an area : (northWest)(northEast)(southEast)(southWest)</li>
     *      <li>if it is a color : (value)</li>
     * </ul>
     * @return the string containing all the information on the quadtree
     */
    @Override
    public String toString(){
        if      (this.isEmpty()) {return "()";                   }
        else if (this.isColor()) {return value.toString();}
        else /*  this.isArea() */{
            return (
                 "("+this.northWest.toString()+" "
                + this.northEast.toString()+" "
                + this.southEast.toString()+" "
                +this.southWest.toString()+")"
            );
        }                    
    }

    //
    //
    //
    //-----------------------------------------------------------------
    /**
     * Write the quadtree as a string into a text file whose path is given as a parameter.
     * @param pathFileName {@code String} the path to the file in which we will write the quadtree
     */
    public void saveToFile(String pathFileName){
        String newPathFileName = pathFileName.substring(0, pathFileName.length() - 4) 
                                + "_treeSaved_.txt";

        //modifies filename if it exists
        Integer index = 0;
        if(Files.exists(Paths.get(newPathFileName))){
            newPathFileName = newPathFileName.substring(0, newPathFileName.length() - 4) 
                            + "("+index.toString()+").txt";
            index++;
        }

        while( Files.exists(Paths.get(newPathFileName)) ){
            newPathFileName = newPathFileName.substring(0, newPathFileName.length() - 7) 
                            + "("+index.toString()+").txt";
            index++;
        }

        //write in a file
        try{
            BufferedWriter bufferWrite = Files.newBufferedWriter(Paths.get(newPathFileName));
            String treeString = this.toString();

            bufferWrite.write(treeString, 0, treeString.length());
            bufferWrite.flush();
            bufferWrite.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //
    //
    //
    //-----------------------------------------------------------------
    /**
     * The function recursively, horizontaly and symmetrically displays the nodes of the quadtree, with each node indented based on
     * its level in the tree.
     * @param tabIncrementCpt {@code int} represents the number of tab increments for each level of the tree.
     */
    private void displayChildNode(int tabIncrementCpt) {
        if(northWest != null) northWest.displayChildNode(tabIncrementCpt + 1);
        if(northEast != null) northEast.displayChildNode(tabIncrementCpt + 1);
        
        for(int t = 0; t < tabIncrementCpt; t++) System.out.print("\t");
        System.out.println("{"+value+"}");

        if(southEast != null) southEast.displayChildNode(tabIncrementCpt + 1);
        if(southWest != null){ 
            southWest.displayChildNode(tabIncrementCpt + 1);
            if(southWest.isColor()) System.out.println("---");
        }
    }

    /**
     * The display() function calls the recursive method {@link QuadTree#displayChildNode(int)} with an argument of 0.
     * It display the quadtree as a tree, however it doesn't show the link from a father node to its child.
     */
    public void display() {
        this.displayChildNode(0);
    }
}
