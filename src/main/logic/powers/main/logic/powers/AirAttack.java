package main.logic.powers;

import java.util.HashSet;
import java.util.Set;

public abstract class AirAttack implements Power{

    private int usage = 1;

    /*
     * This method is the first method called by the use of the powers, it also controls if it is possible to use the before
     */
    public Set<String> use(char column){        
        if(this.usage > 0){
        	//there the decrease of the usage function so the player can't use the power multiple times
            this.usage--;
            return CoordinatesGenerator(String.valueOf(column));
        } else {
            return null;
        }
    }

    //generation of the coordinates of the column using the pattern first letter than the number (ex. A1 or A10)
	public Set<String> CoordinatesGenerator(String AreaCenter){
        String coordinate;

        Set<String> coordinates = new HashSet<>();

        for (int i = 1; i<=10; i++){
            coordinate = AreaCenter.concat(String.valueOf(i));
            coordinates.add(coordinate);
        }

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
