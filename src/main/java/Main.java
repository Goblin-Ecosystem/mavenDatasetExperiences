import experiences.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args){
        for(Iexperience exp : getAllExperiences()){
            exp.printName();
            long startTime = System.currentTimeMillis();
            exp.execute();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            System.out.println("Experience execution time: " + executionTime + " ms");
        }
    }

    private static List<Iexperience> getAllExperiences(){
        List<Iexperience> experienceList = new ArrayList<>();
        experienceList.add(new ExpUpdate());
        experienceList.add(new ExpSpeedClient());
        experienceList.add(new ExpSpeedProvider());
        experienceList.add(new ExpEcosystem());
        experienceList.add(new ExpArtifactCompare());
        return experienceList;
    }
}
