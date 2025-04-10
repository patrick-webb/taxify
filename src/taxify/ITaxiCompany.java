package taxify;

import java.util.List;

public interface ITaxiCompany {

    public String getName();    
    public int getTotalServices();
    public boolean provideService(int user, ILocation origin, ILocation destination);
    //public boolean provideSharedService(int user)
    public boolean provideSilentService(int user, ILocation origin, ILocation destination);
    public boolean providePinkService(int user, ILocation origin, ILocation destination);
    public void arrivedAtPickupLocation(IVehicle vehicle);
    public void arrivedAtDropoffLocation(IVehicle vehicle);
    
}