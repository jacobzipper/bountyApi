package bountyApi;

import java.util.List;

public interface RestService {
    List<Object> viewVehicles();
    List<Object> viewVehicle(int id);
    void deleteVehicle(int id);
    Vehicle editVehicle(int id, String name);
    Vehicle addVehicle(String name);
}
