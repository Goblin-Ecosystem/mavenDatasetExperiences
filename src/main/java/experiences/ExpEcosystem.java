package experiences;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.WeaverApi;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Ecosystem experience
 * 1. Calculate the percentage of packages that have at least one CVE in their latest version on the entire ecosystem.
 * 2. Calculate the percentage of release that have at least one CVE and export csv with timestamp and cve count.
 * 3. Export csv containing for all releases their number of direct dependencies
 */
public class ExpEcosystem implements Iexperience{
    private int totalLastReleases = 0;
    private int lastReleasesWithCve = 0;
    private int totalReleases = 0;
    private int releasesWithCve = 0;
    private long executionTimeLast = 0;
    private long queryTimeLast = 0;
    private long executionTimeAll = 0;
    private long queryTimeAll = 0;
    private long executionBoxPlot = 0;
    private long queryTimeBoxplot = 0;
    private final String exportFolder = "experiences/ecosystem/";

    @Override
    public void execute() {
        long startTime = System.currentTimeMillis();
        lastReleases();
        long endTime = System.currentTimeMillis();
        executionTimeLast = endTime - startTime;
        startTime = System.currentTimeMillis();
        allReleases();
        endTime = System.currentTimeMillis();
        executionTimeAll = endTime - startTime;
        startTime = System.currentTimeMillis();
        directDependenciesBoxPlot();
        endTime = System.currentTimeMillis();
        executionBoxPlot = endTime - startTime;
        printResult();
    }

    private void lastReleases(){
        String query = "MATCH (a:Artifact) " +
                "WITH a " +
                "MATCH (a)-[:relationship_AR]->(r:Release) " +
                "WITH a, r " +
                "ORDER BY r.timestamp DESC " +
                "WITH a, COLLECT(r)[0] AS mostRecentRelease " +
                "RETURN mostRecentRelease";
        long startTime = System.currentTimeMillis();
        JSONArray result = WeaverApi.cypherQuery(query, List.of("CVE"));
        long endTime = System.currentTimeMillis();
        queryTimeLast = endTime - startTime;
        if (result != null) {
            for (Object o : result) {
                totalLastReleases++;
                JSONObject jsonObject = (JSONObject) o;
                JSONObject node = (JSONObject) jsonObject.get("node");
                JSONArray cveJsonArray = (JSONArray) node.get("CVE");
                if(!cveJsonArray.isEmpty()){
                    lastReleasesWithCve++;
                }
            }
        }
    }

    private void allReleases(){
        try (FileWriter writer = new FileWriter(exportFolder+"cveOccurrenceByTimestamp.csv")) {
            writer.write("timestamp,nbCVE\n");
            String query = "MATCH (n:Release) " +
                    "RETURN n";
            long startTime = System.currentTimeMillis();
            JSONArray result = WeaverApi.cypherQuery(query, List.of("CVE"));
            long endTime = System.currentTimeMillis();
            queryTimeAll = endTime - startTime;
            if (result != null) {
                for (Object o : result) {
                    totalReleases++;
                    JSONObject jsonObject = (JSONObject) o;
                    JSONObject node = (JSONObject) jsonObject.get("node");
                    JSONArray cveJsonArray = (JSONArray) node.get("CVE");
                    if (!cveJsonArray.isEmpty()) {
                        releasesWithCve++;
                        writer.write(node.get("timestamp")+","+cveJsonArray.size()+"\n");
                        writer.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void directDependenciesBoxPlot(){
        try (FileWriter writer = new FileWriter(exportFolder+"nbDirectDependencies.csv")) {
            writer.write("nbDependencies\n");
            String query = "MATCH (r:Release)-[:dependency]->() " +
                    "WITH r, count(*) AS dependencyCount " +
                    "RETURN dependencyCount";
            long startTime = System.currentTimeMillis();
            JSONArray result = WeaverApi.cypherQuery(query, List.of());
            long endTime = System.currentTimeMillis();
            queryTimeBoxplot = endTime - startTime;
            if (result != null) {
                for (Object o : result) {
                    JSONObject jsonObject = (JSONObject) o;
                    String nbDependencies = (String) jsonObject.get("dependencyCount");
                    writer.write(nbDependencies+"\n");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void printResult() {
        double percentageLast = ((double) lastReleasesWithCve / totalLastReleases) * 100;
        double percentageAll = ((double) releasesWithCve / totalReleases) * 100;
        System.out.println(lastReleasesWithCve+"/"+totalLastReleases+" last releases have CVE(s) ("+String.format("%.3f", percentageLast)+"%)");
        System.out.println("Query time: "+queryTimeLast+" ms");
        System.out.println("Execution time: "+executionTimeLast+" ms");
        System.out.println(releasesWithCve+"/"+totalReleases+" releases have CVE(s) ("+String.format("%.3f", percentageAll)+"%)");
        System.out.println("Query time: "+queryTimeAll+" ms");
        System.out.println("Execution time: "+executionTimeAll+" ms");
        System.out.println("All releases and number of dependencies csv export");
        System.out.println("Query time: "+queryTimeBoxplot+" ms");
        System.out.println("Execution time: "+executionBoxPlot+" ms");
    }

    @Override
    public void printName() {
        System.out.println("-----------\nEXPERIENCE ECOSYSTEM\n-----------");
    }
}
