import java.time.LocalDate;

public interface IDriver {
    public String getFirstName();
    public String getLastName();
    public char getGender();
    public LocalDate getBirthDate();
    public int getYearsOfExperience();
    public float getRating();
    public IVehicle getVehicle();
}
