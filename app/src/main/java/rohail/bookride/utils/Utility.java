package rohail.bookride.utils;

public class Utility {

    public static String getFormattedCoordinates(String coordinate) {

        if (coordinate.length() > 9)
            coordinate = coordinate.substring(0, 9);

        return coordinate;
    }
}
