package ervikenterprises.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genetic Algorithm
 */
public class GA extends Thread{
    private Problem problem;
    private List<ClusterHolder> clusters;
    private List<ClusterHolder> parents;
    private List<Double> bestFitnessOverTime;
    private boolean cross;
    private int number;
    private boolean progress;
    private final int maxGen;

    /**
     * Initalize with K-means
     * 
     * @param problem Nurse problem
     * @param crossover Enable crossover? Warning: Slow
     * @param number Thread number
     */
    public GA(Problem problem, boolean crossover, int number) {
        this.problem = problem;
        bestFitnessOverTime = new ArrayList<>();
        clusters = new ArrayList<>();
        init();
        this.cross = crossover;
        this.number = number;
        maxGen = Enviroment.MAX_GENERATIONS;
        System.out.println("THREAD " + this.number + " IS RUNNING");
    }



    /**
     * Initialize GA with existing clusters
     * 
     * @param problem Nurse problem
     * @param clusters Previous clusters
     */
    public GA(Problem problem, List<ClusterHolder> clusters) {
        this.problem = problem;
        bestFitnessOverTime = new ArrayList<>();
        this.clusters = new ArrayList<>(clusters);
        init();
        this.cross = true;
        maxGen = Enviroment.MAX_GEN_FINAL;
        this.progress = true;
    }

    public GA(Problem problem, List<ClusterHolder> clusters, boolean crossover) {
        this.problem = problem;
        bestFitnessOverTime = new ArrayList<>();
        this.clusters = new ArrayList<>(clusters);
        init();
        this.cross = crossover;
        maxGen = Enviroment.MAX_GEN_FINAL;
        this.progress = crossover;
    }


    /**
     * Initialize clusters with K-means
     */
    private void init() {
        problem.init();
        int end = clusters.size();
        for (int i = 0; i < Enviroment.POPULATION_SIZE - end; i++) {
            clusters.add(new KMeans(new Random().nextInt(problem.getNbr_nurses() - 1) + 1, problem.getPatientsList(), new Random().nextInt(10) + 1).run(problem));
            for (ClusterHolder cHolder : clusters) {
                while (cHolder.getClusters().size() < problem.getNbr_nurses()) {
                    cHolder.getClusters().add(new Cluster(0, 0));
                }
            }
        }
    }

    // Mutate
    private void mutation() {
        parents.forEach(cluster -> cluster.localMutate(Enviroment.MUTATION_RATE));
    }

    // Crossbow
    private void crossover() {
        if (cross) {
            for (ClusterHolder clusterHolder : parents) {
                double chance = new Random().nextDouble();
                if (chance > Enviroment.CROSSOVER_RATE) {
                    clusterHolder.crossover(clusters.get(new Random().nextInt(clusters.size())));
                }
            }
        }
    }


    // Choose 1 parent from chance table
    private void chooseParent(List<Double> ranking) {
        double cumulativeChance = 0;
        double winner = new Random().nextDouble();
        for (int j = 0; j < Enviroment.POPULATION_SIZE; j++) {
            if (cumulativeChance + ranking.get(j) > winner) {
                parents.add(new ClusterHolder(clusters.get(j)));
                break;
            }
            cumulativeChance += ranking.get(j);
        }
    }


    // Choose n best parents with ranking. Stochastic
    private void chooseParents() {
        this.parents = new ArrayList<>();
        clusters.sort((c1, c2) -> Double.compare(c2.getSolution().getFitness(), c1.getSolution().getFitness()));
        bestFitnessOverTime.add(clusters.get(Enviroment.POPULATION_SIZE - 1).getSolution().getFitness());
        List<Double> ranking = new ArrayList<>();
        for (int i = 0; i < Enviroment.POPULATION_SIZE; i++) {
            ranking.add((2-Enviroment.S_FACTOR) / Enviroment.POPULATION_SIZE + 2*i*(Enviroment.S_FACTOR-1)/(Enviroment.POPULATION_SIZE*(Enviroment.POPULATION_SIZE-1)));
        }
        for (int i = 0; i < Enviroment.POPULATION_SIZE  - Enviroment.GG; i++) {
            chooseParent(ranking);
        }
    }

    // One generation in GA
    private void iteration() {
        chooseParents();
        mutation();
        crossover();
        parents.addAll(clusters.subList(Enviroment.POPULATION_SIZE - Enviroment.GG, Enviroment.POPULATION_SIZE));
        clusters = parents;
    }


    // Starts thread
    public void run() {
        if (progress) {System.out.println("\nPROGRESS:");}
        for (int i = 0; i < maxGen; i++) {
            if (progress) {System.out.print("\r" + i + "/" + maxGen);}
            iteration();
            //System.out.println("NEW GENERATION! Best individual fitness = " + bestFitnessOverTime.get(i));            
        }
        // System.out.println(this.clusters.get(Enviroment.POPULATION_SIZE - 1).getSolution().getSolution());
        // System.out.println(this.bestFitnessOverTime);
    }


    /**
     * Returns solution
     * @return n best individuals
     */
    public List<ClusterHolder> getSolution(boolean all) {
        if (all) {
            return new ArrayList<>(clusters);
        }
        return clusters.subList(Enviroment.POPULATION_SIZE - Enviroment.POPULATION_SIZE / Enviroment.THREADS, Enviroment.POPULATION_SIZE);
    }

    @Override
    public String toString() {
        return Integer.toString(clusters.stream().map(cluser -> cluser.getClusters().stream().map(clu -> clu.getPatients().size()).reduce(0, (a,b) -> a+b)).reduce(0, (a,b) -> a + b));
        //return "GA [clusters=" + clusters.get(0) + "]";
    }



    

}
