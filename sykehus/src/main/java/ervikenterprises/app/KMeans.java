package ervikenterprises.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.lang.Math;


/**
 * Kmeans for initializing an individual
 */
public class KMeans {
    
    private int k;
    private List<patient> coordinates;
    private List<Cluster> clusters;
    private int x_min;
    private int x_max;
    private int y_min;
    private int y_max;
    private int repetitions;
    private boolean legal;


    public KMeans(int k, List<patient> coordinates, int repetitions, boolean legal) {
        this.k = k;
        this.coordinates = coordinates;
        this.repetitions = repetitions;
        this.legal = legal;
        generateMap();
    }

    private void generateMap() {
        clusters = new ArrayList<>();
        x_min = coordinates.stream().map(p -> p.getX_coord()).sorted().toList().get(0);
        x_max = coordinates.stream().map(p -> p.getX_coord()).sorted().toList().get(coordinates.size() - 1);
        y_min = coordinates.stream().map(p -> p.getY_coord()).sorted().toList().get(0);
        y_max = coordinates.stream().map(p -> p.getY_coord()).sorted().toList().get(coordinates.size() - 1);
        for (int i = 0; i < k; i++) {
            double x_pos = new Random().nextDouble() * (x_max - x_min) + x_min;
            double y_pos = new Random().nextDouble() * (y_max - y_min) + y_min;
            clusters.add(new Cluster(x_pos, y_pos));
        }
    }

    private void clust() {
        // Reset Cluster
        clusters.stream().forEach(clus -> clus.reset());
        // Assign patient to cluster
        for (patient patient : coordinates) {
            List<Double> lengths = clusters.stream()
            .map(clus -> Math.abs(clus.getX() - patient.getX_coord()) + Math.abs(clus.getY() - patient.getY_coord()))
            .collect(Collectors.toList());
            //Index of min value
            int minIdx = IntStream.range(0,lengths.size())
            .reduce((i,j) -> lengths.get(i) > lengths.get(j) ? j : i)
            .getAsInt();
            clusters.get(minIdx).addPatient(patient);
        }

        // Recalculate cluster centroids
        for (Cluster cluster : clusters) {
            double x_center = cluster.getPatients().stream().map(pat -> pat.getX_coord()).mapToDouble(a -> a).average().orElse(0);
            double y_center = cluster.getPatients().stream().map(pat -> pat.getY_coord()).mapToDouble(a -> a).average().orElse(0);
            cluster.setX(x_center);
            cluster.setY(y_center);
        }
            
    }

    private void sortClusters() {
        clusters.stream().forEach(cluster -> cluster.sortPatients());
    }

    public ClusterHolder run(Problem problem) {
        for (int i = 0; i < repetitions; i++) {
            clust();
        }
        sortClusters();
        return new ClusterHolder(clusters, problem, legal); 
    }




    
}
