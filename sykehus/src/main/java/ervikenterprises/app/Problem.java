package ervikenterprises.app;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains problem description
 */
public class Problem {
    private String instance_name;
    private int nbr_nurses;
    private int capacity_nurse;
    private depot depot;
    private HashMap<String, patient> patients;
    private List<patient> patientsList;
    private double[][] travel_times;


    public void init() {
        patientsList = patients.keySet()
        .stream()
        .map(pat -> new patient(patients.get(pat), pat))
                .collect(Collectors.toList());
    }

    public String getInstance_name() {
        return instance_name;
    }

    public void setInstance_name(String instance_name) {
        this.instance_name = instance_name;
    }

    public int getNbr_nurses() {
        return nbr_nurses;
    }

    public void setNbr_nurses(int nbr_nurses) {
        this.nbr_nurses = nbr_nurses;
    }

    public int getCapacity_nurse() {
        return capacity_nurse;
    }

    public void setCapacity_nurse(int capacity_nurse) {
        this.capacity_nurse = capacity_nurse;
    }

    public depot getDepot() {
        return depot;
    }

    public void setDepot(depot depot) {
        this.depot = depot;
    }

    public HashMap<String, patient> getPatients() {
        return patients;
    }

    public void setPatients(HashMap<String, patient> patients) {
        this.patients = patients;
    }

    public double[][] getTravel_times() {
        return travel_times;
    }

    public void setTravel_times(double[][] travel_times) {
        this.travel_times = travel_times;
    }

    public List<patient> getPatientsList() {
        return patientsList;
    }

    public void setPatientsList(List<patient> patientsList) {
        this.patientsList = patientsList;
    }

    

}
