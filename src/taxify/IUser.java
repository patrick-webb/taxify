package taxify.interfaces;

import java.time.LocalDate;

public interface IUser {

    public int getId();
    public String getFirstName();
    public String getLastName();
    public char getGender();
    public LocalDate getBirthDate();
    public boolean getService();
    public void setService(boolean service);
    public void setCompany(ITaxiCompany company);
    public void requestService();
    public void requestPinkService();
    public void requestSilentService();
    public void rateService(IService service);
    public String toString();
    public boolean askPermissionForShare();
    public boolean isFemale();
    public boolean isChild();
    

}
