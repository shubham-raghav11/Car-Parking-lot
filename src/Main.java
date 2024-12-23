import java.util.HashMap;
import java.util.Map;

// VehicleType Enum
enum VehicleType {
    CAR, BIKE, TRUCK, BUS
}

// Vehicle Class
class Vehicle {
    private String registrationNumber;
    private VehicleType type;
    private String color;

    public Vehicle(String registrationNumber, VehicleType type, String color) {
        this.registrationNumber = registrationNumber;
        this.type = type;
        this.color = color;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public VehicleType getType() {
        return type;
    }

    public String getColor() {
        return color;
    }
}

// ParkingSpace Class
class ParkingSpace {
    private int slotNumber;
    private boolean isSpaceAvailable;
    private Vehicle vehicle;
    private ParkingTicket ticket;

    public ParkingSpace(int slotNumber) {
        this.slotNumber = slotNumber;
        this.isSpaceAvailable = true;
    }

    public boolean isAvailable() {
        return isSpaceAvailable;
    }

    public void parkVehicle(Vehicle vehicle, ParkingTicket ticket) {
        if (!isSpaceAvailable) {
            throw new IllegalStateException("Space is already occupied.");
        }
        this.vehicle = vehicle;
        this.ticket = ticket;
        this.isSpaceAvailable = false;
    }

    public void removeVehicle() {
        this.vehicle = null;
        this.ticket = null;
        this.isSpaceAvailable = true;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingTicket getTicket() {
        return ticket;
    }
}

// ParkingTicket Class
class ParkingTicket {
    private String registrationNumber;
    private long entryTime;

    public ParkingTicket(String registrationNumber) {
        this.registrationNumber = registrationNumber;
        this.entryTime = System.currentTimeMillis();
    }

    public long getEntryTime() {
        return entryTime;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }
}

// CostStrategy Class
class CostStrategy {
    private Map<VehicleType, Integer> hourlyRates;

    public CostStrategy() {
        hourlyRates = new HashMap<>();
        hourlyRates.put(VehicleType.CAR, 20);
        hourlyRates.put(VehicleType.BIKE, 10);
        hourlyRates.put(VehicleType.TRUCK, 30);
        hourlyRates.put(VehicleType.BUS, 30);
    }

    public int calculateCost(VehicleType type, long entryTime) {
        long currentTime = System.currentTimeMillis();
        long hoursParked = Math.max((currentTime - entryTime) / (1000 * 60 * 60), 1); // At least 1 hour
        return hourlyRates.get(type) * (int) hoursParked;
    }
}

// Floor Class
class Floor {
    int floorNumber;
    ParkingSpace[] spaces;

    public Floor(int floorNumber, int spaceCount) {
        this.floorNumber = floorNumber;
        this.spaces = new ParkingSpace[spaceCount];
        for (int i = 0; i < spaceCount; i++) {
            spaces[i] = new ParkingSpace(i + 1);
        }
    }

    public boolean parkVehicle(Vehicle vehicle, ParkingTicket ticket) {
        for (ParkingSpace space : spaces) {
            if (space.isAvailable()) {
                space.parkVehicle(vehicle, ticket);
                System.out.println("Parked vehicle at space " + space.getSlotNumber() + " on floor " + floorNumber);
                return true;
            }
        }
        return false; // No space is available
    }

    public ParkingTicket removeVehicle(String registrationNumber) {
        for (ParkingSpace space : spaces) {
            if (!space.isAvailable() && space.getVehicle().getRegistrationNumber().equals(registrationNumber)) {
                ParkingTicket ticket = space.getTicket();
                space.removeVehicle();
                System.out.println("Removed vehicle from space " + space.getSlotNumber() + " on floor " + floorNumber);
                return ticket;
            }
        }
        return null; // Vehicle is not there
    }

    public void showAvailability() {
        for (ParkingSpace space : spaces) {
            if (space.isAvailable()) {
                System.out.println("Space " + space.getSlotNumber() + " is available");
            }
        }
    }
}

// ParkingLot Class
class ParkingLot {
    private Floor[] floors;
    private CostStrategy costStrategy;

    public ParkingLot(int floorCount, int spacesPerFloor) {
        this.floors = new Floor[floorCount];
        for (int i = 0; i < floorCount; i++) {
            floors[i] = new Floor(i + 1, spacesPerFloor);
        }
        this.costStrategy = new CostStrategy();
    }

    public void parkVehicle(Vehicle vehicle) {
        ParkingTicket ticket = new ParkingTicket(vehicle.getRegistrationNumber());
        for (Floor floor : floors) {
            if (floor.parkVehicle(vehicle, ticket)) {
                return; // Vehicle parked
            }
        }
        System.out.println("Parking lot is full!");
    }

    public void removeVehicle(String registrationNumber) {
        for (Floor floor : floors) {
            ParkingTicket ticket = floor.removeVehicle(registrationNumber);
            if (ticket != null) {
                int cost = costStrategy.calculateCost(floor.spaces[0].getVehicle().getType(), ticket.getEntryTime());
                System.out.println("Vehicle " + registrationNumber + " removed. Total cost: ₹" + cost);
                return;
            }
        }
        System.out.println("Vehicle not found!");
    }

    public void showParkingLotStatus() {
        for (Floor floor : floors) {
            System.out.println("Floor " + floor.floorNumber + " availability:");
            floor.showAvailability();
        }
    }
}

// Main Class
public class Main {
    public static void main(String[] args) {
        // Create a parking lot with 2 floors and 3 spaces per floor
        ParkingLot parkingLot = new ParkingLot(2, 3);

        // Create vehicles
        Vehicle car1 = new Vehicle("py01jf5634", VehicleType.CAR, "red");
        Vehicle car2 = new Vehicle("up81fb5535", VehicleType.CAR, "blue");
        Vehicle bike1 = new Vehicle("Hr26ff4533", VehicleType.BIKE, "red");

        // Park vehicles
        parkingLot.parkVehicle(car1);
        parkingLot.parkVehicle(car2);
        parkingLot.parkVehicle(bike1);

        // Show parking lot status
        parkingLot.showParkingLotStatus();

        // Remove a vehicle
        parkingLot.removeVehicle("up81fb5535");

        // Show parking lot status again
        parkingLot.showParkingLotStatus();
    }
}
