package taxify;

public class ServiceFactory {

    public static IService createStandardService(IUser user, ILocation pickup, ILocation dropoff) {
        return new Service(user, pickup, dropoff);
    }

    public static IService createPinkService(IUser user, ILocation pickup, ILocation dropoff) {
        if (!user.isFemaleOrChild())
            throw new IllegalArgumentException("Pink rides are for women/kids");
        return new PinkService(user, pickup, dropoff);
    }

    public static IService createSilentService(IUser user, ILocation pickup, ILocation dropoff) {
        return new SilentService(user, pickup, dropoff);
    }

    public static SharedService createSharedService(IService service1, IService service2)
    {
        return new SharedService(service1, service2);
    }

    public static IService createMicroService(IUser user, ILocation pickup, ILocation dropoff)
    {
        return new MicroService(user, pickup, dropoff);
    }
}

