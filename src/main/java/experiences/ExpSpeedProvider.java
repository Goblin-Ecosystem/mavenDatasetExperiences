package experiences;

import entities.Artifact;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.WeaverApi;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 * Speed provider side
 * Determine the average, max, min release speed of artifacts who depends on me so that I know the best time to provide my updates.
 */
public class ExpSpeedProvider implements Iexperience{
    List<Double> clientSpeed = new ArrayList<>();
    Artifact providerArtifact = new Artifact("org.apache.httpcomponents", "httpcore");

    public void execute(){
        String query = "MATCH (a:Artifact) -[e:relationship_AR]-> (r:Release) -[d:dependency]-> (me:Artifact) " +
                "WHERE me.id = '"+providerArtifact.getGa()+"' " +
                "RETURN DISTINCT a";
        JSONArray result = WeaverApi.cypherQuery(query, List.of("SPEED"));
        if (result != null) {
            for (Object o : result) {
                JSONObject jsonObject = (JSONObject) o;
                JSONObject node = (JSONObject) jsonObject.get("node");
                Double nodeSpeed = (Double) node.get("speed");
                if (nodeSpeed != 0){
                    clientSpeed.add(1/nodeSpeed);
                }
            }
        }
        printResult();
    }

    private String getCurrentSpeed(){
        JSONObject node = WeaverApi.getSpecificArtifactQuery(providerArtifact, List.of("SPEED"));
        if (node != null){
            Double nodeSpeed = (Double) node.get("speed");
            if (nodeSpeed != 0){
               return String.valueOf(String.format("%.2f", 1/nodeSpeed));
            }
            else {
                return "Unknown";
            }
        }
        else {
            return "Unknown";
        }
    }

    private void printResult(){
        System.out.println("Current speed: " + getCurrentSpeed() + " days");
        Double min = clientSpeed.stream().min(Double::compare).orElse(null);
        Double max = clientSpeed.stream().max(Double::compare).orElse(null);
        OptionalDouble average = clientSpeed.stream().mapToDouble(d -> d).average();

        if(min!=null) {
            System.out.println("Min client speed: " + String.format("%.2f", min) +" days");
        }

        if(max!=null) {
            System.out.println("Max client speed: " + String.format("%.2f", max) +" days");
        }

        if (average.isPresent()) {
            System.out.println("Average client speed: " + String.format("%.2f", average.getAsDouble()) +" days");
        }
    }

    public void printName(){
        System.out.println("-----------\nEXPERIENCE SPEED PROVIDER\n-----------");
    }

}
