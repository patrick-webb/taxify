package taxify;

public class Scooter extends MicroVehicle {
    private double rate = 0.15;

    public Scooter(int id, ILocation location)
    {
        super(id, location);
    }

    @Override
    public double calculateCost()
    {
        return super.calculateCost() * rate + fee;
    }

    @Override
    public String toString()
    {
        return "Scooter" + super.toString();
    }

    @Override
    public void startRide()
    {
        if (getRideCount() >= 3) {
            System.out.println("Scooter " + this.getId() + " needs to charge after exceeding ride count");
            this.charge();
            this.setRideCount(0);
        }
        if (getBattery() < 100.0) {
            System.out.println("Scooter " + this.getId() + " needs to charge to full" );
            this.charge();
        }
        super.startRide();
    }
}
