package ervikenterprises.app;

import java.util.List;

/**
 * Solution class for checking validity and fitness of an individual
 */
public class Solution {
    private List<List<String>> solution;
    private Problem problem;
    private double fitness = 0;
    private boolean isValid;

    /**
     * @param solution
     * @param problem
     */
    public Solution(List<List<String>> solution, Problem problem) {
        this.solution = solution;
        this.problem = problem;
        this.isValid = isValid();
        setFitness();
    }

    public void newGen() {
        this.isValid = isValid();
        setFitness();
    }

    public List<List<String>> getSolution() {
        return solution;
    }

    private void setFitness() {
        for (List<String> list : solution) {
            int prev_patient = 0;
            for (int i = 0; i < list.size() ; i++) {
                fitness += problem.getTravel_times()[prev_patient][Integer.parseInt(list.get(i))];
                prev_patient = Integer.parseInt(list.get(i));
            }
            fitness += problem.getTravel_times()[prev_patient][0];
        }
        if (!isValid) { fitness *= 1000;}
    }

    public void setSolution(List<List<String>> solution) {
        this.solution = solution;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public boolean isValid() {
        for (List<String> nurse : solution) {
            double travel = 0;
            int capacity = 0;
            int prev_patient = 0;
            for (String patientStr : nurse) {
                patient patient = problem.getPatients().get(patientStr);
                // travel
                travel += problem.getTravel_times()[prev_patient][Integer.parseInt(patientStr)];
                //Wait
                if (travel < patient.getStart_time()) {
                    travel = patient.getStart_time();
                }
                //Check if before end time 
                if (travel + patient.getCare_time() > patient.getEnd_time()) {
                    return false;
                }
                travel += patient.getCare_time();
                capacity += patient.getDemand();

                //check if overworked
                if (capacity > problem.getCapacity_nurse()) {
                    return false;
                }
                if (travel > problem.getDepot().getReturn_time()) {
                    return false;
                }

                prev_patient = Integer.parseInt(patientStr);
            }

            travel += problem.getTravel_times()[prev_patient][0];
            if (travel > problem.getDepot().getReturn_time()) {
                return false;
            }
            
        }
        return true;
    }


    public double getFitness() {
        return fitness;
    }

    @Override
    public String toString() {
        return "Solution [solution=" + solution + ", problem=" + problem + ", fitness=" + fitness + ", isValid="
                + isValid + "]";
    }

    

    
}
