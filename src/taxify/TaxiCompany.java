package taxify;

import java.util.ArrayList;
import java.util.List;

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
    public boolean provideService(int user, ILocation origin, ILocation destination) {
        int userIndex = findUserIndex(user);  
        if (provideSharedService(user, origin, destination))
        {
            return true;
        }

        List<IVehicle> listOfFree = this.listOfFreeVehicles(this.vehicles);
        int vehicleIndex = this.findClosestVehicle(listOfFree, origin);      

        if (vehicleIndex == -1)
            return false;
        
        if (vehicleIndex != -1) {                
            
            // update the user status
                       
            this.users.get(userIndex).setService(true);
            
            // create a service with the user, the pickup and the drop-off location

            IService service = ServiceFactory.createStandardService(this.users.get(userIndex), origin, destination);
            
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

        //IService pinkService = new PinkService(this.users.get(userIndex), origin, destination);
        IService pinkService = ServiceFactory.createPinkService(this.users.get(userIndex), origin, destination);

        this.vehicles.get(closestFreeFemaleIndex).pickService(pinkService);

        notifyObserver("User " + this.users.get(userIndex).getId() + " requests " + pinkService.getServiceName() + " from " + pinkService.toString() + ", the ride is assigned to " +
                           this.vehicles.get(closestFreeFemaleIndex).getClass().getSimpleName() + " " + this.vehicles.get(closestFreeFemaleIndex).getId() + " at location " +
                           this.vehicles.get(closestFreeFemaleIndex).getLocation().toString());
        
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

        //IService silentService = new SilentService(this.users.get(userIndex), origin, destination);
        IService silentService = ServiceFactory.createSilentService(this.users.get(userIndex), origin, destination);

        this.vehicles.get(vehicleIndex).pickService(silentService);

        notifyObserver("User " + this.users.get(userIndex).getId() + " requests " + silentService.getServiceName() + " from " + silentService.toString() + ", the ride is assigned to " +
                           this.vehicles.get(vehicleIndex).getClass().getSimpleName() + " " + this.vehicles.get(vehicleIndex).getId() + " at location " +
                           this.vehicles.get(vehicleIndex).getLocation().toString());

        this.totalServices++;
        return true;
    }

    public boolean provideSharedService(int userId, ILocation origin, ILocation destination)
    {
        int userIndex = findUserIndex(userId);
        IUser user = this.users.get(userIndex);

        // List<IVehicle> shareSameDropoff = shareShameDropoff(destination);
        // if (shareSameDropoff.size() == 0)
        //     return false;
        
        int closestVehicleIndex = findClosestVehicle(this.vehicles, origin);
        if (closestVehicleIndex == -1)
            return false;

        IVehicle closestVehicle = this.vehicles.get(closestVehicleIndex);
        IService currentService = closestVehicle.getService();
        if (closestVehicle.isSharing())
            return false;
        if (currentService == null)
            return false;
        if (ApplicationLibrary.distance(currentService.getDropoffLocation(), destination) > ApplicationLibrary.MAXIMUM_SHARE_DISTANCE)
            return false;

        IUser currentOccupiedUser = currentService.getUser();
        if (!currentOccupiedUser.askPermissionForShare())
            return false;
        
        IService newService = ServiceFactory.createStandardService(this.users.get(userIndex), origin, destination);
        SharedService sharedService = ServiceFactory.createSharedService(currentService, newService);
        if (closestVehicle.getStatus() != VehicleStatus.PICKUP)
            sharedService.updateFirstLegStarted();

        if (closestVehicle.getStatus() == VehicleStatus.SERVICE)
        {
            sharedService.updateFirstLegStarted();
        }

        closestVehicle.pickService(sharedService);
        this.users.get(userIndex).setService(true);

        notifyObserver("User " + currentOccupiedUser.getId() + " and " +  user.getId() + " are now sharing a ride. " + "From " + sharedService.toString() + ", the ride is assigned to " +
                           this.vehicles.get(closestVehicleIndex).getClass().getSimpleName() + " " + this.vehicles.get(closestVehicleIndex).getId() + " at location " +
                           this.vehicles.get(closestVehicleIndex).getLocation().toString());
        

        this.totalServices++;
        return true;
        

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
        List<IVehicle> freeVehicleList = new ArrayList<IVehicle>();

        for (int i = 0; i < vehicleList.size(); i++)
        {
            IVehicle vehicle  = vehicleList.get(i);

            if (vehicle.isFree())
                freeVehicleList.add(vehicle);
        }

        return freeVehicleList;
    }

    private List<IVehicle> listOfFemaleVehicles()
    {
        List<IVehicle> femaleVehicleList = new ArrayList<IVehicle>();

        for (int i = 0; i < this.vehicles.size(); i++)
        {
            IVehicle vehicle = this.vehicles.get(i);

            if (vehicle.getDriver().getGender() == 'f')
                femaleVehicleList.add(vehicle);
        }

        return femaleVehicleList;
    }

    private List<IVehicle> listOfOccupiedVehicles()
    {
        List<IVehicle> occupiedVehicleList = new ArrayList<IVehicle>();

        for (int i = 0; i < this.vehicles.size(); i++)
        {
            IVehicle vehicle  = this.vehicles.get(i);

            if (!vehicle.isFree())
                occupiedVehicleList.add(vehicle);
        }

        return occupiedVehicleList;
    }

    private List<IVehicle> shareShameDropoff(ILocation location)
    {
        List<IVehicle> occupiedVehicles = listOfOccupiedVehicles();
        List<IVehicle> sameDropOffList = new ArrayList<IVehicle>();
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
        int closestIndex = -1;
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
