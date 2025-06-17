import java.util.HashSet;
import java.util.Set;

public abstract class AirAttack implements Power{

    private int usage = 1;

    public boolean use(char column){        //first control of power usage
        if(this.usage > 0){
            CoordinatesGenerator(String.valueOf(column));
            this.usage--;
            return true;
        } else {
            return false;
        }
    }

    //generation of the coordinates of the column using the patern first letter than the number (ex. A1 or A10)
    public Set<String> CoordinatesGenerator(String AreaCenter){
        String coordinate;

        Set<String> coordinates = new HashSet<>();

        for (int i = 1; i<=10; i++){
            coordinate = AreaCenter.concat(String.valueOf(i));
            coordinates.add(coordinate);
        }

        return coordinates;
    }

    @Override
    public void resetUsage() {
        this.usage = 1;
    }

}
