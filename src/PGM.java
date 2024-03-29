import java.io.*;
import java.util.*;
import java.nio.file.*;

/**
 * The class PGM represents a PGM image using grayscale format into a quadtree that can be later
 * compress to use less storage. The definition of the PGM Format File is explained here :
 * <a href="https://en.wikipedia.org/wiki/Netpbm">Wikipedia#Netpbm</a>.
 * <p>
 * Different attributes are described in this class :
 * <ul>
 *      <li>{@code luminosityMax}     : the maximum luminosity value present in the data                </li>
 *      <li>{@code length}            : the width or height of the image being a power of 2             </li>
 *      <li>{@code nbNodesCurrent}   : the number of current leaves in the QuadTree                    </li>
 *      <li>{@code treeRepresentation}: the QuadTree containing all the luminosity data of the PGM image</li>
 * </ul>
 * <p> 
 * Uses these following librairies : 
 * {@link ArrayList}, 
 * {@link Scanner}, 
 * {@link File}, 
 * {@link IOException}, 
 * {@link Files}, 
 * {@link BufferedWriter}
 * <p>
 * Two compression methods can be used :
 *  {@link #compressLambda()},
 *  {@link #compressRho(int)}
 * 
 * @author Monique RIMBERT  (monique.rimbert@etu.univ-nantes.fr)
 * @author Aurel HAMON      (aurel.hamon@etu.univ-nantes.fr)
 * 
 * @version 1.1 on the 11/16/2023
 * 
 * @see #PGM(String)
 * @see #PGM(int, int)
 * @see #toPGM(String)
 * @see #compressLambda(QuadTree)
 * @see #compressRho(QuadTree, int, int)
 * @see #compressRho(int)
 * @see #toString()
 * 
 */
public class PGM {
    private Integer luminosityMax;
    private Integer length;
    private Integer nbNodesCurrent;
    
    private QuadTree treeRepresentation;

    /* GETTERS SETTERS */
    /**
     * @return {@code int} The maximum luminosity value present in the data
     */
    public int getLuminosityMax()       {   return luminosityMax;   }

    /**
     * @return {@code int} represents the surface of the image with length being a power of 2
     */
    public int getLength()              {   return length;  }

    /**
     * @return {@code QuadTree} containing all the luminosity data of the PGM image
     */
    public QuadTree getTreeRepresentation()  {   return treeRepresentation;  }  

    /**
     * @return {@code int} The number of leaves currently present in the quadtree
     */
    public int getNbNodesCurrent()       {   return nbNodesCurrent;   }


    /**
     * @param lumMax The new maximum luminosity value present in the data
     */
    public void setLuminosityMax(int lumMax) {   luminosityMax   = lumMax;   }  //XXX The integrity of the PGM object cannot be guaranteed

    /**
     * @param n The new surface of the image with length being a power of 2
     */
    public void setLength(int n)             {   length          = n;        }  //XXX The integrity of the PGM object cannot be guaranteed

    /**
     * @param tree the new QuadTree containing all the luminosity data of the PGM image
     */
    public void setQuadTree(QuadTree tree)   {   treeRepresentation = tree;  }  //XXX The integrity of the PGM object cannot be guaranteed


    /* METHODS */


    /**
     * Creates a new instance of a PGM without a QuadTree.
     * 
     * @param lumMax {@code int} the maximum luminosity value present in the data
     * @param n      {@code int} the surface of the image with length being a power of 2
     */
    public PGM (int lumMax, int n) {
        this.luminosityMax = lumMax;
        this.length = n;

        this.treeRepresentation = null;
        this.nbNodesCurrent    = null;
    }

    //
    //
    //
    //
    
    /**
     * Returns if a string is a PGM comment.
     * <p>
     * We use a regular expression to recognize a line of comment. As per the definition of
     * the format of a PGM file. A line of comment begins by a number sign {@code #}.
     * 
     * @param line {@code String} of a file being tested
     * @return {@code true} if the line begins with a '#'
     * 
     * @see String#matches(String)
     */
    private boolean isComment(String line) {
    	return line.matches("^#.*");
    }
    
