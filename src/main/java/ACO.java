import java.util.Random;

/**
 * Created by liyangde on Nov, 2018
 */
public class ACO {
    private int NUMBEROFANTS, NUMBEROFCITIES, INITIALCITY;
    private double ALPHA, BETA, Q, RO, TAUMAX;
    private double BESTLENGTH;
    private Randoms randoms;

    int[] BESTROUTE; // Best route in current iteration
    int [][] GRAPH;  // The adjacency matrix, if city i and city j has a path connected, the value in GRAPH[i][j] should be 1; else be 0;
    int [][] ROUTES; // The routes of all ants from 0 to ANTSIZE-1
    double [][] CITIES; // CITIES[i][j] stands for the distance between city i and city j
    double [][] PHEROMONES;// Pheromones on every edge.
    double [][] DELTAPHEROMONES;
    double [][] PROBS;


    public ACO(int nAnts, int nCities, double alpha, double beta, double q, double ro, double taumax, int initCity) {
        this.NUMBEROFANTS = nAnts;
        this.NUMBEROFCITIES = nCities;
        this.ALPHA = alpha;
        this.BETA = beta;
        this.Q = q;
        this.RO = ro;
        this.TAUMAX = taumax;
        this.INITIALCITY = initCity;
        this.randoms = new Randoms(21);
    }

    /**
     * init the matrix
     */
    public void init() {
        this.GRAPH = new int[NUMBEROFCITIES][];
        this.CITIES = new double[NUMBEROFCITIES][];
        this.PHEROMONES = new double[NUMBEROFCITIES][];
        this.DELTAPHEROMONES = new double[NUMBEROFCITIES][];
        this.PROBS = new double[NUMBEROFCITIES][];
        for (int i = 0; i < NUMBEROFCITIES; i++) {
            this.GRAPH[i] = new int[NUMBEROFCITIES];
            this.CITIES[i] = new double[2];
            this.PHEROMONES[i] = new double[NUMBEROFCITIES];
            this.DELTAPHEROMONES[i] = new double[NUMBEROFCITIES];
            this.PROBS[i] = new double[2];
            for (int j = 0; j < 2; j++) {
                CITIES[i][j] = -1.0;
                PROBS[i][j] = -1.0;
            }
            for (int j = 0; j < NUMBEROFCITIES; j++) {
                GRAPH[i][j] = 0;
                PHEROMONES[i][j] = 0.0;
                DELTAPHEROMONES[i][j] = 0.0;
            }
        }

        ROUTES = new int[NUMBEROFCITIES][];
        for (int i = 0; i < NUMBEROFANTS; i++) {
            ROUTES[i] = new int[NUMBEROFCITIES];
            for (int j = 0; j < NUMBEROFCITIES; j++) {
                ROUTES[i][j] = -1;
            }
        }

        BESTLENGTH = (double) Integer.MAX_VALUE;
        BESTROUTE = new int[NUMBEROFCITIES];
        for (int i = 0; i < NUMBEROFCITIES; i++) {
            BESTROUTE[i] = -1;
        }
    }

    public void connectCITIES(int cityi, int cityj) {
        this.GRAPH[cityi][cityj] = 1;
        this.PHEROMONES[cityi][cityj] = randoms.Uniforme() * TAUMAX; // init random pheromones
        this.GRAPH[cityj][cityi] = 1;
        this.PHEROMONES[cityj][cityi] = this.PHEROMONES[cityi][cityj];
    }

    public void setCITYPOSITION(int city, double x, double y) {
        this.CITIES[city][0] = x;
        this.CITIES[city][1] = y;
    }

    public void printPHEROMONES() {
        System.out.println("PHEROMENES: ");
        System.out.print("|");
        for (int i = 0; i < NUMBEROFCITIES; i++) {
            System.out.printf("%5d   ", i);
        }
        System.out.print("- |\n");
        for (int i = 0; i < NUMBEROFCITIES; i++) {
            System.out.print(i + "|");
            for (int j = 0; j < NUMBEROFCITIES; j++) {
                if (i == j) {
                    System.out.printf("%5s   ", "x");
                    continue;
                }
                if (exists(i, j)) {
                    System.out.printf("%7.3f ", PHEROMONES[i][j]);
                } else {
                    if (PHEROMONES[i][j] == 0.0) {
                        System.out.printf("%5.0f   ", PHEROMONES[i][j]);
                    } else {
                        System.out.printf("%7.3f ", PHEROMONES[i][j]);
                    }
                }
            }
            System.out.println("\n");
        }
        System.out.println("\n");

    }

    public void printGRAPH() {
    }

    public void printRESULTS() {
        BESTLENGTH += distance(BESTROUTE[NUMBEROFCITIES - 1], INITIALCITY);
        System.out.println(" BEST ROUTE:");
        for (int i = 0; i < NUMBEROFCITIES; i++) {
            if (BESTROUTE[i] == 0) {
                System.out.println("source ");
                continue;
            }
            if (BESTROUTE[i] >=1 && BESTROUTE[i] <= 26) {
                System.out.print((char) (BESTROUTE[i] - 1 +'A'));
            } else {
                System.out.print((BESTROUTE[i] - 27));
            }
        }
        System.out.println("\n" + "length: " + BESTLENGTH);
    }

