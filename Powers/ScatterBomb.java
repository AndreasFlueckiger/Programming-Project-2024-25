import java.util.Set;

public abstract class ScatterBomb implements Power{

    private int usage = 1;

    public boolean use(Set<String> area) {
        if(this.usage > 0){
            Attack(area);
            this.usage--;
            return true;
        } else {
            return false;
        }
    }

    public void Attack(Set<String> area) {
        // invocation of the attack function that hits a 3x3 area
    }

    @Override
    public void resetUsage() {
        this.usage = 1;
    }
}
