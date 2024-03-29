import java.io.IOException;
import java.util.Scanner;

/**
 * Programme principal du projet d'Algorithmique et Structure de Données 3
 * <p>
 * Etape de compilation et execution :          <br/>
 *          > {@code javac *.java *d ../bin}    <br/>
 *          > {@code java Main ...}
 * 
 * @author Monique RIMBERT (monique.rimbert@etu.univ-nantes.fr)
 * @author Aurel HAMON (aurel.hamon@etu.univ-nantes.fr)
 */
public class Main {
    /**
     * Read an {@code int} between 0 and 100 both included
     * 
     * @param terminal {@code Scanner} The input stream
     * @return {@code int} the value of rho
     */
    public static int readRho(Scanner terminal) {
            System.out.print("Donnez entier 0 <= rho <= 100 : ");
            int rho = terminal.nextInt();

            /* Verification du facteur rho */
            while (rho < 0 || rho > 100) {
                System.err.println("\nRho non valide !");
                System.out.print("Donnez entier 0 <= rho <= 100 : ");
                rho = terminal.nextInt();

            }

            terminal.nextLine();
            return rho;
    }

    //
    //
    //
    //

    /**
     * Reads the relative path of a file
     * 
     * @param terminal {@code Scanner} The input stream
     * @return {@code String} the path to the image file
     */
    public static String readFilename(Scanner terminal) {
        System.out.print("Donnez chemin relatif au fichier : ");
        String fileName = terminal.nextLine();

        /* Verification du nom de fichier */
        while (fileName == "") {
            System.err.println("\nChemin non valide !");
            System.out.print("Donnez chermin relatif au fichier : ");
            fileName = terminal.nextLine();

        }

        return fileName;
    }

    //
    //
    //
    //
    
    /**
     * Executes {@link PGM#compressRho(int)}, creates a new, and return the initial number of nodes in the tree
     * 
     * @param obj       {@code PGM} The object that contains the QuadTree
     * @param terminal  {@code Scanner} The input stream
     * @return {@code int} The number of nodes in the initial tree
     */
    public static int compressRho(PGM obj, Scanner terminal, String oldFile) {
        int nbNodesInit = obj.getNbNodesCurrent();
        int rho = readRho(terminal);

        System.out.println("Compression Rho en cours...");
        obj.compressRho(rho);

        System.out.println("Creation fichier compression rho en cours...");
        createPGM(obj, terminal, oldFile.substring(0, oldFile.length() - 4) + "_RHO_" + rho + ".pgm");

        return nbNodesInit;
    }

    //
    //
    //
    //
    
    /**
     * Executes {@link PGM#compressLambda()}, creates a new PGM, and returns the initial number of nodes in the tree
     * 
     * @param obj       {@code PGM} The object that contains the QuadTree
     * @param terminal  {@code Scanner} The input stream
     * @return {@code int} The number of nodes in the initial tree
     */
    public static int compressLambda(PGM obj, Scanner terminal, String oldFile) {
        int nbNodesInit = obj.getNbNodesCurrent();

        System.out.println("Compression Lambda en cours...");
        obj.compressLambda();

        System.out.println("Creation fichier compression lambda en cours...");
        createPGM(obj, terminal, oldFile.substring(0, oldFile.length() - 4) + "_LAMBDA_" + ".pgm");

        return nbNodesInit;
    }

    //
    //
    //
    //

