package taxify;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class TestProgram {

    public static void main(String[] args) {
        Random random = new Random();

        int beginningServices = 5;
        int userAmount = random.nextInt(10,16);
        int vehicleAmount = random.nextInt(5,11);
        
        List<IUser> userList = new ArrayList<IUser>();
        List<IVehicle> vehicleList = new ArrayList<IVehicle>();

        String[] firstNames = {"Alice", "Bob", "Charlie", "Diana", "Ethan", "Fiona", "George", "Hannah", "Ian", "Julia"};
        String[] lastNames = {"Smith", "Johnson", "Brown", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin"};

        for (int i = 0; i < userAmount; i++)
        {
            String firstName = firstNames[random.nextInt(10)];
            String lastName = lastNames[random.nextInt(10)];

            char gender;
            if (i % 2 == 0)
                gender = 'm';
            else
                gender = 'f';

            LocalDate date = LocalDate.ofYearDay(2000, 30);

            IUser user = new User(i, firstName, lastName, gender, date);
            userList.add(user);
        }

        for (int i = 0; i < vehicleAmount; i++)
        {
            ILocation location = ApplicationLibrary.randomLocation();

            IVehicle vehicle;

            if (i % 3 == 0)
                vehicle = new Shuttle(i, location);
            else
                vehicle = new Taxi(i, location);

            vehicleList.add(vehicle);
        }

        TaxiCompany company = new TaxiCompany("Taxify", userList, vehicleList);
        ApplicationSimulator sim = new ApplicationSimulator(company, userList, vehicleList);
        company.addObserver(sim);

        for (int i = 0; i < beginningServices; i++)
            sim.requestService();
            
        while (sim.getTotalServices() > 0)
        {
            if (random.nextInt(100) < 25)
                sim.requestService();

            sim.update();
        }

        System.out.println();
        System.out.println("Amount of Users " + String.valueOf(userAmount) + ".");
        System.out.println("Amount of Vehicles " + String.valueOf(vehicleAmount) + ".");
        sim.showStatistics();



    }


}
