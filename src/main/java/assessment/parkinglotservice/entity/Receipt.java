package assessment.parkinglotservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Receipt {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "SPACE_NUMBER")
    private String spaceNumber;
    @Column(name = "LICENSE_PLATE")
    private String licensePlate;
    @Column(name = "TIME_ENTER")
    private LocalDateTime timeEnter;
    @Column(name = "TIME_EXIT")
    private LocalDateTime timeExit;

    public Receipt() {
        //Needed by the JPA spec
    }

    public Receipt(String spaceNumber, String licensePlate) {
        this.spaceNumber = spaceNumber;
        this.licensePlate = licensePlate;
        timeEnter = LocalDateTime.now();
        timeExit = null;
    }

    public Long getId() {
        return id;
    }

    public String getSpaceNumber() {
        return spaceNumber;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public LocalDateTime getTimeEnter() {
        return timeEnter;
    }

    public LocalDateTime getTimeExit() {
        return timeExit;
    }

    public void setTimeExit(LocalDateTime timeExit) {
        this.timeExit = timeExit;
    }
}
