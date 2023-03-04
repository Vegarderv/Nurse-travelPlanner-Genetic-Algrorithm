package ervikenterprises.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Route within an individual
 */
public class Cluster {

    private double x;
    private double y;
    private List<patient> patients;


    
    /**
     * initialize cluster
     * @param x
     * @param y
     */
    public Cluster(double x, double y) {
        this.x = x;
        this.y = y;
        this.patients = new ArrayList<>();
    }

    /**
     * Deep copy
     * @param another
     */
    public Cluster(Cluster another) {
        this.x = another.x;
        this.y = another.y;
        this.patients = new ArrayList<>(another.patients.stream().map(pat -> new patient(pat, pat.getName())).collect(Collectors.toList()));
    }

    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }

    public patient popPatient() {
        int pos = new Random().nextInt(patients.size());
        patient patient =  patients.get(pos);
        patients.remove(pos);
        return patient;
    }

    public List<patient> getPatients() {
        return patients;
    }

    public void setPatients(List<patient> patients) {
        this.patients = patients;
    }

    public void addPatient(patient patient) {
        this.patients.add(patient);
    }

    public void addPatient(int i, patient patient) {
        this.patients.add(i, patient);
    }

    public void removePatients(List<patient> patients) {
        this.patients.removeAll(patients);
    }

    public void removePatientsName(List<patient> patients) {
        this.patients.removeIf(patient -> patients.stream().map(pat -> pat.getName()).toList().contains(patient.getName()));
    }

    public void addPatientsRandom(List<patient> patients){
        if (this.patients.size() == 0) {
            this.patients.addAll(patients);
            return;
        }
        int index = new Random().nextInt(this.patients.size());
        this.patients.addAll(index, patients);
    }

    public void addPatientRandom(patient patient) {
        if (patients.size() == 0) {
            this.patients.add(patient);
            return;
        }
        int pos = new Random().nextInt(patients.size() );
        this.patients.add(pos, patient);
    }

    public void sortPatients() {
        patients = patients.stream().sorted(Comparator.comparing(patient::getEnd_time)).collect(Collectors.toList());
    }
    
    public List<String> toSolution()  {
        return patients.stream().map(pat -> pat.getName()).collect(Collectors.toList());
    }

    public void swapPatients() {
        if (patients.size() == 0) {return;}
        int pos = new Random().nextInt(patients.size());
        int pos2 = new Random().nextInt(patients.size());
        Collections.swap(patients, pos, pos2); 
    }

    public void reset() {
        this.patients = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Cluster [x=" + x + ", y=" + y + ", patients=" + patients + "]";
    }
    
}
