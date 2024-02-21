package assessment.parkinglotservice.repository;

import assessment.parkinglotservice.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    @Query("SELECT r FROM Receipt r WHERE (r.licensePlate = :licensePlate) AND (r.timeExit is null)")
    public Optional<Receipt> findVehicleInParkingLot(String licensePlate);
}
