package assessment.parkinglotservice.dto;

import assessment.parkinglotservice.model.VehicleType;

public record AvailableVehicleSpaces(VehicleType vehicleType, int availableSpaces) {
}
