package taxify;

import java.util.Random;

public class ApplicationLibrary {
    public static final int MINIMUM_DISTANCE = 3;
    private static final int MAP_WIDTH = 10;
    private static final int MAP_HEIGHT = 10;
    public static final int MAXIMUM_SHARE_DISTANCE = 100; 

    public static int rand() {
        Random random = new Random();
        
        return random.nextInt(9767);
    }
    
    public static int rand(int max) {
        Random random = new Random();

        return random.nextInt(9767) % max;
    }
    
    public static int distance(ILocation a, ILocation b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }
    
    public static ILocation randomLocation() {
        return new Location(rand(MAP_WIDTH), rand(MAP_HEIGHT));
    }
    
    public static ILocation randomLocation(ILocation location) {
        ILocation destination;
        
        do {
            
            destination = new Location(rand(MAP_WIDTH), rand(MAP_HEIGHT));
            
        } while (distance(location, destination) < MINIMUM_DISTANCE);  
            
        return destination;
    }

    public static boolean locationsEqual(ILocation location1, ILocation location2)
    {
        if (location1.getX() == location2.getX() && location1.getY() == location2.getY())
            return true;
        return false;
    }
 
}
