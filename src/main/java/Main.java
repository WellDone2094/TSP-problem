import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by WellDone2044 on 17/09/15.
 */
public class Main {
    public static void main(String[] args) {
        FileParser fileParser = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
            fileParser = new FileParser(br);
        }catch (Exception ex){
            ex.printStackTrace();
            return;
        }

        DistanceMatrix distanceMatrix = new DistanceMatrix(fileParser.getCoordinates());

    }
}
