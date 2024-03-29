import java.util.LinkedList;

/**
 * This AVL class is a an AVL tree, a self-balancing ABR that uses double for element.
 * <p>
 * It uses {@link LinkedList} to optimize the suppression and the insetion of new elements, being in the end constant. 
 * 
 * @author Monique RIMBERT  (monique.rimbert@etu.univ-nantes.fr)
 * @author Aurel HAMON      (aurel.hamon@etu.univ-nantes.fr)
 * 
 * @version 2.0 on the 12/04/2023
 * 
 * @see PGM#compressRho(int)
 * @see QuadTree#compressRho(int, int)
 */
public class AVL<T> {
    /* Private attributes */
    private int balance;

    private Double value;
    private LinkedList<T> dataCollection;

    private AVL<T>     left;
    private AVL<T>     right;

    //
    //
    //
    //
    //
    //
    /* Constructors */
    /**
     * Creates a new instance a of an empty AVL.
     */
    public AVL() {
	    this.value = null;
        this.dataCollection = new LinkedList<T>();
        this.balance	= 0;
    }


    /**
     * Creates a new instance of AVL not empty.
     * 
     * @param val {@code double} the value of the node
     * @param data {@code T} the first element in the collection of that node
     */
    public AVL(double val, T data) {
	    this.value = val;
    
        this.dataCollection = new LinkedList<T>();
        this.dataCollection.add(data);
        this.balance	= 0;
    }
    
    /**
     * Creates a copy of a pre-existing AVL.
     * 
     * @param obj {@code AVL<T>} the copied AVL
     */
    public AVL(AVL<T> obj) {
        this.balance    = obj.balance;    
	    this.value      = obj.value;
        this.dataCollection = obj.dataCollection;

        this.left	= obj.left;
        this.right	= obj.right;
    }

    //
    //
    //
    //
    //
    //
    /* GETTERS SETTERS */
    /**
     * @return {@code true} if the collection is empty or the tree is empty
     */
    public boolean isEmpty(){
        return this.value == null || this.dataCollection.isEmpty();
    }

    /**
     * @return {@code ArrayList<T>} the collection of this node.
     */
    public LinkedList<T> getDataCollection(){   return this.dataCollection; }

    /**
     * @return The node having the smallest value (on the farthest left)
     */
    public AVL<T> min() {
        if (this.value == null) return null;
        if (this.left == null)  return this;

        else return this.left.min();
    }

    //
    //
    //
    //
    //
    // 
    /* METHODS */
    /**
     * Rotates the tree to the right and changes the root
     */
    private void rotationRight() {
        AVL<T> tmp = new AVL<T>(this);
    	
    	int a = this.balance,
    		b = this.left.balance;
    	
        /* Swaps data first */
        this.value = this.left.value;
        this.balance = this.left.balance;
        this.dataCollection = this.left.dataCollection;

        this.left.value = tmp.value;
        this.left.balance = tmp.balance;
        this.left.dataCollection = tmp.dataCollection;

        /* Swap branches */
        tmp = this.left.left;

        this.left.left = this.left.right;
        this.left.right = this.right;

        this.right = this.left;
        this.left = tmp;

        /* Compute new balances : uses the same logic as rotationLeft()*/
        a = -a;
        b = -b;

        this.right.balance = a - Math.max(b, 0) - 1;
        this.balance = Math.min(a - 2, Math.min(a + b - 2, b - 1));

        this.right.balance *= -1;
        this.balance *= -1;
    }

    //
    //
    //
    //
    
    /**
     * Rotates the tree to the left and changes the root
     */
    private void rotationLeft() {
        AVL<T> tmp = new AVL<T>(this);

        int a = this.balance,
            b = this.right.balance;

        /* Swaps data first */
        this.value = this.right.value;
        this.balance = this.right.balance;
        this.dataCollection = this.right.dataCollection;

        this.right.value = tmp.value;
        this.right.balance = tmp.balance;
        this.right.dataCollection = tmp.dataCollection;

        /* Swap branches */
        tmp = this.right.right;

        this.right.right = this.right.left;
        this.right.left = this.left;

        this.left = this.right;
        this.right = tmp;

        /* Compute new balances */
        this.left.balance = a - Math.max(b, 0) - 1;
        this.balance = Math.min(a - 2, Math.min(a + b - 2, b - 1));
    }
    
