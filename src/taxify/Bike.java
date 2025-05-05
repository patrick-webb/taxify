package taxify;

public class Bike extends MicroVehicle {
    private double rate = 0.25;

    public Bike(int id, ILocation location)
    {
        super(id, location);
    }

    @Override
    public double calculateCost()
    {
        return super.calculateCost() * rate + 1;
    }

    @Override
    public String toString()
    {
        return "Bike " + super.toString();
    }

    @Override
    public void startRide()
     {
        if (this.getBattery() <= 25.0) {
            System.out.println("Bike " + this.getId() + " needs to charge.");
            this.charge();
        }
        super.startRide();
    }
}
