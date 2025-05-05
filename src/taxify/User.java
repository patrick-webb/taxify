package taxify;

import java.time.LocalDate;
import java.time.Period;

public class User implements IUser {
    private int id;
    private String firstName;
    private String lastName;
    private char gender;
    private LocalDate birthDate;
    private ITaxiCompany company;
    private boolean service;
    
    public User(int id, String firstName, String lastName, char gender, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.service = false;
    }
    
    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }
    
    @Override
    public String getLastName() {
        return this.lastName;
    }
    
    @Override
    public char getGender() {
       return this.gender;
    }

    @Override
    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    @Override
    public boolean getService() {
        return this.service;
    }
    
    @Override
    public void setService(boolean service) {
        this.service = service;
    }
    
    @Override
    public void setCompany(ITaxiCompany company) {
        this.company = company;
    }
    
    @Override
    public void requestService() {
        ILocation origin = ApplicationLibrary.randomLocation();
        ILocation destination = ApplicationLibrary.randomLocation(origin);
        this.company.provideService(this.id, origin, destination);
    }

    @Override
    public void requestMicroService() {
        ILocation origin = ApplicationLibrary.randomLocation();
        ILocation destination = ApplicationLibrary.randomLocation(origin);
        this.company.provideMicroService(this.id, origin, destination);
    
    }

    @Override
    public void requestSilentService() {
        ILocation origin = ApplicationLibrary.randomLocation();
        ILocation destination = ApplicationLibrary.randomLocation(origin);
        this.company.provideSilentService(this.id, origin, destination);
    }

    @Override
    public void requestPinkService() {
        ILocation origin = ApplicationLibrary.randomLocation();
        ILocation destination = ApplicationLibrary.randomLocation(origin);
        this.company.providePinkService(this.id, origin, destination);
    }
    
    @Override
    public void rateService(IService service) {
        // users rate around 50% of the services (1 to 5 stars)
        
        if (ApplicationLibrary.rand() % 2 == 0) {
            service.setStars(ApplicationLibrary.rand(5) + 1);
        }
    }


    @Override
    public boolean isFemale()
    {
        if (this.getGender() == 'f')
            return true;
        return false;
    }

    @Override
    public boolean isChild()
    {
        LocalDate currentDay = LocalDate.now();
        Period timeBetween = Period.between(this.getBirthDate(), currentDay);
        int age = timeBetween.getYears();

        if (age < 18)
            return true;
        return false;
    }

    @Override
    public boolean isFemaleOrChild()
    {
        if (this.isFemale() || this.isChild())
            return true;
        return false;
    }

    @Override
    public boolean askPermissionForShare()
    {
        if (ApplicationLibrary.rand() % 4 == 0)
            return false;
        
        return true;
    }
    
    @Override
    public String toString() {
        return this.getId() + " " + String.format("%-20s",this.getFirstName() + " " + this.getLastName());
    }
}