    /**
     * Returns the first data information it can find from the left.
     * <p>
     * Each piece of data is separated by spaces that are either spaces,
     * newlines or tabulations.
     * 
     * @param line {@code String} containing relevant data for a PGM image. 
     *             Those can be the magic number, the width, height, the maximum
     *             value and the colors.
     * @return {@code String} corresponding to one data information or {@code ""}
     *         if there was remaining no data.
     * 
     * @see #removeNextToken(String)
     */
    private String getNextToken(String line) {
    	String res = ""; int i = 0; 
        boolean gotDigit = false;
        boolean gotSpace = false;
    	
    	while (i < line.length() && !(gotDigit && gotSpace)) {
            gotSpace = (line.charAt(i) == ' ' || line.charAt(i) == '\t'  || line.charAt(i) == '\n');
            gotDigit = gotDigit || !gotSpace;

    		res += (!gotSpace) ? line.charAt(i) : "";

    		i++;
    	}
    	return res;
    }
    
    /**
     * Removes the first data information it can find from the left.
     * 
     * @param line {@code String} containing relevant data for a PGM image. 
     *             Those can be the magic number, the width, height, the maximum
     *             value and the colors.
     * @return {@code String} the line after the data was removed or {@code ""}
     *         if there was remaining no data
     * 
     * @see #getNextToken(String)
     */
    private String removeNextToken(String line) {
    	int i = 0;
        boolean gotDigit = false;
        boolean gotSpace = false;
        
    	while (i < line.length() && !(gotDigit && gotSpace)) {
            gotSpace = (line.charAt(i) == ' ' || line.charAt(i) == '\t'  || line.charAt(i) == '\n');
            gotDigit = gotDigit || !gotSpace;
    		i++;
    	}

        /* If maybe there are still tokens on the right */
    	return (i < line.length()) ? line.substring(i, line.length()) : "";
    }

    //
    //
    //
    //

    /**
     * Creates an instance of PGM, a representation of a PGM file using {@link QuadTree}.
     * <p>
     * We used a sort of lexer for the PGM file so it can accept any formatting
     * of a valid PGM file. We use {@link String#matches(String)} that accepts regular expressions.
     * We follow these rules :                                  <p>
     *      - Line    -> {@code (Tokens | Comment)\n}           <p>
     *      - Comment -> {@code #.*}                            <p>
     *      - Tokens  -> {@code Token Tokens | Token }          <p>
     *      - Token   -> {@code [a-zA-Z0-9]}                    
     * 
     * @param pathfile {@code String} representing the relative path of our PGM file
     * @throws IOException If the file does not exist.
     * 
     * @see #isComment(String)
     */
    public PGM(String pathfile) throws IOException {
        File pgmFile = new File(pathfile);

        if (!pgmFile.exists()) throw new IOException(pathfile + "File or path to file does not exist !");

        Scanner scanner = new Scanner(pgmFile);
        scanner.useDelimiter("\s|\\n");
        
        String buffer;          //A string in which we'll store each line of the file.
        /* 0 => P2 ; 1 => width ; 2 => height
         * 3 => maximum value ; 4 and above => colors
         */
        int cptInformation = 0; 
        int cptRow = 0;         //The number of values in a row
        
        ArrayList<ArrayList<Integer>> lumValues2D = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> lumValuesRow = new ArrayList<Integer>();

        /* Begin reading the file */
		while (scanner.hasNext()) {
			buffer = scanner.nextLine();
			
			if (!isComment(buffer)) {
				while (buffer.length() > 0) {
					/* We read the next token : P2 or a number */
					String token = getNextToken(buffer);
					buffer = removeNextToken(buffer);
					
					/* The following valid inputs will contain : pixels data */
					if (cptInformation > 3) {
						/* Add token in row otherwise add row in tab */
						if (cptRow < length) {
							lumValuesRow.add(Integer.parseInt(token));
							cptRow++;
							
						} else {
							lumValues2D.add(lumValuesRow);
							lumValuesRow = new ArrayList<Integer>();
							lumValuesRow.add(Integer.parseInt(token));
							cptRow = 1;
						
						}
					}
					/* Luminosity max data */
					else if (cptInformation == 3) {
						luminosityMax = Integer.parseInt(token);
						cptInformation++;
					}
					/* Width Data */
					else if (cptInformation == 1) {
						length = Integer.parseInt(token);
						cptInformation++;
					}
                    /* With this else condition, we skip <P2> and the height */
					else {
						cptInformation++;
					}
				}
			}
		}
		
        /* IMPORTANT : We add the very last row that was previously being read! */
        lumValues2D.add(lumValuesRow);
		scanner.close();

        /* As Integer cannot be "passed-by-reference" in a parameter of a 
         * function. We pass an array that are considered a Java object,
         * therefore we will have access to the modification of their content.
         */
        int[] tabNodes = new int[1];

        treeRepresentation = new QuadTree(lumValues2D, tabNodes);
        nbNodesCurrent = tabNodes[0];
        /* End reading the file */
    }
    
