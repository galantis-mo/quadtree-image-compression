
/**
 * This class TwigList is used for {@link PGM#compressRho(int)}.
 * <p>
 * Not only it can maps internally a {@code QuadTree} by pairing a node with its father.
 * But it stocks the list of twigs that can be compressed by sorting the pairs {@code ChildOf}
 * in descending order. Resulting in the first twig having the smallest epsilon value.
 * <p>
 * 
 * @author Monique RIMBERT  (monique.rimbert@etu.univ-nantes.fr)
 * @author Aurel HAMON      (aurel.hamon@etu.univ-nantes.fr)
 * 
 * @version 2.0 on the 12/04/2023
 * 
 * @see PGM#compressRho(int)
 * @see QuadTree#compressRho(int, int)
 */
public class TwigList{
    /**
     * This internal private class pairs a father and a child node together.
     * It is only used for {@link PGM#compressRho(int)} thus its only appearance in {@link TwigList}
     */
    private class ChildOf {
        protected QuadTree node;        //The twig
        protected ChildOf nodeFather;   //The parent of that twig

        /**
         * Creates an instance of ChildOf
         * 
         * @param node_
         * @param nodeFather_
         */
        public ChildOf(QuadTree node_, ChildOf nodeFather_){
            this.node    = node_;
            this.nodeFather = nodeFather_;
        }
    }

    //
    //
    //
    //

    /* ATTRIBUTES */
    private AVL<ChildOf> twigList;

    //
    //
    //
    //

    /* METHODS */
    /**
     * Creates a new instance of TwigList and fills the list of potential twigs
     * 
     * @param treeArea {@code QuadTree} that is the root of the tree.
     */
    public TwigList(QuadTree treeArea){
        twigList = null;
        this.fillAllTwigs(null, treeArea);
    }

    //
    //
    //
    //

    /* GETTERS */
    /**
     * @return {@code QuadTree} The twig that needs to be compressed
     */
    public QuadTree getNodeToBeCompressed() {
        if (twigList == null) {
            return null;
        } else {
            return twigList.min().getDataCollection().get(0).node;
        }
    }

    /**
     * @return {@code true} if there's no more twig to compress
     */
    public boolean isEmpty() {
        return twigList == null || twigList.isEmpty();
    }

    //
    //
    //
    //
    //

    /**
     * Fills the table of pairs of father and children of nodes that may be compressed
     * 
     * @param parent {@code ChildOf} the pair of the parent with their ancestors
     * @param tree   {@code QuadTree} the child of parent
     */
    private void fillAllTwigs(ChildOf parent, QuadTree tree){
        if (tree != null && tree.isArea()) {
            ChildOf newPair = new ChildOf(tree, parent);

            if (tree.isTwig()) {
                addSorted(newPair);

            } else {
                fillAllTwigs(newPair, tree.getNorthWest());
                fillAllTwigs(newPair, tree.getNorthEast());
                fillAllTwigs(newPair, tree.getSouthEast());
                fillAllTwigs(newPair, tree.getSouthWest());
            }
        }
    }
    
    //
    //
    //
    //
    //

    /**
     * Adds an actual twig in the {@link AVL} that may be first compressed.
     * 
     * @param pair {@code ChildOf} A twig linked with its ancestors
     */
    private void addSorted(ChildOf pair) {
        if (pair != null) {
            if (twigList == null) twigList = new AVL<ChildOf>((int) Math.round(pair.node.getTreeEpsilon()), pair);
            else twigList.add(pair.node.getTreeEpsilon(), pair);
        }
    }

    //
    //
    //
    //

    /**
     * Removes the first twig that has been compressed and add one of its ancestors if possible.
     * <p>
     * When searching for its ancestors, it also tries to maintain a valid QuadTree
     * by mergin a node if its son are colors and equal. Hence the need to keep track
     * of the variation of number of nodes.
     * 
     * @return {@code int} The variation of number of node from te initial tree to the resulting tree
     */
    public int update() {      
		ChildOf[] minChildOf = new ChildOf[1];
		twigList = twigList.extractMinAvl(minChildOf);
        ChildOf parentOfMin = minChildOf[0].nodeFather;  

        if (parentOfMin == null) {
            return 0;

        } else {
            int varNode = 0;

            /* If we had a filiform tree, by compressing a node we've created a twig.
             * If this twif has equal sons, we fuse it and update the varNode variable.
             */
            while (parentOfMin != null && (parentOfMin.node.isColor() || parentOfMin.node.fuseEqualSon() == 1)) {
                parentOfMin = parentOfMin.nodeFather;
                varNode -= 4;
            }

            /* If we end up with a parent that is a twig, we add it to the list,
             * otherwise, this parent has another child that has yet to be compressed
             */
            if (parentOfMin != null && parentOfMin.node.isTwig()) {
                addSorted(parentOfMin);
            }

            return varNode;
        }
    }

}
