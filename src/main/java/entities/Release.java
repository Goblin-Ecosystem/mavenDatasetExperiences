package entities;

public class Release {
    private String groupId;
    private String artifactId;
    private String version;

    public Release(String gav){
        String[] splitedGav = gav.split(":");
        if(splitedGav.length == 3){
            this.groupId = splitedGav[0];
            this.artifactId = splitedGav[1];
            this.version = splitedGav[2];
        }
    }

    public Release(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getGav() {
        return groupId + ":" + artifactId + ":" + version;
    }
}
