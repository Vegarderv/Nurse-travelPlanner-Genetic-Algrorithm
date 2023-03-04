package ervikenterprises.app;

public class patient {

    private int demand;
    private int start_time;
    private int end_time;
    private int care_time;
    private int x_coord;
    private int y_coord;
    private String name;

    public patient(patient patient, String name) {
        this.demand = patient.demand;
        this.start_time = patient.start_time;
        this.end_time = patient.end_time;
        this.care_time = patient.care_time;
        this.x_coord = patient.x_coord;
        this.y_coord = patient.y_coord;
        this.name = name;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public int getCare_time() {
        return care_time;
    }

    public void setCare_time(int care_time) {
        this.care_time = care_time;
    }

    public int getX_coord() {
        return x_coord;
    }

    public void setX_coord(int x_coord) {
        this.x_coord = x_coord;
    }

    public int getY_coord() {
        return y_coord;
    }

    public void setY_coord(int y_coord) {
        this.y_coord = y_coord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "patient [demand=" + demand + ", start_time=" + start_time + ", end_time=" + end_time + ", care_time="
                + care_time + ", x_coord=" + x_coord + ", y_coord=" + y_coord + ", name=" + name + "]";
    }

    
}
