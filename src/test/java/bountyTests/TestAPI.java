package bountyTests;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import bountyApi.RestService;
import bountyApi.Vehicle;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Methods run in order of priority
 * This isn't 100% necessary, but the tests build upon each other
 * ie if the third test fails, all after will fail
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAPI {
    OkHttpClient client;
    public static String ROOT_URL = "http://jacobzipper.com:8080/v1/";
    public static String ADMIN_JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJib3VudHkweCIsImFkbWluIjp0cnVlfQ.ltFAkU7zl8k6Pyb6TJjlL6_SYoWpvNe8BZ-jrZTtpTg";
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Map<String, String> params;

    @Mock
    RestService restService;

    /**
     * Do an authenticated post request to the server
     * @param route route on server
     * @param params parameters for post
     * @return the response object
     * @throws IOException
     */
    private Response doAuthPost(String route, Map<String, String> params) throws IOException {
        JSONObject jsonParams = new JSONObject(params);

        RequestBody requestBody = RequestBody.create(JSON, jsonParams.toString());

        Request request = new Request.Builder()
                .url(ROOT_URL + route)
                .addHeader("content-type", "application/json; charset=utf-8")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response;
    }

    /**
     * Do a simple get request
     * @param route route to get
     * @return the response object
     * @throws IOException
     */
    private Response doGet(String route) throws IOException {
        Request request = new Request.Builder()
                .url(ROOT_URL + route)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        return response;
    }

    /**
     * Before running each test, dump the database
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException{
        client = new OkHttpClient();
        restService = mock(RestService.class);
        params = new HashMap<>();
        params.put("jwt", ADMIN_JWT);
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        for (int i = 0; i < jArray.length(); i++) {
            res = doAuthPost("deletevehicle/" + jArray.getJSONObject(i).getString("id"), params);
            System.out.println(res);
        }
    }

    /**
     * Check to make sure viewvehicles works on an empty databse
     * @throws IOException
     */
    @Test
    public void checkAViewEmpty() throws IOException{
        // Check database empty
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());
    }

    /**
     * Check to make sure it is possible to add an object
     * to the database
     * @throws IOException
     */
    @Test
    public void checkBAddOne() throws IOException{
        // Check database empty
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());

        // Add to database
        String carName = "Honda";
        params.put("name", carName);
        res = doAuthPost("addvehicle/", params);
        JSONObject addedVehicle = new JSONObject(res.body().string());
        Assert.assertEquals(addedVehicle.getString("name"), carName);
    }

    /**
     * Test adding multiple cars to the database
     * @throws IOException
     */
    @Test
    public void checkCAddMany() throws IOException{
        // Check database empty
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());

        // Cars to add
        String[] carNames = {"Honda", "Dodge", "Toyota", "Volkswagen", "Lexus"};
        for (int i = 0; i < carNames.length; i++) {
            params.put("name", carNames[i]);
            res = doAuthPost("addvehicle/", params);
            JSONObject addedVehicle = new JSONObject(res.body().string());
            Assert.assertEquals(addedVehicle.getString("name"), carNames[i]);
        }

        // Check to see if all cars were added successfully
        res = doGet("viewvehicles");
        jArray = new JSONArray(res.body().string());
        Assert.assertEquals(carNames.length, jArray.length());
        for (int i = 0; i < jArray.length(); i++) {
            Assert.assertEquals(carNames[i], jArray.getJSONObject(i).getString("name"));
        }
    }

    /**
     * Check to see if you can view a specific car by id
     * @throws IOException
     */
    @Test
    public void checkDViewSpecific() throws IOException{
        // Check database empty
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());

        // Adding car
        String carName = "Honda";
        params.put("name", carName);
        res = doAuthPost("addvehicle/", params);
        JSONObject addedVehicle = new JSONObject(res.body().string());
        Assert.assertEquals(addedVehicle.getString("name"), carName);

        // Grabbing car by id
        res = doGet("viewvehicle/" + addedVehicle.getString("id"));
        jArray = new JSONArray(res.body().string());
        Assert.assertEquals(1, jArray.length());

        // Making sure car grabbed is the added car
        for (int i = 0; i < jArray.length(); i++) {
            Assert.assertEquals(addedVehicle.getString("name"),
                    jArray.getJSONObject(i).getString("name"));
        }
    }

    /**
     * Test to check if you can edit a vehicle
     * @throws IOException
     */
    @Test
    public void checkEEditVehicle() throws IOException{
        // Check database empty
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());

        // Adding car
        String carName = "Honda";
        params.put("name", carName);
        res = doAuthPost("addvehicle/", params);
        JSONObject addedVehicle = new JSONObject(res.body().string());
        Assert.assertEquals(addedVehicle.getString("name"), carName);

        // Make sure car is added correctly
        res = doGet("viewvehicle/" + addedVehicle.getString("id"));
        jArray = new JSONArray(res.body().string());
        Assert.assertEquals(1, jArray.length());
        for (int i = 0; i < jArray.length(); i++) {
            Assert.assertEquals(addedVehicle.getString("name"),
                    jArray.getJSONObject(i).getString("name"));
        }

        // Editing car
        carName = "Lexus";
        params.put("name", carName);
        res = doAuthPost("editvehicle/" + addedVehicle.getString("id"), params);
        JSONObject editedVehicle = new JSONObject(res.body().string());
        System.out.println(editedVehicle);
        Assert.assertEquals(editedVehicle.getString("name"), carName);
        Assert.assertEquals(editedVehicle.getString("id"), addedVehicle.getString("id"));

        // Make sure vehicle is edited properly
        res = doGet("viewvehicle/" + editedVehicle.getString("id"));
        jArray = new JSONArray(res.body().string());
        Assert.assertEquals(1, jArray.length());
        for (int i = 0; i < jArray.length(); i++) {
            Assert.assertEquals(editedVehicle.getString("name"),
                    jArray.getJSONObject(i).getString("name"));
        }
    }

    /**
     * Test delete function
     * @throws IOException
     */
    @Test
    public void checkFDelete() throws IOException{
        // Check database empty
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());

        // Adding car
        String carName = "Honda";
        params.put("name", carName);
        res = doAuthPost("addvehicle/", params);
        JSONObject addedVehicle = new JSONObject(res.body().string());
        Assert.assertEquals(addedVehicle.getString("name"), carName);
        params.remove("name");

        // Deleting car
        doAuthPost("deletevehicle/" + addedVehicle.getString("id"), params);

        // Making sure car is deleted
        res = doGet("viewvehicle/" + addedVehicle.getString("id"));
        jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
    }

    /**
     * Test to see if invalid auth is prevented
     * @throws IOException
     */
    @Test
    public void checkGInvalidAuth() throws IOException{
        // Changing params to have bad auth
        params.put("jwt", "very.invalid.jwt");

        // Checking delete with invalid auth
        Response res = doAuthPost("deletevehicle/1", params);
        Assert.assertEquals(500, res.code());

        // Checking addvehicle with invalid ith
        params.put("name", "randomCarName");
        res = doAuthPost("addvehicle", params);
        Assert.assertEquals(500, res.code());

        // Checking editvehicle with invalid auth
        res = doAuthPost("editvehicle/1", params);
        Assert.assertEquals(500, res.code());
    }

    /**
     * Code to dump database after each test
     * @throws IOException
     */
    @After
    public void tearDown() throws IOException{
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        for (int i = 0; i < jArray.length(); i++) {
            doAuthPost("deletevehicle/" + jArray.getJSONObject(i).getString("id"), params);
        }
    }

}
