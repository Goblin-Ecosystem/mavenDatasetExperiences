package experiences;

import entities.Release;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.MavenUtils;
import utils.WeaverApi;

import java.io.IOException;
import java.util.*;

/**
 * 1. Retrieve a project's ego-centric dependency tree with CVEs.
 * 2. For packages with CVEs, retrieve later versions without CVEs.
 */
public class ExpUpdate implements Iexperience{
    private int directDependenciesNumber = 0;
    private int totalDependenciesNumber = 0;
    private List<ReleaseCve> cveList = new ArrayList<>();

    public void execute(){
        String expPomPath = "experiences/exp1.pom.xml";
        try {
            List<Release> directDependencies = MavenUtils.getPomDirectDependencies(expPomPath);
            directDependenciesNumber = directDependencies.size();
            for (Release release : directDependencies){
                totalDependenciesNumber++;
                JSONObject node = WeaverApi.getSpecificReleaseQuery(release, List.of("CVE"));
                checkNodeCVE(node, release.getGav());
                resolveDependency(release, release.getGav());
            }
            printResult();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void resolveDependency(Release release, String path) {
        String query = "MATCH (r:Release)-[d:dependency]->(a:Artifact) " +
                "WHERE r.id = '" + release.getGav() + "'  AND d.scope = 'compile' " +
                "WITH a.id AS artifactId, d.targetVersion AS targetVersion " +
                "MATCH (w:Release) " +
                "WHERE w.id = artifactId+':'+targetVersion " +
                "RETURN w";
        JSONArray result = WeaverApi.cypherQuery(query, List.of("CVE"));
        if (result != null) {
            for (Object o : result) {
                totalDependenciesNumber++;
                JSONObject jsonObject = (JSONObject) o;
                JSONObject node = (JSONObject) jsonObject.get("node");
                Release depRelease = new Release ((String) node.get("id"));
                checkNodeCVE(node, path + " -> " + depRelease.getGav());
                resolveDependency(depRelease, path + " -> ");
            }
        }
    }

    private void checkNodeCVE(JSONObject node, String pathToCve) {
        JSONArray cveJsonArray = (JSONArray) node.get("CVE");
        if(!cveJsonArray.isEmpty()){
            ReleaseCve releaseCve = new ReleaseCve((String) node.get("id"),  (Long) node.get("timestamp"), pathToCve);
            for (Object cveObject : cveJsonArray) {
                JSONObject jsonObjectCve = (JSONObject) cveObject;
                Map<String, String> cve = new HashMap<>();
                for (Object key : jsonObjectCve.keySet()){
                    cve.put(key.toString(),jsonObjectCve.get(key.toString()).toString());
                }
                releaseCve.addCve(cve);
            }
            cveList.add(releaseCve);
        }
    }

    private void printResult() {
        System.out.println("Direct dependencies: " + directDependenciesNumber);
        System.out.println("Total dependencies: " + totalDependenciesNumber);
        Map<String, Integer> severityCount = new HashMap<>();
        severityCount.put("TOTAL", 0);
        severityCount.put("CRITICAL", 0);
        severityCount.put("HIGH", 0);
        severityCount.put("MODERATE", 0);
        severityCount.put("LOW", 0);

        for(ReleaseCve releaseCve : cveList){
            for (Map<String, String> cveMap : releaseCve.cveList){
                String severity = cveMap.get("severity");
                if (severityCount.containsKey(severity)) {
                    severityCount.put(severity, severityCount.get(severity) + 1);
                    severityCount.put("TOTAL", severityCount.get("TOTAL") + 1);
                }
            }
        }
        System.out.println("Vulnerabilities: " + severityCount.get("TOTAL") + " TOTAL -> "
                + severityCount.get("CRITICAL") + " CRITICAL, "
                + severityCount.get("HIGH") + " HIGH, "
                + severityCount.get("MODERATE") + " MODERATE, "
                + severityCount.get("LOW") + " LOW");
        System.out.println("Detail vulnerabilities: ");
        for(ReleaseCve releaseCve : cveList){
            System.out.println(releaseCve);
        }
        long startTime = System.currentTimeMillis();
        printAlternativeWithoutCve();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Find alternative execution time: " + executionTime + " ms");
    }

    private void printAlternativeWithoutCve(){
        System.out.println("\n Alternatives without CVE:");
        for(ReleaseCve releaseCve : cveList){
            System.out.println(releaseCve.gav+": ");
            for(JSONObject release : WeaverApi.getReleasesNewVersions(new Release(releaseCve.gav), List.of("CVE"))){
                JSONArray cveJsonArray = (JSONArray) release.get("CVE");
                if(cveJsonArray.isEmpty()){
                    System.out.println("   "+release.get("id"));
                }
            }
        }
    }

    public void printName(){
        System.out.println("-----------\nEXPERIENCE UPDATE\n-----------");
    }

    private class ReleaseCve{
        String gav;
        long timestamp;
        List<Map<String, String>> cveList = new ArrayList<>();
        String pathToRelease;

        public ReleaseCve(String gav, long timestamp, String pathToRelease) {
            this.gav = gav;
            this.timestamp = timestamp;
            this.pathToRelease = pathToRelease;
        }

        public void addCve(Map<String, String> cve){
            cveList.add(cve);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n-------- ").append(pathToRelease).append("--------");
            for(Map<String, String> cve : cveList){
                sb.append("\n").append(cve);
            }
            return sb.toString();
        }
    }
}

