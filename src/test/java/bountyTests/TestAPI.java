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

// @RunWith attaches a runner with the test class to initialize the test data
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAPI {
    OkHttpClient client;
    public static String ROOT_URL = "http://localhost:8080/v1/";
    public static String ADMIN_JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJib3VudHkweCIsImFkbWluIjp0cnVlfQ.ltFAkU7zl8k6Pyb6TJjlL6_SYoWpvNe8BZ-jrZTtpTg";
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Map<String, String> params;

    @Mock
    RestService restService;

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

    private Response doGet(String route) throws IOException {
        Request request = new Request.Builder()
                .url(ROOT_URL + route)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        return response;
    }

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

    @Test
    public void checkAViewEmpty() throws IOException{
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());
    }

    @Test
    public void checkBAddOne() throws IOException{
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());
        String carName = "Honda";
        params.put("name", carName);
        res = doAuthPost("addvehicle/", params);
        JSONObject addedVehicle = new JSONObject(res.body().string());
        Assert.assertEquals(addedVehicle.getString("name"), carName);
    }

    @Test
    public void checkCAddMany() throws IOException{
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());
        String[] carNames = {"Honda", "Dodge", "Toyota", "Volkswagen", "Lexus"};
        for (int i = 0; i < carNames.length; i++) {
            params.put("name", carNames[i]);
            res = doAuthPost("addvehicle/", params);
            JSONObject addedVehicle = new JSONObject(res.body().string());
            Assert.assertEquals(addedVehicle.getString("name"), carNames[i]);
        }
        res = doGet("viewvehicles");
        jArray = new JSONArray(res.body().string());
        Assert.assertEquals(carNames.length, jArray.length());
        for (int i = 0; i < jArray.length(); i++) {
            Assert.assertEquals(carNames[i], jArray.getJSONObject(i).getString("name"));
        }
    }

    @Test
    public void checkDViewSpecific() throws IOException{
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());
        String carName = "Honda";
        params.put("name", carName);
        res = doAuthPost("addvehicle/", params);
        JSONObject addedVehicle = new JSONObject(res.body().string());
        Assert.assertEquals(addedVehicle.getString("name"), carName);
        res = doGet("viewvehicle/" + addedVehicle.getString("id"));
        jArray = new JSONArray(res.body().string());
        Assert.assertEquals(1, jArray.length());
        for (int i = 0; i < jArray.length(); i++) {
            Assert.assertEquals(addedVehicle.getString("name"),
                    jArray.getJSONObject(i).getString("name"));
        }
    }

    @Test
    public void checkEEditVehicle() throws IOException{
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());
        String carName = "Honda";
        params.put("name", carName);
        res = doAuthPost("addvehicle/", params);
        JSONObject addedVehicle = new JSONObject(res.body().string());
        Assert.assertEquals(addedVehicle.getString("name"), carName);
        res = doGet("viewvehicle/" + addedVehicle.getString("id"));
        jArray = new JSONArray(res.body().string());
        Assert.assertEquals(1, jArray.length());
        for (int i = 0; i < jArray.length(); i++) {
            Assert.assertEquals(addedVehicle.getString("name"),
                    jArray.getJSONObject(i).getString("name"));
        }
        carName = "Lexus";
        params.put("name", carName);
        res = doAuthPost("editvehicle/" + addedVehicle.getString("id"), params);
        JSONObject editedVehicle = new JSONObject(res.body().string());
        System.out.println(editedVehicle);
        Assert.assertEquals(editedVehicle.getString("name"), carName);
        Assert.assertEquals(editedVehicle.getString("id"), addedVehicle.getString("id"));
        res = doGet("viewvehicle/" + editedVehicle.getString("id"));
        jArray = new JSONArray(res.body().string());
        Assert.assertEquals(1, jArray.length());
        for (int i = 0; i < jArray.length(); i++) {
            Assert.assertEquals(editedVehicle.getString("name"),
                    jArray.getJSONObject(i).getString("name"));
        }
    }

    @Test
    public void checkFDelete() throws IOException{
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
        Assert.assertEquals(200, res.code());
        String carName = "Honda";
        params.put("name", carName);
        res = doAuthPost("addvehicle/", params);
        JSONObject addedVehicle = new JSONObject(res.body().string());
        Assert.assertEquals(addedVehicle.getString("name"), carName);
        params.remove("name");
        doAuthPost("deletevehicle/" + addedVehicle.getString("id"), params);
        res = doGet("viewvehicle/" + addedVehicle.getString("id"));
        jArray = new JSONArray(res.body().string());
        Assert.assertEquals(restService.viewVehicles(), jArray.toList());
    }

    @Test
    public void checkGInvalidAuth() throws IOException{
        when(restService.viewVehicles()).thenReturn(new ArrayList<>());
        params.put("name", "randomCarName");
        params.put("jwt", "very.invalid.jwt");
        Response res = doAuthPost("addvehicle", params);
        Assert.assertEquals(500, res.code());
    }

    @After
    public void tearDown() throws IOException{
        Response res = doGet("viewvehicles");
        JSONArray jArray = new JSONArray(res.body().string());
        for (int i = 0; i < jArray.length(); i++) {
            doAuthPost("deletevehicle/" + jArray.getJSONObject(i).getString("id"), params);
        }
    }

}
