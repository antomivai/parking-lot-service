package assessment.parkinglotservice.model;

public class Space {
    final private String number;
    final SpaceSize size;

    public Space(String number, SpaceSize size) {
        this.number = number;
        this.size = size;
    }
}
