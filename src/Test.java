import java.util.Scanner;

/**
 * Programme principal du projet d'Algorithmique et Structure de Données 3
 * <p>
 * Etape de compilation et execution :          <br/>
 *          > {@code javac *.java *d ../bin}    <br/>
 *          > {@code java Test ...}
 * 
 * @author Monique RIMBERT (monique.rimbert@etu.univ-nantes.fr)
 * @author Aurel HAMON (aurel.hamon@etu.univ-nantes.fr)
 */
public class Test {
    //
    //
    //
    /**
     * Main program of class.
     * 
     * @param args {@code String[]} an array of given parameters
     */
    public static void main(String[] args) {
        String fileName;
            Scanner terminal = new Scanner(System.in);
        int rhoIn, nbFeuilleInit;
        int anchor = (args.length == 1) ? Integer.parseInt(args[0]) % 9 : 0;
        
        /* Initialisation des valeurs en mode non-interactif  */
        if (args.length == 2) {
            System.out.println("=======MODE-NON-INTERACTIF=======");

            fileName    = args[0];
            rhoIn         = Integer.parseInt(args[1]);

            assert fileName == "" || rhoIn < 0 || rhoIn > 100;

        /* Initialisation des valeurs en mode-interactif */
        } else {
            System.out.println("=========MODE-INTERACTIF=========");

            System.out.print("Donnez chemin relatif au fichier : ");
            fileName = terminal.nextLine();

            /* Verification du nom de fichier */
            while (fileName == "") {
                System.err.println("\nChemin non valide !");
                System.out.print("Donnez chermin relatif au fichier : ");
                fileName = terminal.nextLine();

            }

            System.out.print("Donnez entier 0 <= rho <= 100 : ");
            rhoIn = 0;

            /* Verification du facteur rho */
            while (rhoIn < 0 || rhoIn > 100) {
                System.err.println("\nRho non valide !");
                System.out.print("Donnez entier 0 <= rho <= 100 : ");
                rhoIn = terminal.nextInt();

            }
        }

        try {
            /* Si l'image donnée n'est pas un pgm, alors on la convertie d'abord */
            if (!fileName.endsWith(".pgm")) {
                System.out.println("Conversion de l'image en PGM...");
                fileName = PGMConverter.convertToPGM(fileName, anchor);
            }

            for (int i = 0; i <= 100; i += 1) {
                System.out.print("Donnez entier 0 <= rho <= 100 : ");
                int rho = terminal.nextInt();

                PGM rhoPGM    = new PGM(fileName);
                nbFeuilleInit = rhoPGM.getNbNodesCurrent();

                /* Application des compressions */
                System.out.println("Compression rho de l'image en cours...");
                long startTime = System.nanoTime();
                rhoPGM.compressRho(rho);
                long endTime = System.nanoTime();

                /* Affichage statistique */
                System.out.println("=======STATISTIQUES=======");
                System.out.println("Nombre initial  de feuilles : \t" + nbFeuilleInit);
                System.out.println("Nombre Rho "+ rho +" de feuilles  : \t" + rhoPGM.getNbNodesCurrent());
                System.out.println("==========================");
                
                /* Creation des fichiers */
                System.out.println("Creation fichier compression rho en cours...");
                rhoPGM.toPGM(fileName.substring(0, fileName.length() - 4) + "_RHO_" + rho + ".pgm");

                
                System.out.println("Duration : " + (endTime - startTime));

            }
            
            terminal.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}