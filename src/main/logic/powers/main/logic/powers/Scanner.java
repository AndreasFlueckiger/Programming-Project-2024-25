package main.logic.powers;

import java.util.HashSet;
import java.util.Set;

public class Scanner implements Power{

    private int usage = 1;

    //this method starts the use of the all class and returns the coordinates of the other cells
    public Set<String> use(String AreaCenter){
        if(this.usage > 0 && AreaControl(AreaCenter)){
                this.usage--;
                return CoordinatesGenerator(AreaCenter);
        } else {
            return null;
        }
    }

    /*
     * The input  must be the coordinate of the center of the 3x3 area
     * there is the necessity to control if the area is over the borders
     * of the game board.
     * The coordinate that cannot be allowed is those who contains the letter A/L
     * or the number 1/10
     */
    private boolean AreaControl(String AreaCenter){

        if(AreaCenter.contains("A") || AreaCenter.contains("L")){
            return false;
        } else if (AreaCenter.contains("1") || AreaCenter.contains("10")){
            return false;
        } else {
            return true;
        }

    }

	public Set<String> CoordinatesGenerator(String AreaCenter){
        String coordinate = AreaCenter;
        char letter;
        int number;

        Set<String> coordinates = new HashSet<>();

        letter = coordinate.charAt(0);
        number = Integer.parseInt(coordinate.replace(String.valueOf(letter), ""));

        //coordinates in the upper row
        coordinate = String.valueOf(letter-1).concat(String.valueOf(number-1));
        coordinates.add(coordinate);
        coordinate = String.valueOf(letter).concat(String.valueOf(number-1));
        coordinates.add(coordinate);
        coordinate = String.valueOf(letter+1).concat(String.valueOf(number-1));
        coordinates.add(coordinate);

        //coordinates in the middle row
        coordinate = String.valueOf(letter-1).concat(String.valueOf(number));
        coordinates.add(coordinate);
        coordinate = String.valueOf(letter+1).concat(String.valueOf(number));
        coordinates.add(coordinate);

        //coordinates in the lower row
        coordinate = String.valueOf(letter-1).concat(String.valueOf(number+1));
        coordinates.add(coordinate);
        coordinate = String.valueOf(letter).concat(String.valueOf(number+1));
        coordinates.add(coordinate);
        coordinate = String.valueOf(letter+1).concat(String.valueOf(number+1));
        coordinates.add(coordinate);

        return coordinates;
    }
	
	public int getUse(){
    	return this.usage;
    }

    @Override
    public void resetUsage() {
        this.usage = 1;
    }
}
