package taxify;

import java.time.LocalDate;

public interface IDriver {
    public String getFirstName();
    public String getLastName();
    public char getGender();
    public LocalDate getBirthDate();
    public int getYearsOfExperience();
    public double getRating();
    public IVehicle getVehicle();
    public void setVehicle(IVehicle vehicle);
}
