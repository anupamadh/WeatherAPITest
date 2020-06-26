package com.weather.statuses;
import static io.restassured.RestAssured.given;
import com.weather.common.RestUtilities;
import com.weather.constants.Auth;
import com.weather.constants.EndPoints;
import com.weather.constants.Path;
import com.weather.models.StationAddModel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
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
    }

    @Test
    public void postStation() {
        StationAddModel stations = new StationAddModel();
        stations.setExternal_id("ANM_TES001");
        stations.setName("Anz Updated Station");
        stations.setLatitude(35.80);
        stations.setLongitude(122.47);
        stations.setAltitude(143);
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
