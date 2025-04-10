package taxify.functionality;

import java.util.List;

import taxify.interfaces.IVehicle;

public class TaxiCompany implements ITaxiCompany, ISubject {
    private String name;
    private List<IUser> users;
    private List<IVehicle> vehicles;
    private int totalServices;
    private IObserver observer;
    
    public TaxiCompany(String name, List<IUser> users, List<IVehicle> vehicles) {
        this.name = name;
        this.users = users;
        this.vehicles = vehicles;        
        this.totalServices = 0;
        
        for (IUser user : this.users) {
            user.setCompany(this);
        }
        
        for (IVehicle vehicle : this.vehicles) {
            vehicle.setCompany(this);
        }
    }
    
    @Override
    public String getName() {
        // return name
        return this.name;
    }

    @Override
    public int getTotalServices() {
        // return total services
        return this.totalServices;
    }
        
    @Override
    public boolean provideService(int user) {
        int userIndex = findUserIndex(user);        
        int vehicleIndex = findFreeVehicle();
        
        // if there is a free vehicle, assign a random pickup and drop-off location to the new service
        // the distance between the pickup and the drop-off location should be at least 3 blocks
        
        if (vehicleIndex != -1) {
            ILocation origin, destination;
                
            origin = ApplicationLibrary.randomLocation();
            destination = ApplicationLibrary.randomLocation(origin);
                
            
            // update the user status
                       
            this.users.get(userIndex).setService(true);
            
            // create a service with the user, the pickup and the drop-off location

            IService service = new Service(this.users.get(userIndex), origin, destination);
            
            // assign the new service to the vehicle
            
            this.vehicles.get(vehicleIndex).pickService(service);            
             
            notifyObserver("User " + this.users.get(userIndex).getId() + " requests " + service.getServiceName() + " from " + service.toString() + ", the ride is assigned to " +
                           this.vehicles.get(vehicleIndex).getClass().getSimpleName() + " " + this.vehicles.get(vehicleIndex).getId() + " at location " +
                           this.vehicles.get(vehicleIndex).getLocation().toString());
            
            // update the counter of services
            
            this.totalServices++;
            
            return true;
        }
        
        return false;
    }

    @Override
    public boolean providePinkService(int user, ILocation origin, ILocation destination)
    {
        int userIndex = findUserIndex(user);
        int closestFreeFemaleIndex = this.findClosestVehicle(listOfFreeVehicles(listOfFemaleVehicles()), destination);

        if (closestFreeFemaleIndex == -1)
            return false;

        this.users.get(userIndex).setService(true);

        IService pinkService = new PinkService(this.users.get(userIndex), origin, destination);

        this.vehicles.get(closestFreeFemaleIndex).pickService(pinkService);

        notifyObserver("User " + this.users.get(userIndex).getId() + " requests " + service.getServiceName() + " from " + service.toString() + ", the ride is assigned to " +
                           this.vehicles.get(vehicleIndex).getClass().getSimpleName() + " " + this.vehicles.get(vehicleIndex).getId() + " at location " +
                           this.vehicles.get(vehicleIndex).getLocation().toString());
        
        this.totalServices++;
        return true;
        
    }

    @Override
    public boolean provideSilentService(int user, ILocation origin, ILocation destination)
    {
        int userIndex = findUserIndex(user);
        int vehicleIndex = this.findClosestVehicle(listOfFreeVehicles(this.vehicles), destination);

        if (vehicleIndex == -1)
            return false;
        
        this.users.get(userIndex).setService(true);

        IService silentSerivce = new SilentService(this.users.get(userIndex), origin, destination);

        notifyObserver("User " + this.users.get(userIndex).getId() + " requests " + service.getServiceName() + " from " + service.toString() + ", the ride is assigned to " +
                           this.vehicles.get(vehicleIndex).getClass().getSimpleName() + " " + this.vehicles.get(vehicleIndex).getId() + " at location " +
                           this.vehicles.get(vehicleIndex).getLocation().toString());

        this.totalServices++;
        return true;
    }

    @Override
    public boolean provideSharedService(int user, ILocation origin, ILocation destination)
    {
        
        int userIndex = findUserIndex(user);           

        //Possibly problem when one passenger denies shared ride, does it go to next?
        IVechicle closestVehicle = findClosestVehicle(this.vehicles, origin)
        if (closestVehicle.isFree())
        {
            //Handle normal behavior, this might be moved to normal function provideService()
        }

        IService closestVehicleService = closestVehicle.getService();
        IUser userInRide = closestVehicleService.getUser();

        if (!userInRide.askPermissionForShare())
            return false;
        
        this.users.get(userIndex).setService(true);
        IService service = new Serivce(this.users.get(userIndex), origin, destination);
        this.totalServices++
        
        
    }

