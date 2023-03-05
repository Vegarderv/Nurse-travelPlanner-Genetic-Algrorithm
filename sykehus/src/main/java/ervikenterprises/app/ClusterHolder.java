package ervikenterprises.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.gson.Gson;


/**
 * Used as one individual in the GA
 */
public class ClusterHolder {

    private List<Cluster> clusters;
    private Solution solution;
    private Problem problem;

    /**
     * Initialize new Clusterholder
     * @param clusters
     * @param problem
     */
    public ClusterHolder(List<Cluster> clusters, Problem problem) {
        this.clusters = clusters;
        this.problem = problem;
        generateSolution();
    }

    /**
     * Deep Copy
     * @param another
     */
    public ClusterHolder(ClusterHolder another) {
        this.problem = another.problem;
        this.solution = another.solution;
        this.clusters = another.clusters.stream().map(clust -> new Cluster(clust)).collect(Collectors.toList());
    }

    /**
     * Do random mutation with chance p
     * @param chance
     */
    public void localMutate(double chance) {
        for (Cluster cluster : clusters) {
            double random = new Random().nextDouble();
            if (chance > random) {
                chooseMutation(cluster);
            }
        }
        generateSolution();
    }

    /**
     * 
     * @return random cluster from within clusterholder
     */
    private Cluster getRandomCluster() {
        int pos = new Random().nextInt(clusters.size());
        return clusters.get(pos);
    }

    /**
     * Does one out of four clusters, with equal chance
     * @param cluster cluster to mutate
     */
    private void chooseMutation(Cluster cluster) {
        int i = new Random().nextInt(4);
        if (i == 0) {
            cluster.swapPatients();
        } else if (i == 1) {
            swapPatients(cluster, getRandomCluster());
        } else if (i == 2) {
            addPatient(getRandomCluster(), cluster);
        } else {
            largeNHS(cluster, getRandomCluster());
        }
    }

    /**
     * Mutate cluster by swapping two random patients from two routes
     * @param c1 route 1
     * @param c2 route 2
     */
    private void swapPatients(Cluster c1, Cluster c2) {
        if (c1.getPatients().size() == 0 || c2.getPatients().size() == 0) {
            return;
        }
        patient patient = c1.popPatient();
        c2.addPatientRandom(patient);
        patient = c2.popPatient();
        c1.addPatientRandom(patient);
    }

    /**
     * Transfers patients from one route to another
     * @param c1 Route patient transfers from
     * @param c2 Route patient transfers to
     */
    private void addPatient(Cluster c1, Cluster c2) {
        if (c1.getPatients().size() == 0) {
            return;
        }
        patient patient = c1.popPatient();
        c2.addPatient(patient);
    }

    /**
     * Does Large Neighborhood Search by finding the longest travel time between two patients
     * within a route, and sending all patients from either before that travel or after that travel
     * to a random place in another route
     * @param c1 Route patients transfers from
     * @param c2 Route patients transfer to
     */
    private void largeNHS(Cluster c1, Cluster c2) {
        if (c1.getPatients().size() == 0) {
            return;
        }
        List<patient> patients = new ArrayList<>(c1.getPatients());
        int i = 0;
        // Finds index of largest travel
        for (int j = 0; j < patients.size() - 1; j++) {
            i = problem.getTravel_times()[Integer.parseInt(patients.get(i).getName())][Integer
                    .parseInt(patients.get(i + 1).getName())] > problem.getTravel_times()[Integer
                            .parseInt(patients.get(j).getName())][Integer.parseInt(patients.get(j + 1).getName())] ? i
                                    : j;

        }
        if (new Random().nextDouble() > 0.5) {
            c1.removePatients(patients.subList(i + 1, patients.size()));
            c2.addPatientsRandom(patients.subList(i + 1, patients.size()));
        } else {
            c1.removePatients(patients.subList(0, i + 1));
            c2.addPatientsRandom(patients.subList(0, i + 1));
        }
    }

    /**
     * Does crossover with another route.
     * Details in slides
     * @param other RouteCollection to do crossover with
     */
    public void crossover(ClusterHolder other) {
        List<patient> patients = new ArrayList<>(
                clusters.get(new Random().nextInt(problem.getNbr_nurses())).getPatients());
        List<patient> patients2 = new ArrayList<>(
                other.clusters.get(new Random().nextInt(problem.getNbr_nurses())).getPatients());
        Collections.shuffle(patients);
        Collections.shuffle(patients2);
        removePatients(patients2);
        other.removePatients(patients);
        assignPatients(patients2);
        other.assignPatients(patients);

    }

    /**
     * Removes patients from route
     * @param patients
     */
    public void removePatients(List<patient> patients) {
        clusters.forEach(cluster -> cluster.removePatientsName(patients));
    }

    /**
     * Assigns patients one at a time to the optimal spot in the routes
     * @param patients
     */
    public void assignPatients(List<patient> patients) {
        for (patient patient : patients) {
            int bestCluster = -1;
            int bestPos = -1;
            double bestFitness = 99999999;
            List<List<String>> SolLists = clusters.stream().map(cluster -> cluster.toSolution())
                    .collect(Collectors.toList());
            for (int i = 0; i < SolLists.size(); i++) {
                for (int j = 0; j < SolLists.get(i).size() + 1; j++) {
                    SolLists.get(i).add(j, patient.getName());
                    double fitness = new Solution(SolLists, problem).getFitness();
                    if (fitness < bestFitness) {
                        bestCluster = i;
                        bestPos = j;
                        bestFitness = fitness;
                    }
                    SolLists.get(i).remove(j);
                }
            }
            clusters.get(bestCluster).addPatient(bestPos, patient);
        }
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = clusters;
    }

    public void generateSolution() {
        solution = new Solution(clusters.stream().map(cluster -> cluster.toSolution()).toList(), problem);
    }

    public Solution getSolution() {
        return solution;
    }

    @Override
    public String toString() {
        // return "ClusterHolder [clusters=" + clusters + ", solution=" + solution +
        // "]";
        // return toPython();
        // return Integer.toString(solution.getFitness());
        return "Length: " + clusters.stream().map(cluster -> cluster.getPatients().size()).reduce(0, (a, b) -> a + b);
    }

    public String toPython() {
        List<List<List<Integer>>> points = clusters
                .stream().map(clust -> clust.getPatients().stream()
                        .map(pat -> Arrays.asList(pat.getX_coord(), pat.getY_coord())).toList())
                .toList();
        return new Gson().toJson(points);
    }

}
