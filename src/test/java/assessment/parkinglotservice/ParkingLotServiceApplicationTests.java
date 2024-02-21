package assessment.parkinglotservice;

import assessment.parkinglotservice.dto.AvailableSpaces;
import assessment.parkinglotservice.entity.Receipt;
import assessment.parkinglotservice.model.Vehicle;
import assessment.parkinglotservice.model.VehicleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ParkingLotServiceApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    //TODO: Need to get the Max available spaces of the lot through configuration instead of hard coding it like below.
    private int MAX_CYCLE_SPACE = 5;
    private int MAX_COMPACT_SPACE = 10;
    private int MAX_REGULAR_SPACE = 10;

    @Test
    public void parkVehicleAndGetParkingReceiptTest() {
        String licensePlate = "6JPA234";
        Vehicle vehicle = new Vehicle(licensePlate, VehicleType.CAR);
        Receipt receipt = parkVehicle(vehicle);

        assertThat(receipt).isNotNull();
        assertThat(receipt.getId()).isNotZero();
        assertThat(receipt.getSpaceNumber()).isNotEmpty();
        assertThat(receipt.getLicensePlate()).isEqualTo(licensePlate);
        assertThat(receipt.getTimeEnter()).isNotNull();
        assertThat(receipt.getTimeExit()).isNull();
    }

    @Test
    public void parkVehicleAndGetUniqueReceiptNumberTest() {
        String licensePlate1 = "6JPA234";
        Vehicle vehicle1 = new Vehicle(licensePlate1, VehicleType.CAR);
        String licensePlate2 = "7CWR123";
        Vehicle vehicle2 = new Vehicle(licensePlate2, VehicleType.CAR);

        Receipt receipt1 = parkVehicle(vehicle1);
        Receipt receipt2 = parkVehicle(vehicle2);

        assertThat(receipt1).isNotNull();
        assertThat(receipt1.getId()).isNotZero();
        assertThat(receipt1.getSpaceNumber()).isNotEmpty();
        assertThat(receipt1.getLicensePlate()).isEqualTo(licensePlate1);

        assertThat(receipt2).isNotNull();
        assertThat(receipt2.getId()).isNotZero();
        assertThat(receipt2.getSpaceNumber()).isNotEmpty();
        assertThat(receipt2.getLicensePlate()).isEqualTo(licensePlate2);

        assertThat(receipt1.getId()).isNotEqualTo(receipt2.getId());
    }

    @Test
    public void parkSameVehicleTwiceShouldGetSameReceiptNumberTest() {
        String licensePlate1 = "6JPA234";
        Vehicle vehicle = new Vehicle(licensePlate1, VehicleType.CAR);

        Receipt receipt1 = parkVehicle(vehicle);
        Receipt receipt2 = parkVehicle(vehicle);

        assertThat(receipt1).isNotNull();
        assertThat(receipt1.getId()).isNotZero();
        assertThat(receipt1.getSpaceNumber()).isNotEmpty();
        assertThat(receipt1.getLicensePlate()).isEqualTo(licensePlate1);
        assertThat(receipt1.getTimeEnter()).isNotNull();

        assertThat(receipt2).isNotNull();
        assertThat(receipt2.getId()).isNotZero();
        assertThat(receipt2.getSpaceNumber()).isNotEmpty();
        assertThat(receipt2.getLicensePlate()).isEqualTo(licensePlate1);
        assertThat(receipt2.getTimeEnter()).isNotNull();

        assertThat(receipt1.getId()).isEqualTo(receipt2.getId());
        assertThat(receipt1.getTimeEnter()).isEqualTo(receipt2.getTimeEnter());
    }

    @Test
    public void parkVehicleAndExitShouldReturnReceipt() {
        String licensePlate = "6JPA234";
        Vehicle vehicle = new Vehicle(licensePlate, VehicleType.CAR);

        Receipt receipt1 = parkVehicle(vehicle);

        assertThat(receipt1.getLicensePlate()).isEqualTo(licensePlate);

        Receipt unparkReceipt = unparkVehicle(receipt1);

        assertThat(unparkReceipt.getId()).isEqualTo(receipt1.getId());
        assertThat(unparkReceipt.getTimeEnter()).isEqualTo(receipt1.getTimeEnter());
        assertThat(unparkReceipt.getTimeExit()).isNotNull();
    }

    @Test
    public void parkSameVehicleOnTwoSeparateSessionShouldReturnTwoUniqueReceipt() {
        String licensePlate = "6JPA234";
        Vehicle vehicle = new Vehicle(licensePlate, VehicleType.CAR);

        Receipt receipt1 = parkVehicle(vehicle);

        assertThat(receipt1.getLicensePlate()).isEqualTo(licensePlate);

        Receipt unparkReceipt1 = unparkVehicle(receipt1);

        assertThat(unparkReceipt1.getId()).isEqualTo(receipt1.getId());
        assertThat(unparkReceipt1.getTimeEnter()).isEqualTo(receipt1.getTimeEnter());
        assertThat(unparkReceipt1.getTimeExit()).isNotNull();

        Receipt receipt2 = parkVehicle(vehicle);
        Receipt unparkReceipt2 = unparkVehicle(receipt2);

        assertThat(unparkReceipt2.getId()).isNotEqualTo(unparkReceipt1.getId());
    }

    @Test
    public void unparkTheSameVehicleShouldBeIndempotent() {
        String licensePlate = "6JPA234";
        Vehicle vehicle = new Vehicle(licensePlate, VehicleType.CAR);

        Receipt receipt1 = parkVehicle(vehicle);

        assertThat(receipt1.getLicensePlate()).isEqualTo(licensePlate);

        Receipt unparkReceipt1 = unparkVehicle(receipt1);

        assertThat(unparkReceipt1.getId()).isEqualTo(receipt1.getId());
        assertThat(unparkReceipt1.getTimeEnter()).isEqualTo(receipt1.getTimeEnter());
        assertThat(unparkReceipt1.getTimeExit()).isNotNull();

        Receipt unparkReceipt2 = unparkVehicle(receipt1);

        assertThat(unparkReceipt2.getId()).isEqualTo(unparkReceipt1.getId());
        assertThat(unparkReceipt2.getTimeEnter()).isEqualTo(unparkReceipt1.getTimeEnter());
        assertThat(unparkReceipt2.getTimeExit()).isEqualTo(unparkReceipt1.getTimeExit());
    }

    @Test
    public void getAvailableSpaceOnAnEmptyLot() {
        restTemplate.getForObject("/parkinglot/reset", Void.class);

        AvailableSpaces availableSpaces = getAvailableSpaces();

        assertThat(availableSpaces.availableMotorcycleSpace()).isEqualTo(MAX_CYCLE_SPACE);
        assertThat(availableSpaces.availableCompactSpace()).isEqualTo(MAX_COMPACT_SPACE);
        assertThat(availableSpaces.availableRegularSpace()).isEqualTo(MAX_REGULAR_SPACE);
    }

    @Test
    public void availableSpaceShouldDecreaseByOneWhenParkACar() {
        restTemplate.getForObject("/parkinglot/reset", Void.class);

        String licensePlate = "6JPA235";
        Vehicle vehicle = new Vehicle(licensePlate, VehicleType.CAR);
        Receipt receipt = parkVehicle(vehicle);

        AvailableSpaces availableSpaces = getAvailableSpaces();

        assertThat(availableSpaces.availableCompactSpace()).isEqualTo(MAX_COMPACT_SPACE-1);
    }

    @Test
    public void availableMotorcycleSpaceShouldDecreaseByOneWhenParkACar() {
        restTemplate.getForObject("/parkinglot/reset", Void.class);

        String licensePlate = "6MOTO23";
        Vehicle vehicle = new Vehicle(licensePlate, VehicleType.MOTORCYCLE);
        Receipt receipt = parkVehicle(vehicle);

        AvailableSpaces availableSpaces = getAvailableSpaces();

        assertThat(availableSpaces.availableMotorcycleSpace()).isEqualTo(MAX_CYCLE_SPACE-1);
        assertThat(availableSpaces.availableCompactSpace()).isEqualTo(MAX_COMPACT_SPACE);
        assertThat(availableSpaces.availableRegularSpace()).isEqualTo(MAX_REGULAR_SPACE);
    }

    @Test
    public void availableSpaceShouldDecreaseByThreeWhenParkAVan() {
        restTemplate.getForObject("/parking/reset", Void.class);

        String licensePlate = "8VAN495";
        Vehicle vehicle = new Vehicle(licensePlate, VehicleType.VAN);

        Receipt receipt = parkVehicle(vehicle);
        AvailableSpaces availableSpaces = getAvailableSpaces();

        assertThat(availableSpaces.availableRegularSpace()).isEqualTo(MAX_REGULAR_SPACE-3);
    }

    private Receipt parkVehicle(Vehicle vehicle) {
        String jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkFudGhvbnkgTmd1eWVuIiwiYWRtaW4iOmZhbHNlLCJpYXQiOjE1MTYyMzkwMjJ9.GYDOCQDABEZWqoa0ShIW3MiGnux241E1DrJbDHg1117FTfe-9ZI1UOWA3A3ysIt_S9dKZjPQXhb0l__g_qdBDgelNmTsKvNe42txs3chNLOrCbhzUvktOngc2TnpY0kxr8opet0Wbp4TYrF_f-PpCq_HZUIJI1yLiC01CiPVOaBWOwJ1CIxIFbD_0cWlS1gyT8sfZfYe0cCwqfXJTsYrBUWfsYTWdEVOyxTSZeZjGxIPGBgxbgPoUM9yKJ1FbjECy9YOgjybyklSuqKLD0BBbIXYzgNtQQmIRieFjxvAwwl5AAWJnvjJ7nnroMHbHG9VO5Kc7mIs_BItPRPFbw1jWQ";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken); // Set the bearer token

        // Set the request entity, including headers if needed
        HttpEntity<Object> requestEntity = new HttpEntity<>(vehicle, headers);

        String url = "/parkinglot/park";
//        return restTemplate.postForObject(url, vehicle, Receipt.class);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Receipt.class).getBody();
    }

    private Receipt unparkVehicle(Receipt receipt) {
        String unparkUrl = "/parkinglot/unpark/{receiptId}";
        return restTemplate.getForObject(unparkUrl, Receipt.class, receipt.getId());
    }

    private AvailableSpaces getAvailableSpaces() {
        String spacesUrl = "/parkinglot/spaces";
        return restTemplate.getForObject(spacesUrl, AvailableSpaces.class);
    }
}
