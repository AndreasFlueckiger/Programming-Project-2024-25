package main.bot;

import java.util.HashSet;
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

    public Set<String> Positioning() {

        

        return null;
    }

}
