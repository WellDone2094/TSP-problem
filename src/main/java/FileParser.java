import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by WellDone2044 on 17/09/15.
 */
public class FileParser {

    private String name;
    private String type;
    private int dimension;
    private int bestKnow;
    private ArrayList<Point> coordinates;

    public FileParser(BufferedReader br) throws IOException {
        for(String line; (line = br.readLine()) != null && !line.equals("NODE_COORD_SECTION"); ) {
            String[] splittedString = line.split(":");
            if(splittedString.length<2){
                return;
            }
            splittedString[0] = this.removeStartAndEndSymbols(splittedString[0]);
            splittedString[1] = this.removeStartAndEndSymbols(splittedString[1]);

            switch (splittedString[0]){
                case "NAME":
                    this.name = splittedString[1];
                    break;
                case "TYPE":
                    this.type = splittedString[1];
                    break;
                case "DIMENSION":
                    this.dimension = Integer.parseInt(splittedString[1]);
                    break;
                case "BEST_KNOWN":
                    this.bestKnow = Integer.parseInt(splittedString[1]);
                    break;
            }
        }

        coordinates = new ArrayList<Point>();

        for(String line; (line = br.readLine()) != "EOF" && line != null; ) {
            String[] splittedString = line.split(" ");

            if(splittedString.length!=3){
                return;
            }

            int id    = Integer.parseInt(splittedString[0]);
            double x  = Double.parseDouble(splittedString[1]);
            double y  = Double.parseDouble(splittedString[2]);

            coordinates.add(new Point(id, x, y));
        }
    }

    public String getType() {
        return type;
    }

    public int getDimension() {
        return dimension;
    }

    public int getBestKnow() {
        return bestKnow;
    }

    public ArrayList<Point> getCoordinates() {
        return coordinates;
    }

    private String removeStartAndEndSymbols(String s){
        while(s.length()>0 && s.charAt(0) == ' '){
            s = s.substring(1);
        }
        while(s.charAt(s.length() - 1) == ' ' || s.charAt(s.length()-1) == ':'){
            s = s.substring(0, s.length()-1);
        }
        return s;
    }


}