    /**
     * Calls {@link PGM#toPGM(String)} and asks for a new path if a file with the same name already exists
     * 
     * @param obj       {@code PGM} The object that contains our QuadTree
     * @param terminal  {@code Scanner} The input stream
     * @param filename  {@code String} The name of the newly created file
     */
    public static void createPGM(PGM obj, Scanner terminal, String filename) {
        try {
            obj.toPGM(filename);

        } catch (IOException e) {
            e.printStackTrace();
            createPGM(obj, terminal, readFilename(terminal));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    //
    //
    //

    /**
     * Prints the statistics of the last compression done on the tree updated
     * 
     * @param nbNodesInit   {@code int} The initial number of nodes in the tree
     * @param obj           {@code PGM} The PGM object that contains the QuadTree
     * @param lastCompress  {@code int} 0 if the last compression done was Lambda, 
     *                                  1 if it was rho
     */
    public static void affStats(int nbNodesInit, PGM obj, int lastCompress) {
        System.out.println("======= STATISTIQUES =======");
        System.out.println("Nombre initial  de noeuds : \t" + nbNodesInit);

        if (lastCompress == 0) System.out.println("Nombre Lambda   de noeuds  : \t" + obj.getNbNodesCurrent());
        else System.out.println("Nombre Rho      de noeuds  : \t" + obj.getNbNodesCurrent());

        System.out.println("(nbCourant/nbInit)% :: Taux de compression : \t" + ((double)100.0 * (obj.getNbNodesCurrent()) / (double)(nbNodesInit)) + " %");
        System.out.println("==========================");

    }

    //
    //
    //
    //

    /**
     * Non interactive mode
     * <p>
     *  - Reads the PGM file
     *  - Compress the PGM file with both method and saves both QuadTrees
     *  - Saves the toString() of both trees
     * 
     * @param filename  {@code String} relative path of a pgm file
     * @param rho       {@code int} a value between 0 and 100 icluded
     */
    //TODO QuadTree#toString()
    public static void nonInteractif(String filename, int rho) {
            assert filename == "" || rho < 0 || rho > 100;

            try {
                PGM lambdaPGM = new PGM(filename),
                    rhoPGM    = new PGM(filename);
                
                /* Sauvegarde du nombre initial de noeuds */
                int nbNoeudInit = lambdaPGM.getNbNodesCurrent();

                /* Compression des arbres */
                lambdaPGM.compressLambda();
                rhoPGM.compressRho(rho);

                /* Creation des fichiers */
                System.out.println("Creation fichier compression lambda en cours...");
                String lambdaFile = filename.substring(0, filename.length() - 4) + "_LAMBDA_" + ".pgm";
                lambdaPGM.toPGM(lambdaFile);

                System.out.println("Creation fichier compression rho en cours...");
                String rhoFile = filename.substring(0, filename.length() - 4) + "_RHO_" + rho + ".pgm";
                rhoPGM.toPGM(rhoFile);

                /* Affichage statistique */
                System.out.println("======= STATISTIQUES =======");
                System.out.println("Nombre initial  de noeuds : \t" + nbNoeudInit);
                System.out.println("Nombre Lambda   de noeuds  : \t"        + lambdaPGM.getNbNodesCurrent());
                System.out.println("Taux de compression : \t" + ((double)100.0 * (lambdaPGM.getNbNodesCurrent()) / (double)(nbNoeudInit)) + " %");
                System.out.println("Nombre Rho "+ rho +" de noeuds  : \t"   + rhoPGM.getNbNodesCurrent());
                System.out.println("Taux de compression : \t" + ((double)100.0 * (lambdaPGM.getNbNodesCurrent()) / (double)(nbNoeudInit)) + " %");

            } catch (Exception e) {
                e.printStackTrace();
            }
            



    }

    //
    //
    //
    //

    /**
     * Prints all the possible functionnalities
     */
    public static void affMenu() {
        System.out.println("\t 1. Choisir une image a charger");
        System.out.println("\t 2. Recharger l'image");
        System.out.println("\t 3. Appliquer une compression lambda et le sauvegarder");
        System.out.println("\t 4. Appliquer une compression rho et le sauvegarder");
        System.out.println("\t 5. Generer un PGM");
        System.out.println("\t 6. Generer le toString()");
        System.out.println("\t Sinon STOP");
    }

    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //

    /**
     * Main program of class.
     * Uses a textual menu interface with these functionalities :
     *      1. Choisir une image à charger
     *      2. Recharger l'image
     *      3. Appliquer une compression lambda et le sauvegarder
     *      4. Appliquer une compression rho et le sauvegarder
     *      5. Générer un PGM
     *      6. Générer le toString()
     *      DEFAULT 
     * 
     * @param args {@code String[]} an array of given parameters
     */
    public static void main(String[] args) {
        /* Initialisation des valeurs en mode non-interactif  */
        if (args.length == 2) {
            System.out.println("======= MODE NON-INTERACTIF =======");

            String filename = args[0];
            int rho         = Integer.parseInt(args[1]);

            nonInteractif(filename, rho);

        /* Initialisation des valeurs en mode-interactif */
        } else {
            System.out.println("========= MODE INTERACTIF =========");

            //Option pour l'utilisateur
            int anchor = PGMConverter.WEST;

            //Variable pour l'arbre/PGM
            String filename;
            PGM pgmObject;
            int nbNodesInit;
            
            //Variable pour menu
            Scanner terminal = new Scanner(System.in);
            int menuChoice;
            boolean stop = false;

            try {
                filename = readFilename(terminal);
                pgmObject = new PGM(filename);

                while (!stop) {
                    affMenu();
                    System.out.print("$ ");
                    menuChoice = terminal.nextInt();
                    terminal.nextLine();

                    switch (menuChoice) {
                        // 1. Choisir une image à charger
                        case 1:
                            filename = readFilename(terminal);
                            
                            /* Si l'image donnée n'est pas un pgm, alors on la convertie d'abord */
                            if (!filename.endsWith(".pgm")) {
                                System.out.println("Conversion de l'image en PGM...");
                                filename = PGMConverter.convertToPGM(filename, anchor);
                            }

                            pgmObject = new PGM(filename);
                            break;
                        
                        // 2. Recharger l'image
                        case 2:
                            pgmObject = new PGM(filename);
                            break;

                        // 3. Appliquer une compression Lambda et la sauvegarder
                        case 3:
                            nbNodesInit = compressLambda(pgmObject, terminal, filename);
                            affStats(nbNodesInit, pgmObject, 0);
                            break;
                        
                        // 4. Appliquer une compression Rho et la sauvegarder
                        case 4:
                            nbNodesInit = compressRho(pgmObject, terminal, filename);
                            affStats(nbNodesInit, pgmObject, 1);
                            break;

                        // 5. Générer un PGM
                        case 5:
                            createPGM(pgmObject, terminal, filename);
                            break;

                        // 6. Générer le toString()
                        case 6:
                            System.out.println("Sauvegarde du quadtree dans un fichier.txt...");
                            pgmObject.getTreeRepresentation().saveToFile(filename);
                            System.out.println("Sauvegarde terminée !");
                            break;

                        // STOP
                        default:
                            stop = true;
                            break;
                    }

                }
                terminal.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
