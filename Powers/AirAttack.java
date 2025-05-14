public abstract class AirAttack implements Power{

    private int usage = 1;

    public boolean use(char column){        //first control of power usage
        if(this.usage > 0){
            Attack(column);
            this.usage--;
            return true;
        } else {
            return false;
        }
    }

    public void Attack(char letter){       //main power command
        // invocation of the attack function for the column described by the variable "letter"
    }

    @Override
    public void resetUsage() {
        this.usage = 1;
    }

}
