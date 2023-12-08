package utils;

import entities.Release;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MavenUtils {

    public static List<Release> getPomDirectDependencies(String pomPath) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(pomPath));
        return model.getDependencies().stream()
                .map(dep -> new Release(dep.getGroupId(), dep.getArtifactId(), dep.getVersion()))
                .collect(Collectors.toList());
    }
}
