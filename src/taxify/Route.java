package taxify;

import java.util.ArrayList;
import java.util.List;

public class Route implements IRoute {
    private List<ILocation> route;
    
    public Route(ILocation location, ILocation destination) {
        this.route = setRoute(location, destination);
    }
    
    @Override
    public boolean hasLocations() {
        return !this.route.isEmpty();
    }
            
    @Override
    public ILocation getNextLocation() {
        ILocation location = this.route.get(0);
        
        this.route.remove(0);
        
        return location;
    }
    
    @Override
    public String toString() {
        String route = "";
           
           for (ILocation location : this.route) {
               route = route + location.toString() + " ";
           }
       
           return route;        
    }
    
    private static List<ILocation> setRoute(ILocation location, ILocation destination) {
        List<ILocation> route = new ArrayList<ILocation>();
        
        int x1 = location.getX();
        int y1 = location.getY();
        
        int x2 = destination.getX();
        int y2 = destination.getY();
        
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
       
        for (int i=1; i<=dx; i++) {
            x1 = (x1 < x2) ? x1 + 1 : x1 - 1;

            route.add(new Location(x1, y1));
        }
        
        for (int i=1; i<=dy; i++) {
            y1 = (y1 < y2) ? y1 + 1 : y1 - 1;
            
            route.add(new Location(x1, y1));
        }
        
        return route;
    }       
}
