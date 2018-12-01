
import java.io.*;


public class Main {
    public static final int ITERATIONS = 100000;
    public static final int NUMBEROFANTS = 18;
    public static final int NUMBEROFCITIES = 37;

    public static final double ALPHA = 0.5;
    public static final double BETA  = 0.8;
    public static final double Q     = 1000;
    public static final double RO    = 0.2;
    public static final int    TAUMAX = 2;
    public static final int    INITIALCITY = 0; //source


    public static void main(String[] args) {

        ACO ants = new ACO(NUMBEROFANTS, NUMBEROFCITIES,
                ALPHA, BETA, Q, RO, TAUMAX, INITIALCITY);
        ants.init();
        // read from file
        try {

            File file = new File("cities.txt");

            BufferedReader bf = new BufferedReader(new FileReader(file));

            String line;
            int i = 0;
            while ((line = bf.readLine()) != null) {
                String[] words = line.split("\t");
                ants.setCITYPOSITION(i, Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                for (int j = 0; j < NUMBEROFCITIES; j++ ){
                    if (i == j) continue;
                    ants.connectCITIES(i, j);
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

//        ants.connectCITIES (0, 1);
//        ants.connectCITIES (0, 2);
//        ants.connectCITIES (0, 3);
//        ants.connectCITIES (0, 7);
//        ants.connectCITIES (1, 3);
//        ants.connectCITIES (1, 5);
//        ants.connectCITIES (1, 7);
//        ants.connectCITIES (2, 4);
//        ants.connectCITIES (2, 5);
//        ants.connectCITIES (2, 6);
//        ants.connectCITIES (4, 3);
//        ants.connectCITIES (4, 5);
//        ants.connectCITIES (4, 7);
//        ants.connectCITIES (6, 7);
	/* ants.connectCITIES(8, 2);
	ants.connectCITIES(8, 6);
	ants.connectCITIES(8, 7); */

//        ants.setCITYPOSITION (0,  1,  1);
//        ants.setCITYPOSITION (1, 10, 10);
//        ants.setCITYPOSITION (2, 20, 10);
//        ants.setCITYPOSITION (3, 10, 30);
//        ants.setCITYPOSITION (4, 15,  5);
//        ants.setCITYPOSITION (5, 10,  1);
//        ants.setCITYPOSITION (6, 20, 20);
//        ants.setCITYPOSITION (7, 20, 30);
        // ants.setCITYPOSITION(8, 26, 20);
        
        //ants.printGRAPH();
        //ants.printPHEROMONES();
        ants.optimize(ITERATIONS);
        ants.printRESULTS();
    }
}
