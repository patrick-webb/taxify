package taxify;

public interface IMicroVehicle {

    public int getId();
    public void setLocation(ILocation location);
    public ILocation getLocation();
    public ILocation getDestination();
    public IService getService();
    public IStatistics getStatistics(); 
    public void setCompany(ITaxiCompany company);
    public void notifyArrivalAtPickupLocation();
    public void notifyArrivalAtDropoffLocation();
    public VehicleStatus getStatus();
    public void reserve(IService service);
    public void charge();
    public double getBattery();
    public void setRideCount(int count);
    public int getRideCount();
    public void startRide();
    public void endRide();
    public void move(); 
    public double calculateCost();
    public boolean isFree();
    public String toString();
    
}