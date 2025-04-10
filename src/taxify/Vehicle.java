package taxify;

import java.util.List;

public abstract class Vehicle implements IVehicle {
    private int id;
    private ITaxiCompany company;
    private IService service;
    private VehicleStatus status;
    private ILocation location;
    private ILocation destination;
    private IStatistics statistics;
    private IRoute route;
    private IDriver driver;
    private List<IService> serviceList;
        
    public Vehicle(int id, ILocation location) {        
        this.id = id;
        this.service = null;
        this.status = VehicleStatus.FREE;
        this.location = location;        
        this.destination = ApplicationLibrary.randomLocation(this.location);
        this.statistics = new Statistics();
        this.route = new Route(this.location, this.destination);
    }

    @Override
    public int getId() {
        
        return this.id;

    }
 
    @Override
    public ILocation getLocation() {
        
        return this.location;

    }

    @Override
    public VehicleStatus getStatus()
    {
        return this.status;
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

    public IDriver getDriver()
    {
        return this.driver;
    }

    public void setDriver(IDriver driver)
    {
        this.driver = driver;
    }
    
    @Override
    public void setCompany(ITaxiCompany company) {
        this.company = company;
    }
    
    @Override
    public void pickService(IService service) {
        // pick a service, set destination to the service pickup location, and status to "pickup"
        
        this.service = service;
        this.destination = service.getPickupLocation();
        this.route = new Route(this.location, this.destination);        
        this.status = VehicleStatus.PICKUP;
    }

    @Override
    public void startService() {
        // set destination to the service drop-off location, and status to "service"

        this.destination = service.getDropoffLocation();
        this.route = new Route(this.location, destination);
        this.status = VehicleStatus.SERVICE;        
    }

    @Override
    public void endService() {
        // update vehicle statistics
        
        this.statistics.updateBilling(this.calculateCost());
        this.statistics.updateDistance(this.service.calculateDistance());
        this.statistics.updateServices();
        
        // if the service is rated by the user, update statistics
        
        if (this.service.getStars() != 0) {
            this.statistics.updateStars(this.service.getStars());
            this.statistics.updateReviews();
        }
        
        // set service to null, and status to "free"
        
        this.service = null;
        this.destination = ApplicationLibrary.randomLocation(this.location);
        this.route = new Route(this.location, this.destination);
        this.status = VehicleStatus.FREE;
    }

    @Override
    public void notifyArrivalAtPickupLocation() {
        // notify the company that the vehicle is at the pickup location and start the service
        this.company.arrivedAtPickupLocation(this);
        this.startService();
    
    }
        
    @Override
    public void notifyArrivalAtDropoffLocation() {
        // notify the company that the vehicle is at the drop off location and end the service
        this.company.arrivedAtDropoffLocation(this);
        if (this.service instanceof SharedService)
        {
            SharedService shared = (SharedService) this.service;
            if(!shared.isFirstLegFinished())
            {
                shared.updateLeg();
                if (shared.getStars() != 0)
                    shared.bothReviewed = true;
                this.startService();
                return;
            }
            this.statistics.updateServices();
            if (shared.bothReviewed)
                this.statistics.updateReviews();
            this.endService();
        }
        else
        {
            this.endService();
        }
     }
        
    @Override
    public boolean isFree() {
        // returns true if the status of the vehicle is "free" and false otherwise
        if (this.status == VehicleStatus.FREE)
            return true;
        return false;
    }   
    
    @Override
    public void move() {
        // get the next location from the driving route
        
        this.location = this.route.getNextLocation();        
        
        // if the route has more locations the vehicle continues its route, otherwise the vehicle has arrived to a pickup or drop off location
        
        if (!this.route.hasLocations()) {
            if (this.service == null) {
                // the vehicle continues its random route

                this.destination = ApplicationLibrary.randomLocation(this.location);
                this.route = new Route(this.location, this.destination);
            }
            else {
                // check if the vehicle has arrived to a pickup or drop off location

                ILocation origin = this.service.getPickupLocation();
                ILocation destination = this.service.getDropoffLocation();

                if (this.location.getX() == origin.getX() && this.location.getY() == origin.getY()) {

                    notifyArrivalAtPickupLocation();

                } else if (this.location.getX() == destination.getX() && this.location.getY() == destination.getY()) {

                    notifyArrivalAtDropoffLocation();

                }        
            }
        }
    }

    @Override
    public boolean isSharing()
    {
        if (this.service instanceof SharedService)
            return true;
        return false;
    }

    @Override
    public double calculateCost() {
        if (this.service instanceof SharedService)
            return .5 * this.service.calculateDistance();
        return this.service.calculateDistance();
    }

    @Override
    public String toString() {
        return this.id + " at " + this.location + " driving to " + this.destination +
               ((this.status == VehicleStatus.FREE) ? " is free with path " + this.route.toString(): ((this.status == VehicleStatus.PICKUP) ?
               " to pickup user " + this.service.getUser().getId() : " in service "));
    }    
}