    //
    //
    //
    //
    

    /**
     * Creates a new PGM file if a file does not exist in the current directory.
     * <p>
     * Please note that {@code \\} need to be used in a path if this programs is run on a Windows; {@code /} if
     * it's run on a UNIX machine.
     * 
     * @param pathname {@code String} representing the {@code path/fileName} of our new PGM file
     * @throws IOException If there is already a file with this pathname in the current directory.
     * 
     * @see Files
     * @see Path
     * @see BufferedWriter
     */
    public void toPGM(String pathname) throws IOException {
        if (!Files.exists(Paths.get(pathname))) { //Creation of a new file was successful  

        BufferedWriter bufferWrite = Files.newBufferedWriter(Paths.get(pathname));

        /* Write the header of our greyscale PGM File */
        bufferWrite.write("P2", 0, 2);
        bufferWrite.newLine();

        bufferWrite.write(length.toString() + ' ' + length.toString(), 0, 1 + length.toString().length() * 2);
        bufferWrite.newLine();

        bufferWrite.write(luminosityMax.toString(), 0, luminosityMax.toString().length());
        bufferWrite.newLine();

        /* We get the 2 Dimensional Array from our QuadTree */
        ArrayList<ArrayList<Integer>> lumValues2D = treeRepresentation.toArea(length);

        /* We write now the luminosity value of our grey pixels*/
        for (ArrayList<Integer> lumValuesRows : lumValues2D) {
            for (Integer lumValue : lumValuesRows) {
                bufferWrite.write(lumValue.toString() + ' ', 0, lumValue.toString().length() + 1);
            }
            bufferWrite.newLine();

        }

        bufferWrite.flush();
        bufferWrite.close();

        } else { //Attempt of creating a newfile resulted in failure
            throw new IOException("This file already exists!");

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
     * @param A {@code QuadTree} representing the PGM file
     * @return {@code true} if the compression is finished, {@code false} if the 
     * node given was an external, therefore could not be compressed.
     * 
     * @see #luminosityLogarithmicMean(QuadTree)
     */
    @Deprecated
    protected boolean compressLambda(QuadTree A) {
        if (A == null) return true;
        if (A.isArea()) {
            /* We compress the children first then compute the new boolean value
             * This order is important as Java WILL NOT execute the function
             * if the variable finished is already true.
             */
            boolean finished = compressLambda(A.getNorthWest());
                    finished = compressLambda(A.getNorthEast()) || finished;
                    finished = compressLambda(A.getSouthWest()) || finished;
                    finished = compressLambda(A.getSouthEast()) || finished;
            
            if (!finished) {
                /* We apply the compression formula */
                A.setNodeAsColor((int) Math.round(A.getLogarithmicMean()));
                nbNodesCurrent -= 4;
                
            } else {
                nbNodesCurrent -= 4 * A.fuseEqualSon();
            }

            return true;
            
        } else /* A.isColor() */{
            return false;
        }
    }

    /**
     * Compresses a {@code QuadTree} by using a logarithmic mean of luminosity.
     * <p>
     * Calls the recursive method {@link QuadTree#compressLambda()}. In the end,
     * we expect the QuadTree to have one level compressed, if we don't take into
     * consideration merging leaves with the same value.
     */
    public void compressLambda() { 
        nbNodesCurrent += treeRepresentation.compressLambda();
    }

    //
    //
    //
    //

    /**
     * Compresses a {@code QuadTree} according to a factor rho.
     * <p>
     * In the end, we verify if have reached the factor required. If not, we need
     * to merge the final and remaining children.
     * 
     * @param rho {@code int} between 0 and 100, it represents a factor of remaining leaves in the QuadTree
     * 
     * @see QuadTree#compressRho(int, int) 
     */
    public void compressRho(int rho) {
        nbNodesCurrent += treeRepresentation.compressRho(rho, nbNodesCurrent);
    }

    /**
     * Returns a {@code String} representation of the object.
     * <p>
     * This methods overrides the {@link Object#toString()} method which
     * returns a string representation of the object.
     * 
     * @return {@code String} representation of the object
     */
    @Override
    public String toString(){
        String text =   "luminosityMax : "  + luminosityMax;
        text        +=  "\nlength : "       + length;
        text        +=  "\ntreeRepresentation : \n" + treeRepresentation.toString();

        return text;
    }
}
