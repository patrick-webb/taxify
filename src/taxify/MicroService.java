package taxify;

public class MicroService extends Service{
   
        public MicroService(IUser user, ILocation pickup, ILocation dropoff) {
            super(user,pickup,dropoff);
        }
    
        @Override
        public String getServiceName() {
            return "Micro-Mobility Service";
        }
}
