import java.util.Map;

public class Scanner implements Power{

    private int usage = 1;


    public boolean use(String AreaCenter){        //first control of power usage
        if(this.usage > 0){
            if(AreaControl(AreaCenter)) {
                //call of the attack function
                this.usage--;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /*
     * The input  must be the coordinate of the center of the 3x3 area
     * there is the necessity to control if the area is over the borders
     * of the game board.
     * The coordinate that cannot be allowed is those who contains the letter A/L
     * or the number 1/10
     */
    public boolean AreaControl(String AreaCenter){



        return false;
    }

    @Override
    public void resetUsage() {
        this.usage = 1;
    }
}
