package bountyApi;

import java.util.List;

/**
 * Mock rest service for mockito
 */
public interface RestService {
    /**
     * @return vehicles in database
     */
    List<Object> viewVehicles();

    /**
     * @param id id of vehicle to view
     * @return vehicles matching id
     */
    List<Object> viewVehicle(int id);

    /**
     * @param id id of vehicle to delete
     */
    void deleteVehicle(int id);

    /**
     * @param id id of vehicle to edit
     * @param name new name for car
     * @return edited vehicle
     */
    Vehicle editVehicle(int id, String name);

    /**
     * @param name new car's name
     * @return vehicle added
     */
    Vehicle addVehicle(String name);
}
