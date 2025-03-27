package componentes;

public class RoadPlace {
    private String road;
    private int km;
    
    public RoadPlace(String road, int km) {
        this.road = road;
        this.km = km;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }


}
