package experiences;

import entities.Artifact;
import entities.Release;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.simple.JSONObject;
import utils.MavenUtils;
import utils.WeaverApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 * Speed client side
 * Determine the average, max, min release speed of my dependencies so that I know the best time to update everything.
 */
public class ExpSpeedClient implements Iexperience{
    List<Double> dependenciesSpeed = new ArrayList<>();

    public void execute(){
        String expPomPath = "experiences/exp1.pom.xml";
        try {
            List<Release> directDependencies = MavenUtils.getPomDirectDependencies(expPomPath);
            for (Release release : directDependencies){
                JSONObject node = WeaverApi.getSpecificArtifactQuery(new Artifact(release), List.of("SPEED"));
                if (node != null){
                    addArtifactSpeed(node);
                }
            }
            printResult();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void addArtifactSpeed(JSONObject node) {
        Double nodeSpeed = (Double) node.get("speed");
        if (nodeSpeed != 0){
            dependenciesSpeed.add(1/nodeSpeed);
        }
    }

    private void printResult(){
        Double min = dependenciesSpeed.stream().min(Double::compare).orElse(null);
        Double max = dependenciesSpeed.stream().max(Double::compare).orElse(null);
        OptionalDouble average = dependenciesSpeed.stream().mapToDouble(d -> d).average();

        if(min!=null) {
            System.out.println("Min: " + String.format("%.2f", min) +" days");
        }

        if(max!=null) {
            System.out.println("Max: " + String.format("%.2f", max) +" days");
        }

        if (average.isPresent()) {
            System.out.println("Average: " + String.format("%.2f", average.getAsDouble()) +" days");
        }
    }

    public void printName(){
        System.out.println("-----------\nEXPERIENCE SPEED CLIENT\n-----------");
    }
}
