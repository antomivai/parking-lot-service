package assessment.parkinglotservice.service;

import assessment.parkinglotservice.model.SpaceSize;
import assessment.parkinglotservice.model.Vehicle;
import assessment.parkinglotservice.model.VehicleType;
import assessment.parkinglotservice.entity.Receipt;
import assessment.parkinglotservice.exception.NoParkingSpaceAvailableException;
import assessment.parkinglotservice.exception.VehicleTypeNotSupportedException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ParkingLot {

    final private int MAX_CYCLE_SPACE;
    final private int MAX_COMPACT_SPACE;
    final private int MAX_REGULAR_SPACE;
    final private int COMPOSITE_REGULAR_SPACE_SIZE = 3;     //Specifies the number of Regular space requires to park an oversized vehicle

    Map<String, Vehicle> occupiedMotorcycleSpaces = new HashMap<>();
    Map<String, Vehicle> occupiedCompactSpaces = new HashMap<>();
    Map<String, Vehicle> occupiedRegularSpaces = new HashMap<>();
    public ParkingLot(int maxCycleSpace, int maxCompactSpace, int maxRegularSpace) {
        MAX_CYCLE_SPACE = maxCycleSpace;
        MAX_COMPACT_SPACE = maxCompactSpace;
        MAX_REGULAR_SPACE = maxRegularSpace;
    }

    /**
     * Description: For simplicity, motorcycle must park in motorcycle space,
     * car can park in compact and regular, and van can park in 3 regular spaces;
     * @param vehicle
     * @return
     */
    public Receipt parkVehicle(Vehicle vehicle) {
        if(vehicle.type() == VehicleType.MOTORCYCLE) {
            if(getAvailableMotorcycleSpaces() > 0) {
                String spaceNumber = "M" + occupiedMotorcycleSpaces.size() + 1;
                occupiedMotorcycleSpaces.put(spaceNumber, vehicle);
                return new Receipt(spaceNumber, vehicle.licensePlate());
            } else {
                throw new NoParkingSpaceAvailableException();
            }
        } else if(vehicle.type() == VehicleType.CAR) {
            if(getAvailableCompactSpaces() > 0) {
                String spaceNumber = "C" + occupiedCompactSpaces.size() + 1;
                occupiedCompactSpaces.put(spaceNumber, vehicle);
                return new Receipt(spaceNumber, vehicle.licensePlate());
            } else if(getAvailableRegularSpaces() > 0) {
                String spaceNumber = "R" + occupiedRegularSpaces.size() + 1;
                occupiedRegularSpaces.put(spaceNumber, vehicle);
                return new Receipt(spaceNumber, vehicle.licensePlate());
            } else {
                throw new NoParkingSpaceAvailableException();
            }
        } else if(vehicle.type() == VehicleType.VAN) {
            if(getAvailableRegularSpaces() >= COMPOSITE_REGULAR_SPACE_SIZE) {
                int initialSpaceNum = occupiedRegularSpaces.size();
                String compositeSpaceNumber = "";
                for(int i = 1; i <= COMPOSITE_REGULAR_SPACE_SIZE; i++) {
                    String spaceNumber1 = "R" + initialSpaceNum + i;
                    occupiedRegularSpaces.put(spaceNumber1, vehicle);

                    if(compositeSpaceNumber.isEmpty()) {
                        compositeSpaceNumber = compositeSpaceNumber.concat(spaceNumber1);
                    } else {
                        compositeSpaceNumber = compositeSpaceNumber.concat("-" + spaceNumber1);
                    }
                }

                return new Receipt(compositeSpaceNumber, vehicle.licensePlate());
            } else {
                throw new NoParkingSpaceAvailableException();
            }
        } else {
            throw new VehicleTypeNotSupportedException();
        }
    }

    public Receipt unparkVehicle(Receipt receipt) {
        if(receipt.getTimeExit() == null) {
            //Set the exit time if it is not defined.
            //If exit time is already defined then that means someone is trying to unpark a vehicle that have previously exited the parking lot.
            //In this case, just return the previous receipt.
            receipt.setTimeExit(LocalDateTime.now());

            SpaceSize spaceSize = determineSpaceSize(receipt.getSpaceNumber());

            if(spaceSize == SpaceSize.MOTOCYCLE) {
                Vehicle vehicle = occupiedMotorcycleSpaces.get(receipt.getSpaceNumber());
                if(vehicle != null && vehicle.licensePlate().equals(receipt.getLicensePlate())) {
                    occupiedMotorcycleSpaces.remove(receipt.getSpaceNumber());
                }
            } else if(spaceSize == SpaceSize.COMPACT) {
                Vehicle vehicle = occupiedCompactSpaces.get(receipt.getSpaceNumber());
                if(vehicle != null && vehicle.licensePlate().equals(receipt.getLicensePlate())) {
                    occupiedCompactSpaces.remove(receipt.getSpaceNumber());
                }
            } else if(spaceSize == SpaceSize.REGULAR) {
                Vehicle vehicle = occupiedRegularSpaces.get(receipt.getSpaceNumber());
                if(vehicle != null && vehicle.licensePlate().equals(receipt.getLicensePlate())) {
                    occupiedRegularSpaces.remove(receipt.getSpaceNumber());
                }
            } else if(spaceSize == SpaceSize.COMPOSITE) {
                String compositeSpaceNumber = receipt.getSpaceNumber();
                for (String spaceNumber : compositeSpaceNumber.split("-")) {
                    Vehicle vehicle = occupiedRegularSpaces.get(spaceNumber);
                    if(vehicle != null && vehicle.licensePlate().equals(receipt.getLicensePlate())) {
                        occupiedRegularSpaces.remove(spaceNumber);
                    }
                }
            }
        }
        return receipt;
    }

    /**
     * Description: Determine the number of available parking spaces left for the given vehicle type.
     * @param vehicleType
     * @return
     */
    public int getAvailableSpace(VehicleType vehicleType) {
        if(vehicleType == VehicleType.MOTORCYCLE) {
            return getAvailableMotorcycleSpaces();
        } else if(vehicleType == VehicleType.CAR) {
            return getAvailableCompactSpaces() + getAvailableRegularSpaces();
        } else if(vehicleType == VehicleType.VAN) {
            //Since Van
            return getAvailableRegularSpaces() / COMPOSITE_REGULAR_SPACE_SIZE;
        } else {
            return 0;
        }
    }

    /**
     * Description: Determine the size of the parking space based on the space number.
     * @param spaceNumber
     * @return
     */
    private SpaceSize determineSpaceSize(String spaceNumber) {
        if(spaceNumber.contains("M")) {
            return SpaceSize.MOTOCYCLE;
        } else if(spaceNumber.contains("C")) {
            return SpaceSize.COMPACT;
        } else if(spaceNumber.contains("R") && spaceNumber.contains("-")) {
            return SpaceSize.COMPOSITE;
        } else if(spaceNumber.contains("R")) {
            return SpaceSize.REGULAR;
        } else {
            return SpaceSize.UNKNOWN;
        }
    }

    public int getAvailableSpaces() {
        return MAX_CYCLE_SPACE - occupiedMotorcycleSpaces.size() + MAX_COMPACT_SPACE - occupiedCompactSpaces.size() + MAX_REGULAR_SPACE - occupiedRegularSpaces.size();
    }

    public int getAvailableMotorcycleSpaces() {
        return MAX_CYCLE_SPACE - occupiedMotorcycleSpaces.size();
    }

    public int getAvailableCompactSpaces() {
        return MAX_COMPACT_SPACE - occupiedCompactSpaces.size();
    }

    public int getAvailableRegularSpaces() {
        return MAX_REGULAR_SPACE - occupiedRegularSpaces.size();
    }

    /**
     * Description: Use for the purpose of unit testing only.  Use to reset to parkingLot for the next test.
     */
    public void reset() {
        occupiedMotorcycleSpaces.clear();
        occupiedCompactSpaces.clear();
        occupiedRegularSpaces.clear();
    }
}
