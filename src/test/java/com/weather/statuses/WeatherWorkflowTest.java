package com.weather.statuses;
import static io.restassured.RestAssured.given;
import com.weather.common.RestUtilities;
import com.weather.constants.Auth;
import com.weather.constants.EndPoints;
import com.weather.constants.Path;
import com.weather.models.StationAddModel;
import com.weather.utilities.ExcelUtility;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


import static io.restassured.RestAssured.given;

public class WeatherWorkflowTest extends StationAddModel {
    RequestSpecification reqSpec;
    ResponseSpecification resSpec;
    String stationId = "";

    @BeforeClass
    public void setup() {
        reqSpec = RestUtilities.getRequestSpecification();
        reqSpec.basePath(Path.STATUSES);
        resSpec = RestUtilities.getResponseSpecification();
        ExcelUtility.setExcelFile(Path.EXCEL_FILE, "AllTests");
    }

    @DataProvider(name = "addStation")
    public Object[][] getaddStation(){
        Object[][] testData = ExcelUtility.getTestData("add_station");
        return testData;
    }

    @Test(dataProvider = "addStation")
    public void postStation(String externalId, String name, String latitude, String longitude, String altitude) {
        StationAddModel stations = new StationAddModel();
        stations.setExternal_id(externalId);
        stations.setName(name);
        stations.setLatitude(Double.valueOf(latitude));
        stations.setLongitude(Double.valueOf(longitude));
        stations.setAltitude(Integer.valueOf(altitude));
        Response response =
                given()
                        .spec(RestUtilities.setBody(reqSpec,stations))
                        .when()
                        .post(EndPoints.STATUSES_STATION_POST)
                        .then()
                            .spec(resSpec)
                            .statusCode(201)
                            .extract()
                            .response();

        stationId = response.path("ID");
        System.out.println("The response.path: " + stationId);

    }

    @Test(dependsOnMethods={"postStation"})
    public void getStation() {
        RestUtilities.setEndPoint(EndPoints.STATUSES_STATION_SINGLE);
        Response res = RestUtilities.getResponse(
                RestUtilities.createPathParam(reqSpec, "id", stationId), "get");
        String extId = res.path("external_id");
        System.out.println("The tweet text is: " + extId);
    }

    @Test(dependsOnMethods={"getStation"})
    public void deleteTweet() {
        given()
                .spec(RestUtilities.createPathParam(reqSpec, "id", stationId))
                .when()
                .delete(EndPoints.STATUSES_STATION_SINGLE)
                .then()
                .spec(resSpec)
                .statusCode(204);
    }
}
