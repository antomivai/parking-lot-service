package assessment.parkinglotservice;

import assessment.parkinglotservice.entity.Receipt;
import assessment.parkinglotservice.exception.NoParkingSpaceAvailableException;
import assessment.parkinglotservice.model.Vehicle;
import assessment.parkinglotservice.model.VehicleType;
import assessment.parkinglotservice.service.ParkingLot;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParkingLotTests {

    private ParkingLot parkingLot;

    @Test
    public void carShouldParkInRegularSpaceWhenCompactSpaceIsNotAvailable() {
        parkingLot = new ParkingLot(1,1, 1);

        Vehicle vehicle1 = new Vehicle("4JCA321", VehicleType.CAR);
        Vehicle vehicle2 = new Vehicle("5JCA321", VehicleType.CAR);
        Receipt receipt1 = parkingLot.parkVehicle(vehicle1);
        Receipt receipt2 = parkingLot.parkVehicle(vehicle2);

        assertThat(receipt1.getSpaceNumber()).isNotEqualTo(receipt2.getSpaceNumber());
        assertThat(parkingLot.getAvailableCompactSpaces()).isEqualTo(0);
        assertThat(parkingLot.getAvailableRegularSpaces()).isEqualTo(0);
    }

    @Test
    public void vanShouldTakeUpThreeRegularSpace() {
        parkingLot = new ParkingLot(1,3,3);

        Vehicle vehicle = new Vehicle("8VAN495", VehicleType.VAN);
        Receipt receipt = parkingLot.parkVehicle(vehicle);

        assertThat(parkingLot.getAvailableRegularSpaces()).isEqualTo(0);
        assertThat(parkingLot.getAvailableCompactSpaces()).isEqualTo(3);
    }

    @Test
    public void carShouldTakeRegularSpaceAfterVanExitLot() {
        parkingLot = new ParkingLot(1,1,3);

        Vehicle vehicle1 = new Vehicle("4JCA321", VehicleType.CAR);
        Vehicle vehicle2 = new Vehicle("8VAN495", VehicleType.VAN);
        Vehicle vehicle3 = new Vehicle("5JCA123", VehicleType.CAR);

        Receipt receipt1 = parkingLot.parkVehicle(vehicle1);
        Receipt receipt2 = parkingLot.parkVehicle(vehicle2);

        try {
            parkingLot.parkVehicle(vehicle3);
        } catch (NoParkingSpaceAvailableException e) {
            Receipt receipt2_1 = parkingLot.unparkVehicle(receipt2);
            assertThat(receipt2_1.getTimeEnter()).isEqualTo(receipt2.getTimeEnter());
            assertThat(receipt2_1.getSpaceNumber()).isEqualTo(receipt2.getSpaceNumber());
            assertThat(receipt2_1.getLicensePlate()).isEqualTo(receipt2.getLicensePlate());

            Receipt receipt3 = parkingLot.parkVehicle(vehicle3);
            assertThat(receipt3.getLicensePlate()).isEqualTo(vehicle3.licensePlate());
            assertThat(parkingLot.getAvailableRegularSpaces()).isEqualTo(2);
            assertThat(parkingLot.getAvailableCompactSpaces()).isEqualTo(0);
        }
    }

    @Test
    public void carShouldTakeCompactSpaceAfterCarExitLot() {
        parkingLot = new ParkingLot(1,1,3);

        Vehicle vehicle1 = new Vehicle("4JCA321", VehicleType.CAR);
        Vehicle vehicle2 = new Vehicle("8VAN495", VehicleType.VAN);
        Vehicle vehicle3 = new Vehicle("5JCA123", VehicleType.CAR);

        Receipt receipt1 = parkingLot.parkVehicle(vehicle1);
        Receipt receipt2 = parkingLot.parkVehicle(vehicle2);

        try {
            parkingLot.parkVehicle(vehicle3);
        } catch (NoParkingSpaceAvailableException e) {
            Receipt receipt1_1 = parkingLot.unparkVehicle(receipt1);
            assertThat(receipt1_1.getTimeEnter()).isEqualTo(receipt1.getTimeEnter());
            assertThat(receipt1_1.getSpaceNumber()).isEqualTo(receipt1.getSpaceNumber());
            assertThat(receipt1_1.getLicensePlate()).isEqualTo(receipt1.getLicensePlate());

            Receipt receipt3 = parkingLot.parkVehicle(vehicle3);
            assertThat(receipt3.getLicensePlate()).isEqualTo(vehicle3.licensePlate());
            assertThat(parkingLot.getAvailableRegularSpaces()).isEqualTo(0);
            assertThat(parkingLot.getAvailableCompactSpaces()).isEqualTo(0);
        }
    }

    @Test
    public void getAvailableSpaceForVehicle() {
        parkingLot = new ParkingLot(1,3,5);

        Vehicle vehicle1 = new Vehicle("4MOT321", VehicleType.MOTORCYCLE);
        Vehicle vehicle2 = new Vehicle("8VAN495", VehicleType.VAN);
        Vehicle vehicle3 = new Vehicle("5JCA123", VehicleType.CAR);

        int availSpace = parkingLot.getAvailableSpace(vehicle1.type());
        assertThat(availSpace).isEqualTo(1);

        availSpace = parkingLot.getAvailableSpace(vehicle2.type());
        assertThat(availSpace).isEqualTo(1);

        availSpace = parkingLot.getAvailableSpace(vehicle3.type());
        assertThat(availSpace).isEqualTo(8);

        parkingLot.parkVehicle(vehicle2);
        availSpace = parkingLot.getAvailableSpace(VehicleType.VAN);
        assertThat(availSpace).isEqualTo(0);

        availSpace = parkingLot.getAvailableSpace(VehicleType.CAR);
        assertThat(availSpace).isEqualTo(5);

        availSpace = parkingLot.getAvailableSpace(VehicleType.MOTORCYCLE);
        assertThat(availSpace).isEqualTo(1);
    }
}
