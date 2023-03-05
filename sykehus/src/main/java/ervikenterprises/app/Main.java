package ervikenterprises.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

/**
 * Main Class for Visma Resolve Nurse task
 * Made using a Genetic Algorithm
 * 
 * Made by vegaer
 */
public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        // Loading Problem
        Path filePath = Path.of("Instances to Project 2/train_9.json");

        String content = "";
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Gson gson = new Gson();

        Problem problem = gson.fromJson(content, new Problem().getClass());


        // Initializing first threads, Clusters initialized with K-means
        List<GA> gas = new ArrayList<>();
        for (int i = 0; i < Enviroment.THREADS; i++) {
            gas.add(new GA(problem, false, i));
            gas.get(i).start();   
        }

        //Waiting for threads to finish
        while (gas.stream().anyMatch(ga -> ga.getState()!=Thread.State.TERMINATED)){
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Checking thread fitnesses
        System.out.println("\n\nTHREAD FITNESSES:\n");
        gas.stream().forEach(ga -> System.out.println(ga.getSolution(false).get(ga.getSolution(false).size() - 1).getSolution().getFitness()));

        // Combining all threads to one Genetic Algorithm, clusters initialized with the best thread clusters
        List<ClusterHolder> clusters = new ArrayList<>();
        gas.stream().forEach(ga -> clusters.addAll(ga.getSolution(false)));

        Collections.shuffle(clusters);

        gas = new ArrayList<>();

        for (int i = 0; i < Enviroment.THREADS / 2; i++) {
            gas.add(new GA(problem, false, i));
            gas.get(i).start();  
        }
        int ratio = Enviroment.POPULATION_SIZE / Enviroment.THREADS * 2;
        for (int i = 0; i < Enviroment.THREADS / 2; i++) {
            gas.add(new GA(problem, clusters.subList(i * ratio, i * ratio + ratio)));
            gas.get(i + Enviroment.THREADS / 2).start();  
        }

        //Waiting for threads to finish
        while (gas.stream().anyMatch(ga -> ga.getState()!=Thread.State.TERMINATED)){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Checking thread fitnesses
        System.out.println("\n\nTHREAD FITNESSES:\n");
        gas.stream().forEach(ga -> System.out.println(ga.getSolution(false).get(ratio/2 - 1).getSolution().getFitness()));

        // Combining all threads to one Genetic Algorithm, clusters initialized with the best thread clusters
        List<ClusterHolder> clusters2 = new ArrayList<>();
        gas.stream().forEach(ga -> clusters2.addAll(ga.getSolution(false)));


        GA last = new GA(problem, clusters2);
        last.run();

        System.out.println("\n\nSCORE BEFORE FINAL STRETCH:");
        System.out.println(last.getSolution(false).get(ratio/2-1).getSolution().getFitness() + "\n");
        GA finalGa = new GA(problem, last.getSolution(true), false);

        while((System.currentTimeMillis() - start) / 1000 < 290 ) {
            finalGa.run();
        }

        Path writePath = Path.of("solutions/solution_1.json");

        try {
            Files.write(writePath, finalGa.getSolution(false).get(ratio/2-1).toPython().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Printing solution and fitness
        System.out.println("\nSOLUTION:\n");
        System.out.println(finalGa.getSolution(false).get(ratio / 2 - 1).getSolution().getSolution());
        System.out.println("\nFITNESS:\n");
        System.out.println(finalGa.getSolution(false).get(ratio / 2 - 1).getSolution().getFitness());

        long end = System.currentTimeMillis();
        System.out.println("TIME PASSED: "  + (end-start) / 1000 + "s");
    }
}
