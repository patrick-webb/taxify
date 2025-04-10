package taxify;

public class PinkService extends Service {
    
    public PinkService(IUser user, ILocation pickup, ILocation dropoff)
    {
        super(user, pickup, dropoff);
    }

    @Override
    public getServiceName()
    {
        return "Pink Service";
    }

}
