package taxify;

import java.util.ArrayList;
import java.util.List;

public class TaxiCompany implements ITaxiCompany, ISubject {
    private String name;
    private List<IUser> users;
    private List<IVehicle> vehicles;
    private int totalServices;
    private IObserver observer;
    private List<IMicroVehicle> microVehicles;
    
    public TaxiCompany(String name, List<IUser> users, List<IVehicle> vehicles, List<IMicroVehicle> microVehicles) {
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

        this.microVehicles = microVehicles;

        for (IMicroVehicle micro : this.microVehicles)
        {
            micro.setCompany(this);
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
        int allVehicleIndex = this.findClosestVehicle(this.vehicles, origin);
        if (allVehicleIndex == -1)
            return false;

        IVehicle vehicle = this.vehicles.get(allVehicleIndex);

        
        if (!vehicle.isFree() && vehicle.getService().getUser().askPermissionForShare() && !vehicle.isSharing())
        {
            return provideSharedService(userIndex, origin, destination, vehicle);
        }
        
        int freeVehicleIndex = this.findClosestVehicle(listOfFreeVehicles(this.vehicles), origin);
        if (freeVehicleIndex == -1)
            return false;
                 
            
        // update the user status
                    
        this.users.get(userIndex).setService(true);
        
        // create a service with the user, the pickup and the drop-off location

        IService service = ServiceFactory.createStandardService(this.users.get(userIndex), origin, destination);
        
        // assign the new service to the vehicle
        
        this.vehicles.get(freeVehicleIndex).pickService(service);            
            
        notifyObserver("User " + this.users.get(userIndex).getId() + " requests " + service.getServiceName() + " from " + service.toString() + ", the ride is assigned to " +
                        this.vehicles.get(freeVehicleIndex).getClass().getSimpleName() + " " + this.vehicles.get(freeVehicleIndex).getId() + " at location " +
                        this.vehicles.get(freeVehicleIndex).getLocation().toString());
        
        // update the counter of services
        
        this.totalServices++;
        
        return true;

    }

    @Override
    public boolean providePinkService(int user, ILocation origin, ILocation destination)
    {
        int userIndex = findUserIndex(user);
        int closestFreeFemaleIndex = this.findClosestVehicle(listOfFreeVehicles(listOfFemaleVehicles()), destination);

        if (closestFreeFemaleIndex == -1)
        {
            notifyObserver("Unable to provide pink service");
            return false;
        }

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

    public boolean provideSharedService(int userIndex, ILocation origin, ILocation destination, IVehicle vehicle) {
        IUser user = this.users.get(userIndex);

        // List<IVehicle> shareSameDropoff = shareShameDropoff(destination);
        // if (shareSameDropoff.size() == 0)
        //     return false;
    
        IService currentService = vehicle.getService();

        if (ApplicationLibrary.distance(currentService.getDropoffLocation(), destination) > ApplicationLibrary.MAXIMUM_SHARE_DISTANCE)
            return false;

        IUser currentOccupiedUser = currentService.getUser();
        
        IService newService = ServiceFactory.createStandardService(this.users.get(userIndex), origin, destination);
        notifyObserver("User: " + user.getId() + " is requesting a shared from " + newService.toString());
        SharedService sharedService = ServiceFactory.createSharedService(currentService, newService);
        if (vehicle.getStatus() == VehicleStatus.SERVICE)
            sharedService.updateFirstUserPickedUp();

        vehicle.pickService(sharedService);
        this.users.get(userIndex).setService(true);

        notifyObserver("User " + currentOccupiedUser.getId() + " and " +  user.getId() + " are now sharing a ride. " + "From " + sharedService.toString() + ", the ride is assigned to " +
                           vehicle.getClass().getSimpleName() + " " + vehicle.getId() + " at location " +
                           vehicle.getLocation().toString());
        

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
        //a vehicle arrives at the drop-off location
        
        IService service = vehicle.getService();
        if (service == null)
        {
            System.out.println(vehicle.getStatus() + "ID: " + vehicle.getId());
        }       
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

    private List<IMicroVehicle> listOfFreeMicroVehicles(List<IMicroVehicle> microVehicleList)
    {
        List<IMicroVehicle> freeMicroVehicleList = new ArrayList<IMicroVehicle>();

        for (int i = 0; i < microVehicleList.size(); i++)
        {
            IMicroVehicle microVehicle  = microVehicleList.get(i);

            if (microVehicle.isFree())
                freeMicroVehicleList.add(microVehicle);
        }

        return freeMicroVehicleList;
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

    @Override
    public boolean provideMicroService(int userId, ILocation origin, ILocation destination)
    {
        int userIndex = findUserIndex(userId);
        IUser user = this.users.get(userIndex);
       
        List<IMicroVehicle> freeVehicles = listOfFreeMicroVehicles(this.microVehicles);
        int closestVehicleIndex = closestMicroMobilityVehicle(freeVehicles, origin);

        if (closestVehicleIndex == -1)
            return false;

        user.setService(true);
        IService service = ServiceFactory.createMicroService(user, origin, destination);

        this.microVehicles.get(closestVehicleIndex).reserve(service); 

        notifyObserver("User " + user.getId() + " requests a " + service.getServiceName() + " Service from " + service.toString() +
                        ", the ride is assigned to " + this.microVehicles.get(closestVehicleIndex).getClass().getSimpleName() +
                        " " + this.microVehicles.get(closestVehicleIndex).getId() + " at location " + this.microVehicles.get(closestVehicleIndex).getLocation().toString());
      
        this.totalServices++;            
        return true; 

    }

    @Override
    public void arrivedAtMicroPickupLocation(IMicroVehicle vehicle)
    {
        IService service = vehicle.getService();
        IUser user = service.getUser();
        notifyObserver("User " + user.getId() + " has arrived at the pickup location for " + vehicle.getClass().getSimpleName() + vehicle.getId());
    }

    @Override
    public void arrivedAtMicroDropoffLocation(IMicroVehicle micro)
    {
        IService service = micro.getService();       
        int user = service.getUser().getId();
        int userIndex = findUserIndex(user);
    
        
        this.users.get(userIndex).rateService(service);
        this.users.get(userIndex).setService(false);
    
        
        this.totalServices--;
        notifyObserver(micro.getClass().getSimpleName() + " " + micro.getId() + " is dropped off by user " + service.getUser().getId() + ", at location " + micro.getLocation().toString());
    }

    private int closestMicroMobilityVehicle(List<IMicroVehicle> mVehicles, ILocation location) {
        /*
        * !!!!!!!
        * Returns index of closest vehicle from the TaxiCompany's list of all vechicles (this.vehicles)
        * Does not return the index of vehicle in the passed vehicleList
        * !!!!!!!
        */
        int closestDistance = 9999;
        int closestIndex = -1;

        if (mVehicles.isEmpty())
            return closestIndex;

        for (int i = 0; i < mVehicles.size(); i++) {
            IMicroVehicle vehicle = mVehicles.get(i);
            int distance = ApplicationLibrary.distance(vehicle.getLocation(), location);
    
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }
        IMicroVehicle mVehi = mVehicles.get(closestIndex);
        return this.microVehicles.indexOf(mVehi);
    }

}
