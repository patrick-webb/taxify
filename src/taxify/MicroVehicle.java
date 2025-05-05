package taxify;

public abstract class MicroVehicle implements IMicroVehicle {
    static final double fee = 1.0;
    private int id;
    private ITaxiCompany company;
    private IService service;
    private VehicleStatus status;
    private ILocation location;
    private ILocation destination;
    private IStatistics statistics;
    private IRoute route;

    private IUser user;
    private boolean isReserved = false;
    private double batteryLevel = 100.0;
    private int rideCount = 0;
    

    public MicroVehicle(int id, ILocation location) {
        this.id = id;
        this.service = null; 
        this.status = VehicleStatus.FREE;
        this.location = location;
        this.destination = ApplicationLibrary.randomLocation(location);
        this.statistics = new Statistics();
        this.route = new Route(this.location, this.destination);
    }

    @Override
    public int getId() {
        return id;
    }
     
    @Override
    public void setLocation(ILocation location) {
       this.location = location;
    }

    @Override
    public ILocation getLocation() {  
        return this.location; 
    }

    @Override
    public ILocation getDestination() {  
        return this.destination; 
    }

    @Override
    public IService getService() { 
        return this.service; 
    }
    
    @Override
    public IStatistics getStatistics() { 
        return this.statistics; 
    }
    
    @Override
    public void setCompany(ITaxiCompany company) {
        this.company = company;
    }


    @Override
    public VehicleStatus getStatus() {
        return this.status;
    }

    @Override
    public double getBattery() {
        return this.batteryLevel;
    }

    @Override
    public void charge() {
        if (this.batteryLevel < 100) {
            this.batteryLevel = 100;
            System.out.println(this.getClass().getSimpleName() + this.id + " is at 100% battery");
        } 
    }    

    public void reserve(IService service) {
        this.service = service; 
        
        this.destination = service.getPickupLocation();
        this.route = new Route(this.destination, this.location); 
        this.isReserved = true;
        status = VehicleStatus.PICKUP;
    
    }
  
    @Override
    public void startRide() {
        this.destination = service.getDropoffLocation(); 
        this.route = new Route(this.location, this.destination); 
        status = VehicleStatus.SERVICE;
    }
  

    @Override
    public void endRide() {
        this.statistics.updateBilling(this.calculateCost());
        this.statistics.updateDistance(this.service.calculateDistance());
        this.statistics.updateServices();
        this.rideCount++;

        if (this.service.getStars() != 0) {
            this.statistics.updateStars(this.service.getStars());
            this.statistics.updateReviews();
        }
        
        this.service = null;
        this.status = VehicleStatus.FREE;

    }

    @Override
    public int getRideCount()
    {
        return this.rideCount;
    }

    @Override
    public void setRideCount(int count)
    {
        this.rideCount = count;
    }
    
    @Override
    public void notifyArrivalAtPickupLocation() {
        this.company.arrivedAtMicroPickupLocation(this); 
        this.startRide(); 
    } 
    
    @Override
    public void notifyArrivalAtDropoffLocation() {
        this.company.arrivedAtMicroDropoffLocation(this);
        this.endRide();

        
        this.batteryLevel -= 25;
        System.out.println(this.getClass().getSimpleName() + this.id + " battery reduced after ride. Current battery level: " + this.batteryLevel + "%");

        
        if (this.batteryLevel < 0) {
            this.batteryLevel = 0;
        } 
    
    }

    @Override
    public void move() {

        if (this.status == VehicleStatus.PICKUP) {
            if (this.route.hasLocations()) {                
                //System.out.println("User " + service.getUser().getId() + ", is walking to micro-mobility vehicle " + this.getClass().getSimpleName() + " " + this.id);
                this.route.getNextLocation(); 
            } else                
                notifyArrivalAtPickupLocation(); 
        }
        else if (this.status == VehicleStatus.SERVICE) {
            if (this.route.hasLocations()) 
                this.location = this.route.getNextLocation();

            if (!this.route.hasLocations()) {
                if (this.location.getX() == destination.getX() && this.location.getY() == destination.getY())
                    notifyArrivalAtDropoffLocation();
            }
        }
    }

    @Override
    public double calculateCost(){ 
       return this.service.calculateDistance(); 
    }

    @Override
    public boolean isFree() {
        return this.status == VehicleStatus.FREE; 
    }   
 
    @Override
    public String toString() { 

        if (this.status == VehicleStatus.SERVICE)
            return this.id + ": " + ", User " + service.getUser().getId() + ", at " + this.location + " is driving themself to " + this.destination;
        else if (this.status == VehicleStatus.PICKUP)
            return this.id + ": reserved for user " + service.getUser().getId();
        else if (this.status == VehicleStatus.FREE)
            return this.id + ": vehicle is idle at" + this.location;
        return "Logic failure";
    }   

}
