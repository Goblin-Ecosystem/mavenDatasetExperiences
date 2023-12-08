package experiences;

import entities.Release;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.FileUtils;
import utils.WeaverApi;

import java.util.*;

/**
 * Artifact compare experience
 * 1. Get latest twenty releases of an artifact.
 * 2. Count the number of dependents of releases and export result as csv.
 * 3.Count the number of cve aggregated of releases and export result as csv.
 */
public class ExpArtifactCompare implements Iexperience{
    String artifact1Ga = "com.fasterxml.jackson.core:jackson-databind";
    String artifact2Ga = "com.google.code.gson:gson";
    private final String exportFolder = "experiences/compareLib/";

    @Override
    public void execute() {
        processArtifact(artifact1Ga);
        processArtifact(artifact2Ga);
    }

    private void processArtifact(String artifactGa) {
        List<Release> artifactReleases =  getArtifactLast20ReleasesWithCveAggreg(artifactGa);
        processDependentsCount(artifactGa, artifactReleases);
    }

    private void processDependentsCount(String artifactGa, List<Release> artifactReleases) {
        Map<String, Integer> artDependentCountMap = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();
        for (Release release : artifactReleases) {
            List<JSONObject> result = WeaverApi.getReleaseDependentsQuery(release, List.of());
            artDependentCountMap.put(release.getGav(), result.size());
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Time to get dependents of the twenty "+ artifactGa + " releases: "+executionTime+" ms");
        FileUtils.exportMapToCSV(artDependentCountMap, "release,nbDependents", exportFolder+artifactGa.replaceAll(":","_")+".csv");
    }

    private List<Release> getArtifactLast20ReleasesWithCveAggreg(String ga) {
        List<Release> resultList = new ArrayList<>();
        Map<String, Integer> artCountCveMap = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();
        String query = "MATCH (a:Artifact)-[e:relationship_AR]->(r:Release) " +
                "WHERE a.id = '" + ga + "' AND NOT r.id =~ '.*-rc.*'" +
                "RETURN r ORDER BY r.timestamp DESC LIMIT 20";
        JSONArray result = WeaverApi.cypherQuery(query, List.of("CVE_AGGREGATED"));
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Time to get twenty latest releases with CVE aggregated of "+ ga + ": "+executionTime+" ms");
        if (result != null) {
            for (int i = result.size() - 1; i >= 0; i--) {
                JSONObject jsonObject = (JSONObject) result.get(i);
                JSONObject node = (JSONObject) jsonObject.get("node");
                JSONArray cveJsonArray = (JSONArray) node.get("CVE_aggregated");
                String gav = (String) node.get("id");
                artCountCveMap.put(gav, cveJsonArray.size());
                resultList.add(new Release(gav));
            }
        }
        FileUtils.exportMapToCSV(artCountCveMap, "release,nbCveAggregated", exportFolder+ga.replaceAll(":","_")+"_CVE.csv");
        return resultList;
    }

    @Override
    public void printName() {
        System.out.println("-----------\nEXPERIENCE LIBRARY COMPARE\n-----------");
    }
}
