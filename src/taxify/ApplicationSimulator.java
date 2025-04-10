package taxify.application;

import java.util.List;

import taxify.application.ApplicationLibrary;

public class ApplicationSimulator implements IApplicationSimulator, IObserver {
    private ITaxiCompany company;
    private List<IUser> users;
    private List<IVehicle> vehicles;
    
    public ApplicationSimulator(ITaxiCompany company, List<IUser> users, List<IVehicle> vehicles) {
        this.company = company;
        this.users = users;
        this.vehicles = vehicles;
    }
    
    @Override
    public void show() {
        // show the status of the vehicles
        
        System.out.println("\n" + this.company.getName() + " status \n");

        for (IVehicle vehicle : this.vehicles) {
            System.out.println(vehicle.toString());
        }   
    }
    
    @Override
    public void showStatistics() {
        // show the statistics of the company
        
        String s = "\n" + this.company.getName() + " statistics \n";
        
        for (IVehicle vehicle : this.vehicles) {            
            s = s + "\n" +
            
            String.format("%-8s", vehicle.getClass().getSimpleName()) +
            String.format("%2s", vehicle.getId()) + " " +
            String.format("%2s", vehicle.getStatistics().getServices()) + " services " + 
            String.format("%3s", vehicle.getStatistics().getDistance()) + " km. " +
            String.format("%3s", vehicle.getStatistics().getBilling()) + " eur. " +
            String.format("%2s", vehicle.getStatistics().getReviews()) + " reviews " +
            String.format("%-4s", vehicle.getStatistics().getStars()) + " stars";
        }
                
        System.out.println(s);        
    }    

    @Override
    public void update() {
        // move vehicles to their next location
        
        for (IVehicle vehicle : this.vehicles) {
               vehicle.move();
        }
    }

    @Override
    public void requestService() {        
        // finds an available user and requests a service to the Taxi Company
        for (IUser user : this.users)
        {
            if (user.getService() == true)
                continue;
            if (user.isFemaleOrChild() && ApplicationLibrary.rand() % 2 == 0)
            {
                user.requestPinkService();
                break;
            }
            if (ApplicationLibrary.rand() % 3 == 0)
            {
                user.requestSilentService();
                break;
            }
            user.requestService();
            break;
        }
    }
    
    @Override
    public int getTotalServices() {
        return this.company.getTotalServices();
    }
    
    @Override
    public void updateObserver(String message) {
        System.out.println(message);
    }
}