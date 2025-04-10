package taxify;

public class SilentService extends Service {
    
    public SilentService(IUser user, ILocation pickup, ILocation dropoff)
    {
        super(user, pickup, dropoff);
    }

    @Override
    public String getServiceName()
    {
        return "Silent Service";
    }

}