    //
    //
    //
    //    
    
    /**
     * Balance the tree in order to maintain a balance of -1, 0 or 1.
     */
    private void balance() {
        if (this.balance == 2) {
            if (this.right.balance < 0) {
                this.right.rotationRight();

            }

            this.rotationLeft();
        }

        else if (this.balance == -2) {            
            if (this.left.balance> 0) {
                this.left.rotationLeft();
            }

            this.rotationRight();
        }
    }

    //
    //
    //
    //

    /***
     * Adds a new data element to the tree.
     * <p>
     * Calls the recursive method {@link AVL#add(Double, Object, int[])}.
     * 
     * @param value {@code double} The value of the node in which to add the data
     * @param data  {@code T} The data we want to add
     */
    public void add(double value, T data) {
        int[] heightVar = {0};
        add(value, data, heightVar);
    }

    /***
     * Adds a new data to the tree recursively.
     * 
     * @param value {@code double} The value of the node in which to add the data
     * @param data  {@code T} The data we want to add
     * @param heightVar {@code int[]} An array of size >= 1, the first value representing
     *                                the variation of height of the subtrees.  
     */
    private void add(double newValue, T newData, int[] heightVar) {
        heightVar[0] = 0;
    
        if (newValue == this.value) {
            this.dataCollection.addLast(newData);
        }

        /* If the value is greater than the current node value */
        else if (newValue > this.value) {
            if (this.right == null) {
                this.right = new AVL<T>(newValue, newData);
                heightVar[0] = 1;

            } else this.right.add(newValue, newData, heightVar);

        /* If the value is less than the current node value */
        } else {
            if (this.left == null) {
                this.left = new AVL<T>(newValue, newData);
                heightVar[0] = 1;

            } else this.left.add(newValue, newData, heightVar);

            heightVar[0] *= -1;
        }
        
        /* If there was a variation in height we balance the tree  */
        if (heightVar[0] != 0) {
            this.balance = this.balance + heightVar[0];
            this.balance();

            if (this.balance == 0) heightVar[0] = 0;
            else heightVar[0] = 1;       
        }
    }

    //
    //
    //
    //

    /**
     * Deletes the node having the smallest value and returns the new root.
     * <p>
     * Calls the recursivev method {@link AVL#deleteMin(int[])}.
     * 
     * @param min {@code T[]} An array of size >= 1, the first value having the min data we just deleted
     * 
     * @return {@code AVL<T>} The new root of the current tree
     */
    public AVL<T> extractMinAvl (T[] min) {
        int[] h = {0};
        return extractMinAvl(min, h);
    }

    /**
     * Deletes the first element having the smallest value or the node, and returns the new root recursively.
     * 
     * @param min {@code T[]} An array of size >= 1, the first value having the min data we just deleted
     * @param heightVar {@code int[]} An array of size >= 1, the first value representing
     *                      the variation of height of the subtrees.  
     *  
     * @return {@code AVL<T>} The new root of the current tree
     */
    private AVL<T> extractMinAvl(T[] min, int[] heightVar){
        heightVar[0] = 0;
        
        /* The minimum is this node */
        if (this.left == null) {
            /* We remove and save the data we just removed */
            min[0] = this.getDataCollection().pop();

            /* If we just extracted the last element left in the collection : we remove the entire node => variation in height of 1 */
            if (this.dataCollection.size() < 1) {
                heightVar[0] = 1;
                return this.right;

            /* Otherwise we remove only the first data element of this collection : the node stays => no variation in height*/
            } else {
                heightVar[0] = 0;
                return this;
            }

        /* We still have elements on the left */
        } else {
            this.left = this.left.extractMinAvl(min, heightVar);

            /* If there was a variation in height */
            if (heightVar[0] != 0) {
                this.balance = this.balance + heightVar[0];
                this.balance();

                /* If the balance of our current node is 0 : that means we lift the subtree by one level => variation in height of 1 */
                if (this.balance == 0) heightVar[0] = 1;
                else heightVar[0] = 0;
            }

            return this;
        }
    }
}
