package assessment.parkinglotservice.controller;

import assessment.parkinglotservice.repository.ReceiptRepository;
import assessment.parkinglotservice.model.Vehicle;
import assessment.parkinglotservice.model.VehicleType;
import assessment.parkinglotservice.dto.AvailableSpaces;
import assessment.parkinglotservice.dto.AvailableVehicleSpaces;
import assessment.parkinglotservice.entity.Receipt;
import assessment.parkinglotservice.service.ParkingLot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/parkinglot")
public class ParkingLotController {
    @Autowired
    ReceiptRepository receiptRepository;

    //TODO make the initialization of the ParkingLot configurable
    final private ParkingLot parkingLot = new ParkingLot(5, 10, 10);

    /**
     * Description: Use for the purpose of integration testing only
     */
    @GetMapping("/reset")
    public void resetParkingLot() {
        //TODO: Find a better way to rollback changes for testing purposes
        parkingLot.reset();
    }

    @PostMapping("/park")
    public Receipt parkVehicle(@RequestBody Vehicle vehicle) {
        Optional<Receipt> activeReceipt = receiptRepository.findVehicleInParkingLot(vehicle.licensePlate());
        if(activeReceipt.isPresent()) {
            return activeReceipt.get();
        } else {
            Receipt newReceipt = parkingLot.parkVehicle(vehicle);
            return receiptRepository.save(newReceipt);
        }
    }

    @GetMapping("/unpark/{receiptId}")
    public Receipt unparkVehicle(@PathVariable Long receiptId) {
        Optional<Receipt> existingReceipt = receiptRepository.findById(receiptId);
        if(existingReceipt.isPresent()) {
            Receipt receipt = parkingLot.unparkVehicle(existingReceipt.get());
            return receiptRepository.save(receipt);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/spaces")
    public AvailableSpaces getAvailableParkingSpaces() {
        return new AvailableSpaces(parkingLot.getAvailableMotorcycleSpaces(), parkingLot.getAvailableCompactSpaces(), parkingLot.getAvailableRegularSpaces());
    }

    @GetMapping("/spaces/{vehicleType}")
    public AvailableVehicleSpaces getAvailableParkingSpacesForVehicleType(@PathVariable VehicleType vehicleType) {
        int availSpace = parkingLot.getAvailableSpace(vehicleType);
        return new AvailableVehicleSpaces(vehicleType,availSpace);
    }
}
