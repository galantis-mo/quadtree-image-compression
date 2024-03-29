import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class PGMConverter contains the necessary methods to prepare any bitmap (other than pgm) images for
 * using the class {@link PGM}.
 * The definition of the PGM Format File is explained here :
 * <a href="https://en.wikipedia.org/wiki/Netpbm">Wikipedia#Netpbm</a>.
 * 
 * <p> Different data are used in PGM object :
 * <ul>
 *      <li>{@code luminosityMax}     : the maximum luminosity value present in the data                </li>
 *      <li>{@code length}            : the width or height of the image that will be a power of 2      </li>
 *      <li>{@code nbNodesCurrent}   : the number of current leaves in the QuadTree                    </li>
 *      <li>{@code treeRepresentation}: the QuadTree containing all the luminosity data of the PGM image</li>
 * </ul>
 * <p> 
 * As the image may be cropped. These several anchor are proposed :
 * <ul>
 *      <li>{@code CENTER}      : the new image is placed on the middle of x and y axis                 </li>
 *      <li>{@code NORTH}       : the top edge of the new image is placed on the middle top edge        </li>
 *      <li>{@code WEST}        : the left edge of the new image is placed on the middle left edge      </li>
 *      <li>{@code SOUTH}       : the bottom edge of the new image is placed on the middle bottom edge  </li>
 *      <li>{@code EAST}        : the right edge of the new image is placed on the middle right edge    </li>
 *      <li>{@code NORTH_WEST}  : the top left corned of the new image is place on the top left corner  </li>
 *      <li>{@code NORTH_EAST}  : the top right corned of the new image is place on the top right corner </li>
 *      <li>{@code SOUTH_WEST}  : the bottom left corned of the new image is place on the bottom left corner </li>
 *      <li>{@code SOUTH_EAST}  : the bottom right corned of the new image is place on the bottom right corner</li>
 * </ul>
 * <p>
 * Uses these following librairies :
 * {@link Color}, 
 * {@link BufferedImage}, 
 * {@link ImageIO}, 
 * {@link IOException}, 
 * {@link Files}, 
 * {@link BufferedWriter}
 * 
 * @author Monique RIMBERT  (monique.rimbert@etu.univ-nantes.fr)
 * @version 1.1 on the 11/16/2023
 */
public abstract class PGMConverter {
    static final int CENTER = 0;
    static final int NORTH = 1;
    static final int SOUTH = 2;

    static final int WEST = 3;
    static final int NORTH_WEST = 4;
    static final int SOUTH_WEST = 5;

    static final int EAST = 6;
    static final int NORTH_EAST = 7;
    static final int SOUTH_EAST = 8;

    /**
     * Returns the closest smaller power of two of the specified number.
     * 
     * @param max {@code int} the bound to the right
     * @return {@code int} the closest smaller power of two of the specified number
     */
    public static int getClosestPower2(int max) {
        int res = 1;

        while (res <= max) res *= 2;

        return res/2;
    }

    /**
     * Returns the luminosity of a aRGB value.
     * 
     * @param color {@code int} the arbg value to be converted
     * @return {@code int} the luminosity according to human color perception
     * 
     * @see <a href="https://www.baeldung.com/cs/convert-rgb-to-grayscale">Source of luminosity method</a>
     */
    public static int getLuminosity(Color color) {
        return (int)Math.round(0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());
    }

    /**
     * Returns the origin of the new PGM Image in an image according to the specified anchor.
     * <p>
     * The anchor must be equal to one of them : {@link #CENTER}, {@link #NORTH}, {@link #SOUTH},
     * {@link #WEST}, {@link #NORTH_WEST}, {@link #SOUTH_WEST}, {@link #EAST}, {@link #NORTH_EAST},
     * {@link #SOUTH_EAST}.
     * 
     * @param width     {@code int} the width of the original image
     * @param height    {@code int} the height of the original image
     * @param length    {@code int} the length of our new image being a power of 2.
     * @param anchor    {@code int} the anchor of the new image in the original image
     * @return {@code int[]} of size 2, the coordinate of the origin of the new image
     *         in the original image
     * 
     * @throws Exception If the specified anchor is greater than 8 or less than 0
     */
    protected static int[] calculateOrigin(int width, int height, int length, int anchor) throws Exception {
        int[] coord = new int[2]; // x y    => COlONNE LIGNE
        switch (anchor) {
            case 0 :
                coord[0] = Math.floorDiv(width - length, 2);
                coord[1] = Math.floorDiv(height - length, 2);
                break;
            
            case 1 :
                coord[0] = Math.floorDiv(width - length, 2);
                coord[1] = 0;
                break;
            
            case 2 :
                coord[0] = Math.floorDiv(width - length, 2);
                coord[1] = height - length;
                break;
            
            case 3 :
                coord[0] = 0;
                coord[1] = Math.floorDiv(height - length, 2);
                break;
            
            case 4 :
                coord[0] = 0;
                coord[1] = 0;
                break;
            
            case 5 :
                coord[0] = 0;
                coord[1] = height - length;
                break;
             
            case 6 :
                coord[0] = width - length;
                coord[1] = Math.floorDiv(height - length, 2);
                break;
            
            case 7 :
                coord[0] = width - length;
                coord[1] = 0;
                break;
            
            case 8 :
                coord[0] = width - length;
                coord[1] = height - length;
                break;
            default :
                throw new Exception("Invalid anchor input.");         
        }
        return coord;
    }

