package taxify;

import java.util.List;

public class ApplicationSimulator implements IApplicationSimulator, IObserver {
    private ITaxiCompany company;
    private List<IUser> users;
    private List<IVehicle> vehicles;
    private List<IMicroVehicle> microMobilityVehicles;
    
    public ApplicationSimulator(ITaxiCompany company, List<IUser> users, List<IVehicle> vehicles, List<IMicroVehicle> microVehicles) {
        this.company = company;
        this.users = users;
        this.vehicles = vehicles;
        this.microMobilityVehicles = microVehicles;
    }
    
    @Override
    public void show() {
        // show the status of the vehicles
        
        System.out.println("\n" + this.company.getName() + " vehicle status \n");

        for (IVehicle vehicle : this.vehicles) {
            System.out.println(vehicle.toString());
        }   

        System.out.println("\n" + this.company.getName() + " micro vehicle status \n");

        for (IMicroVehicle micro : this.microMobilityVehicles) {
            System.out.println(micro.toString());
        }

        
    }
    
    @Override
    public void showStatistics() {
        // show the statistics of the company
        
        String s = "\n" + this.company.getName() + " vehicle statistics \n";
        
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

        String microString = "\n" + this.company.getName() + " Micro Vehicle statistics";
        for (IMicroVehicle vehicle : this.microMobilityVehicles) {
            microString += "\n" +

            String.format("%-16s", vehicle.getClass().getSimpleName()) + 
            String.format("%2d", vehicle.getId()) + " | " +   
            String.format("%2d", vehicle.getStatistics().getServices()) + " services | " +
            String.format("%-3d", vehicle.getStatistics().getDistance()) + " km | " +  
            String.format("%5.1f", vehicle.getStatistics().getBilling()) + " EUR | " +   
            String.format("%2d", vehicle.getStatistics().getReviews()) + " reviews | " +  
            String.format("%4.2f", vehicle.getStatistics().getStars()) + " stars | "; 
        }
        System.out.println(microString);
              
    }    

    @Override
    public void update() {
        // move vehicles to their next location
        
        for (IVehicle vehicle : this.vehicles) {
               vehicle.move();
        }

        for (IMicroVehicle micro : this.microMobilityVehicles)
            micro.move(); 
    }

    @Override
    public void requestService() {        
        // finds an available user and requests a service to the Taxi Company
        for (IUser user : this.users)
        {
            if (user.getService() == true)
                continue;
            if (ApplicationLibrary.rand() % 3 == 0)
            {
                //System.out.println("User " + user.getId() + " is trying to request a micro vehicle" );
                user.requestMicroService();
                break;
            }
            if (user.isFemaleOrChild() && ApplicationLibrary.rand() % 5 == 0)
            {
                //System.out.println("User " + user.getId() + " is trying to request a pink vehicle" );
                user.requestPinkService();
                break;
            }
            if (ApplicationLibrary.rand() % 4 == 0)
            {
                //System.out.println("User " + user.getId() + " is trying to request a silent vehicle" );
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