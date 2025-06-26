package main.bot;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//the letter for the columns must be between 65 an 76 (74 and 75 are not valid) and the numbers form 1 to 10
public class EasyBot {

    private Set<String> MoveSet;
    
    public EasyBot(){
        MoveSet = new HashSet<>();
    }

    public String Move(){

        boolean isValidCoor = false;
        String coordinate = "";

        while(!isValidCoor) {

            int randomRow = (int) (Math.random() * 11);
            char randomColumn = (char) ((int) (Math.random() * 11) + 65);

            if(!(randomColumn == 'K' || randomColumn == 'J') && isValid(MoveSet, coordinate)){
                coordinate = randomColumn + Integer.toString(randomRow);
                isValidCoor = true;
            }

        }

        MoveSet.add(coordinate);

        return coordinate;
    }

    private boolean isValid(Set<String> MoveSet, String coordinate){
        return MoveSet.contains(coordinate);
    }

    /*
     * Positioning must create the start coordinates to position the different ships on the board
     * it have also to control if the start coordinate allows to put the ship completly inside the board
     * and not to overwrite someone.
     */
    public List<String> Positioning() {

        ShipsPositions(Coordinate(), shipSize);

        return null;
    }

    //creates a storage for every ship and its associated coordinates
    private Map<Integer, String> ShipsPositions(String StartCoordinate, int shipSize){

        //generation of a number of coordinates based on the type of the ship
        switch (shipSize) {
            case 4:
                
                break;
            case 3:
                
                break;
            case 2:
                
                break;
            case 1:
                
                break;
            default:
                throw new AssertionError();
        }


        for(int i = 0; i<shipSize; i++){
            
        }

        return null;
    }

    //coordinate generation method
    private String Coordinate(){
        boolean isValidCoor = false;
        String coordinate = "";

        while(!isValidCoor) {

            int randomRow = (int) (Math.random() * 11);
            char randomColumn = (char) ((int) (Math.random() * 11) + 65);

            if(!(randomColumn == 'K' || randomColumn == 'J')){
                coordinate = randomColumn + Integer.toString(randomRow);
                isValidCoor = true;
            }

        }

        return coordinate;
    }

}
