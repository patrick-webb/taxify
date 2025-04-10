package taxify;


public class Taxi extends Vehicle {
    private double rate = 2;

    public Taxi(int id, ILocation location)
    {
        super(id, location);
    }

    @Override
    public double calculateCost()
    {
        return super.calculateCost() * rate;
    }
    
}