    public void optimize(int ITERATIONS) {

        for (int iterations = 1; iterations <= ITERATIONS; iterations++) {
            //System.out.println("ITERATION "+iterations+" HAS STARTED!");

            for (int k = 0; k < NUMBEROFANTS; k++) {
                //System.out.println(": ant "+k+" has been released!");
                while (0 != valid(k, iterations)) {
                    //System.out.println(":: releasing ant "+k+" again!");
                    for (int i = 0; i < NUMBEROFCITIES; i++) {
                        ROUTES[k][i] = -1;
                    }
                    route(k);
                }

                for (int i = 0; i < NUMBEROFCITIES; i++) {
                    //System.out.print(ROUTES[k][i] +" ");
                }
                //System.out.println("\n");
                //System.out.println( ":: route done");

                double rlength = length(k);

                if (rlength < BESTLENGTH) {
                    BESTLENGTH = rlength;
                    for (int i = 0; i < NUMBEROFCITIES; i++) {
                        BESTROUTE[i] = ROUTES[k][i];
                    }
                }
                //System.out.println(": ant "+k+ " has ended!");
            }

            //System.out.println("updating PHEROMONES . . .");
            updatePHEROMONES();
            //cout << " done!" << endl << endl;
            //printPHEROMONES ();

            for (int i = 0; i < NUMBEROFANTS; i++) {
                for (int j = 0; j < NUMBEROFCITIES; j++) {
                    ROUTES[i][j] = -1;
                }
            }

            //cout << endl << "ITERATION " << iterations << " HAS ENDED!" << endl << endl;
        }
    }

    private double distance(int cityi, int cityj) {
        return Math.sqrt(Math.pow(CITIES[cityi][0] - CITIES[cityj][0], 2)
                + Math.pow(CITIES[cityi][1] - CITIES[cityj][1], 2));
    }

    private boolean exists(int cityi, int cityc) {
        return GRAPH[cityi][cityc] == 1;
    }

    private boolean visited(int antk, int c) {
        for (int l = 0; l < NUMBEROFCITIES; l++) {
            if (ROUTES[antk][l] == -1) {
                break;
            }
            if (ROUTES[antk][l] == c) {
                return true;
            }
        }
        return false;
    }

    private double PHI(int cityi, int cityj, int antk) {
        double ETAij = Math.pow(1 / distance(cityi, cityj), BETA);
        double TAUij = Math.pow(PHEROMONES[cityi][cityj], ALPHA);

        double sum = 0.0;
        for (int c = 0; c < NUMBEROFCITIES; c++) {
            if (exists(cityi, c)) {
                if (!visited(antk, c)) {
                    double ETA = Math.pow(1 / distance(cityi, c), BETA);
                    double TAU = Math.pow(PHEROMONES[cityi][c], ALPHA);
                    sum += ETA * TAU;
                }
            }
        }
        return (ETAij * TAUij) / sum;
    }

    private double length(int antk) {
        double sum = 0.0;
        for (int j = 0; j < NUMBEROFCITIES; j++) {
            if (j == NUMBEROFCITIES - 1) {
                sum += distance(ROUTES[antk][j], ROUTES[antk][0]);
            } else {
                sum += distance(ROUTES[antk][j], ROUTES[antk][j + 1]);
            }
        }
        return sum;
    }

    private int city() {
        double xi = randoms.Uniforme();
        int i = 0;
        double sum = PROBS[i][0];
        while (sum < xi) {
            i++;
            sum += PROBS[i][0];
        }
        return (int) PROBS[i][1];
    }

    /**
     *
     * @param antk
     */
    private void route(int antk) {
        ROUTES[antk][0] = INITIALCITY;
        for (int i = 0; i < NUMBEROFCITIES - 1; i++) {
            int cityi = ROUTES[antk][i];
            int count = 0;
            for (int c = 0; c < NUMBEROFCITIES; c++) {
                if (cityi == c) {
                    continue;
                }
                if (exists(cityi, c)) {
                    if (!visited(antk, c)) {
                        PROBS[count][0] = PHI(cityi, c, antk);
                        PROBS[count][1] = (double) c;
                        count++;
                    }

                }
            }

            // deadlock
            if (0 == count) {
                return;
            }

            ROUTES[antk][i + 1] = city();
        }
    }

    /**
     * check the route of an ant
     * @param antk
     * @param iteration
     * @return -1 means that there exists city not being reached;
     * -2 means the path between not exist
     * -3 means reach a city which has been reached already;
     * -4 means the end of the trail not connected with source;
     */
    private int valid(int antk, int iteration) {
        for (int i = 0; i < NUMBEROFCITIES - 1; i++) {
            int cityi = ROUTES[antk][i];
            int cityj = ROUTES[antk][i + 1];
            if (cityi < 0 || cityj < 0) {
                return -1;
            }
            if (!exists(cityi, cityj)) {
                return -2;
            }
            for (int j = 0; j < i - 1; j++) {
                if (ROUTES[antk][i] == ROUTES[antk][j]) {
                    return -3;
                }
            }
        }

        if (!exists(INITIALCITY, ROUTES[antk][NUMBEROFCITIES - 1])) {
            return -4;
        }

        return 0;
    }

    private void updatePHEROMONES() {
        for (int k = 0; k < NUMBEROFANTS; k++) {
            double rlength = length(k); // current path length for antk
            for (int r = 0; r < NUMBEROFCITIES - 1; r++) {
                int cityi = ROUTES[k][r];
                int cityj = ROUTES[k][r + 1];
                DELTAPHEROMONES[cityi][cityj] += Q / rlength;
                DELTAPHEROMONES[cityj][cityi] += Q / rlength;
            }
        }
        for (int i = 0; i < NUMBEROFCITIES; i++) {
            for (int j = 0; j < NUMBEROFCITIES; j++) {
                PHEROMONES[i][j] = (1 - RO) * PHEROMONES[i][j] + DELTAPHEROMONES[i][j];
                DELTAPHEROMONES[i][j] = 0.0;
            }
        }
    }
}