    public boolean provideSharedServiceDropoff(int user, ILocation origin, ILocation destination)
    {
        int userIndex = findUserIndex(user);

        List<IVehicle> shareSameDropoff = shareShameDropoff(destination);
        if (shareSameDropoff.size() == 0)
        {
            //Do normal function, maybe create a provide service overarching that then calls each? depending maybe do decorator ir simethng idk
        }

        //Maybe have to pass a list of vehicles in ascending order and then go through until one accepts
        IVehicle closestVehicle = this.vehicles.get(findClosestVehicle(shareSameDropoff, origin));

        if (!closestVehicle.getService().getUser().askPermissionForShare())
        {
            //handle denial, possibly just do regular instead of going through the list
            return false;
        }

        

    }

    @Override
    public void arrivedAtPickupLocation(IVehicle vehicle) {
        // notify the observer a vehicle arrived at the pickup location
        notifyObserver(String.format("%-8s",vehicle.getClass().getSimpleName()) + vehicle.getId() + " arrived at pickup location. ");
        
    }
    
    @Override
    public void arrivedAtDropoffLocation(IVehicle vehicle) {
        // a vehicle arrives at the drop-off location
        
        IService service = vehicle.getService();       
        int user = service.getUser().getId();
        int userIndex = findUserIndex(user);
       
        // the taxi company requests the user to rate the service, and updates its status

        this.users.get(userIndex).rateService(service);
        this.users.get(userIndex).setService(false);

        // update the counter of services
        
        this.totalServices--;    
        
        notifyObserver(String.format("%-8s",vehicle.getClass().getSimpleName()) + vehicle.getId() + " drops off user " + user);         
    }
        
    @Override
    public void addObserver(IObserver observer) {
        // add a observer
        this.observer = observer;        
    }
    
    @Override
    public void notifyObserver(String message) {
        // send a message to the observer
        this.observer.updateObserver(message);
    }
    
    private int findFreeVehicle() {
        // returns the index of a free vehicle in this.vehicles

        for (int i = 0; i < this.vehicles.size(); i++)
        {
            if (this.vehicles.get(i).isFree())
                return i;
        }

        return -1;

    }

    private List<IVehicle> listOfFreeVehicles(List<IVehicle> vehicleList)
    {
        List<IVehicle> freeVehicleList = new List<IVehicle>();

        for (int i = 0; i < vehicleList.size(); i++)
        {
            IVehicle vehicle  = vehicleList.get(i);

            if (vehicle.isFree())
                freeVehicleList.add(vehicle)
        }

        return freeVehicleList;
    }

    private List<IVehicle> listOfFemaleVehicles()
    {
        List<IVehicle> femaleVehicleList = new List<IVehicle>();

        for (int i = 0; i < this.vehicles.size(); i++)
        {
            IVehicle = vehicle = this.vehicles.get(i);

            if (vehicle.getDriver().getGender() == 'f')
                femaleVehicleList.add(vehicle);
        }

        return femaleVehicleList;
    }

    private List<IVehicle> listOfOccupiedVehicles()
    {
        List<IVehicle> occupiedVehicleList = new List<IVehicle>();

        for (int i = 0; i < this.vehicles.size(); i++)
        {
            IVehicle vehicle  = this.vehicles.get(i);

            if (!vehicle.isFree())
                occupiedVehicleList.add(vehicle)
        }

        return occupiedVehicleList;
    }

    private List<IVehicle> shareShameDropoff(ILocation location)
    {
        List<IVehicle> occupiedVehicles = listOfOccupiedVehicles();
        List<IVehicle> sameDropOffList = new List<IVehicle>();
        //May need to seperate into two function one that checks each vehicle and one that returns list, or maybe not can just check if list is empty

        for (IVehicle vehicle : occupiedVehicles)
        {
            if (vehicle.getLocation().getX() == location.getX() && vehicle.getLocation().getY() == location.getY())
            {
                sameDropOffList.add(vehicle);
            }
        }

        return sameDropOffList;

    }

    private int findClosestVehicle(List<IVehicle> vehicleList, ILocation location)
    {
        /*
        * !!!!!!!
        * Returns index of closest vehicle from the TaxiCompany's list of all vechicles (this.vehicles)
        * Does not return the index of vehicle in the passed vehicleList
        * !!!!!!!
        */
        int closestIndex;
        int closestDistance = 9999;

        if (vehicleList.size() == 0)
            return -1;

        for (int i = 0; i < vehicleList.size(); i++)
        {
            IVehicle vehicle = vehicleList.get(i);
            int manhattanDistance = ApplicationLibrary.distance(vehicle.getLocation(), location);
            if (manhattanDistance < closestDistance)
            {
                closestDistance = manhattanDistance;
                closestIndex = this.vehicles.indexOf(vehicle);
            }
        }

        return closestIndex;
    }

    private int findUserIndex(int id) {
        // returns the index of the user id in this.users
        for (IUser user : this.users)
        {
            if (user.getId() == id)
            {
                return this.users.indexOf(user);
            }

        }

        return -1;

    }

}
