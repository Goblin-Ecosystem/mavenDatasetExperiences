package entities;

public class Artifact {
    private String groupId;
    private String artifactId;

    public Artifact(Release release){
        this.groupId = release.getGroupId();
        this.artifactId = release.getArtifactId();
    }

    public Artifact(String ga){
        String[] splitedGav = ga.split(":");
        if(splitedGav.length == 2){
            this.groupId = splitedGav[0];
            this.artifactId = splitedGav[1];
        }
    }

    public Artifact(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGa(){
        return this.groupId + ":" + this.artifactId;
    }
}
