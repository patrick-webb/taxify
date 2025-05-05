package taxify;

public class SharedService implements IService {
    private IService service1;
    private IService service2;
    private boolean finishedFirstleg = false;
    private boolean firstUserPickedUp = false;
    public boolean bothUsersPickedUp = false;
    private int stars;
    public int firstStars = 0;
    public boolean firstReviewed = false;
    public boolean bothReviewed = false;

    public SharedService(IService service1, IService service2)
    {
        this.service1 = service1;
        this.service2 = service2;
        this.stars = 0;
    }

    public int calculateDistance()
    {
        if (finishedFirstleg)
        {
            return (Math.abs(this.service2.getPickupLocation().getX() - this.service2.getDropoffLocation().getX())
                 + Math.abs(this.service2.getPickupLocation().getY() - this.service2.getDropoffLocation().getY()) ) / 1;
        }
        else
        {
            return (Math.abs(this.service1.getPickupLocation().getX() - this.service1.getDropoffLocation().getX())
                 + Math.abs(this.service1.getPickupLocation().getY() - this.service1.getDropoffLocation().getY())) / 1;
        }
    }

    @Override
    public void setStars(int stars)
    {
        if (!finishedFirstleg) {
            this.firstStars = stars;
            this.firstReviewed = true;
            return;
        }
        this.stars += stars;
    }

    @Override
    public int getStars()
    {
        return this.stars;
    }

    @Override
    public IUser getUser()
    {
        if (!this.isFirstLegFinished())
            return this.service1.getUser();
        else
            return this.service2.getUser();
    }

    public void updateLeg()
    {
        this.finishedFirstleg = true;
    }

    public void updateFirstUserPickedUp()
    {
        this.firstUserPickedUp = true;
    }

    public boolean isFirstLegFinished()
    {
        return finishedFirstleg;
    }

    public boolean isFirstUserPickedUp()
    {
        return firstUserPickedUp;
    }

    public ILocation getPickupLocation()
    {
        if (this.isFirstUserPickedUp())
            return service2.getPickupLocation();
        return service1.getPickupLocation();
    }

    public ILocation getDropoffLocation()
    {
        if (this.finishedFirstleg)
            return service2.getDropoffLocation();
        else
            return service1.getDropoffLocation();
    }

    public String getServiceName()
    {
        return "Shared Service";
    }

    public String toString()
    {
        if (this.isFirstUserPickedUp())
            return this.service2.getPickupLocation().toString() + " to " + service1.getDropoffLocation().toString() + " and " + service2.getDropoffLocation().toString();
        else
            return this.service1.getPickupLocation().toString() + " and " + this.service2.getPickupLocation().toString() + " to " + service1.getDropoffLocation().toString() + " and " + service2.getDropoffLocation().toString();
    }
}
