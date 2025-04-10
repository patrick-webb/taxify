package taxify;

import java.time.LocalDate;

public class Driver implements IDriver {
    private String firstName;
    private String lastName;
    private char gender;
    private LocalDate birthday;
    private int yearsOfExperience;
    private IVehicle vehicle;

    public Driver(String firstName, String lastName, char gender, LocalDate birthday, int yearsOfExperience)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getFirstName()
    {
        return this.firstName;
    }

    public String getLastName()
    {
        return this.lastName;
    }

    public char getGender()
    {
        return this.gender;
    }

    public LocalDate getBirthDate()
    {
        return this.birthday;
    }

    public int getYearsOfExperience()
    {
        return this.yearsOfExperience;
    }

    public IVehicle getVehicle()
    {
        return this.vehicle;
    }

    public double getRating()
    {
        return this.vehicle.getStatistics().getStars();
    }

    public void setVehicle(IVehicle vehicle)
    {
        this.vehicle = vehicle;
    }


}
