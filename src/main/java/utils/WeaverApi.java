package utils;

import entities.Artifact;
import entities.Release;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WeaverApi {
    private static final String API_URL = "http://localhost:8080";

    private static JSONArray executeQuery(JSONObject bodyJsonObject, String apiRoute){
        try {
            URL url = new URL(API_URL+apiRoute);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/json; utf-8");
            http.setRequestProperty("Accept", "application/json");
            http.setDoOutput(true);

            byte[] out = bodyJsonObject.toString().getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            if(http.getResponseCode() == 200){
                JSONParser jsonParser = new JSONParser();
                return (JSONArray)jsonParser.parse(
                        new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
            }
            http.disconnect();
        } catch (IOException | org.json.simple.parser.ParseException e) {
            System.out.println("Unable to connect to API:\n" + e);
        }
        return null;
    }

    public static JSONArray cypherQuery(String query, List<String> addedValues){
        String apiRoute = "/cypher";
        JSONObject bodyJsonObject = new JSONObject();
        bodyJsonObject.put("query", query);
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(addedValues);
        bodyJsonObject.put("addedValues", jsonArray);
        return executeQuery(bodyJsonObject, apiRoute);
    }

    public static JSONObject getSpecificReleaseQuery(Release release, List<String> addedValues){
        String apiRoute = "/release";

        JSONObject bodyJsonObject = new JSONObject();
        bodyJsonObject.put("groupId", release.getGroupId());
        bodyJsonObject.put("artifactId", release.getArtifactId());
        bodyJsonObject.put("version", release.getVersion());
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(addedValues);
        bodyJsonObject.put("addedValues", jsonArray);

        JSONArray result = executeQuery(bodyJsonObject, apiRoute);
        if (result != null && !result.isEmpty()) {
            JSONObject jsonObject = (JSONObject) result.get(0);
            return (JSONObject) jsonObject.get("node");
        }
        return null;
    }

    public static List<JSONObject> getReleaseDependentsQuery(Release release, List<String> addedValues){
        String apiRoute = "/release/dependents";

        JSONObject bodyJsonObject = new JSONObject();
        bodyJsonObject.put("groupId", release.getGroupId());
        bodyJsonObject.put("artifactId", release.getArtifactId());
        bodyJsonObject.put("version", release.getVersion());
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(addedValues);
        bodyJsonObject.put("addedValues", jsonArray);

        JSONArray result = executeQuery(bodyJsonObject, apiRoute);
        List<JSONObject> releaseList = new ArrayList<>();
        if (result != null) {
            for (Object o : result) {
                releaseList.add((JSONObject) ((JSONObject) o).get("node"));
            }
        }
        return releaseList;
    }

    public static List<JSONObject> getAllArtifactReleaseQuery(Artifact artifact, List<String> addedValues){
        String apiRoute = "/artifact/releases";

        JSONObject bodyJsonObject = new JSONObject();
        bodyJsonObject.put("groupId", artifact.getGroupId());
        bodyJsonObject.put("artifactId", artifact.getArtifactId());
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(addedValues);
        bodyJsonObject.put("addedValues", jsonArray);

        JSONArray result = executeQuery(bodyJsonObject, apiRoute);
        List<JSONObject> releaseList = new ArrayList<>();
        if (result != null) {
            for (Object o : result) {
                releaseList.add((JSONObject) ((JSONObject) o).get("node"));
            }
        }
        return releaseList;
    }

    public static JSONObject getSpecificArtifactQuery(Artifact artifact, List<String> addedValues){
        String apiRoute = "/artifact";

        JSONObject bodyJsonObject = new JSONObject();
        bodyJsonObject.put("groupId", artifact.getGroupId());
        bodyJsonObject.put("artifactId", artifact.getArtifactId());
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(addedValues);
        bodyJsonObject.put("addedValues", jsonArray);

        JSONArray result = executeQuery(bodyJsonObject, apiRoute);
        if (result != null && !result.isEmpty()) {
            JSONObject jsonObject = (JSONObject) result.get(0);
            return (JSONObject) jsonObject.get("node");
        }
        return null;
    }

    public static List<JSONObject> getReleasesNewVersions(Release release, List<String> addedValues){
        String apiRoute = "/release/newVersions";

        JSONObject bodyJsonObject = new JSONObject();
        bodyJsonObject.put("groupId", release.getGroupId());
        bodyJsonObject.put("artifactId", release.getArtifactId());
        bodyJsonObject.put("version", release.getVersion());
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(addedValues);
        bodyJsonObject.put("addedValues", jsonArray);

        JSONArray result = executeQuery(bodyJsonObject, apiRoute);
        List<JSONObject> releaseList = new ArrayList<>();
        if (result != null) {
            for (Object o : result) {
                releaseList.add((JSONObject) ((JSONObject) o).get("node"));
            }
        }
        return releaseList;
    }
}