    /**
     * Converts a bitmap file to a greyscale PGM file with a specied anchor
     * 
     * @param imagePath {@code String} path to initial bitmap image
     * @param anchor    {@code int} determine the origin of the new image from original
     * @return {@code String} new path to PGM file
     * 
     * @throws IOException If a file with the same name as the new image already exist in the given directory
     * @throws Exception If the anchor given is different than {@link #CENTER}, {@link #NORTH}, {@link #SOUTH},
     * {@link #WEST}, {@link #NORTH_WEST}, {@link #SOUTH_WEST}, {@link #EAST}, {@link #NORTH_EAST} and
     * {@link #SOUTH_EAST}
     */
    public static String convertToPGM(String imagePath, int anchor) throws Exception{
        /* Read an image */
        BufferedImage image = ImageIO.read(new File(imagePath));

        /* Required data for PGMM file */
        Integer newImageLength = getClosestPower2(Math.min(image.getWidth(), image.getHeight()));   //Crops the image
        Integer newMaxColorValue  = -1;
        String newImagePath = imagePath.substring(0, imagePath.length() - 4) + ".pgm";

        Integer[][] newColorValues = new Integer[newImageLength][newImageLength];
    
        /* Get all the luminosity values from the btmap image */
        int[] originNewImage = calculateOrigin(image.getWidth(), image.getHeight(), newImageLength, anchor);
        
        for (int row = 0; row < newImageLength; row++) {
            for (int col = 0; col < newImageLength; col++) {
                /* We get the pixel on the image according to an offset (originNewImage + coordNewImage(row, col)) */
                newColorValues[row][col] = getLuminosity(new Color(image.getRGB(originNewImage[0] + col, originNewImage[1] + row)));
                newMaxColorValue = Math.max(newMaxColorValue, newColorValues[row][col]);

            }
        }

        /* Write the PGM file */
        if (!Files.exists(Paths.get(newImagePath))) { //Creation of a new file was successful  
            BufferedWriter bufferWrite = Files.newBufferedWriter(Paths.get(newImagePath));

            /* Write the header of our greyscale PGM File */
            bufferWrite.write("P2", 0, 2);
            bufferWrite.newLine();

            bufferWrite.write(newImageLength.toString() + ' ' + newImageLength.toString(), 0, 1 + newImageLength.toString().length() * 2);
            bufferWrite.newLine();

            bufferWrite.write(newMaxColorValue.toString(), 0, newMaxColorValue.toString().length());
            bufferWrite.newLine();

            /* We got previously the 2 Dimensional Array from our image */
            /* We write now the luminosity value of our grey pixels*/
            for (Integer[] lumValuesRows : newColorValues) {
                for (Integer lumValue : lumValuesRows) {
                    bufferWrite.write(lumValue.toString() + '\s', 0, lumValue.toString().length() + 1);
                }
                bufferWrite.newLine();
            }

            bufferWrite.flush();
            bufferWrite.close();
        } else throw new IOException("Cannot convert to PGM, a file with same name already exists in directory.");
        
        return newImagePath;
    }

    /**
     * Converts a bitmap file to a greyscale PGM file using the default anchor : CENTER.
     * 
     * @param imagePath {@code String} path to initial bitmap image
     * @return {@code String} new path to PGM file
     * 
     * @throws Exception If a file with the same name as the new image already exist in the given directory 
     */
    public static String convertToPGM(String imagePath) throws Exception{
        return convertToPGM(imagePath, NORTH_WEST);
    }

}
