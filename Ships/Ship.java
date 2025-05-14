import java.util.Random;

public class Ship {
    private int lives;
    private String type;
    private int id;

    public Ship(String type, int lives){
        this.lives = lives;
        this.type = type;
        idGenerator();
    }
    //getter/setter

    public int getLives(){
        return this.lives;
    }

    public String getType(){
        return this.type;
    }

    public int getId(){
        return this.id;
    }

    public void setLives(int lives){
        this.lives = lives;
    }

    public void setType(String Type){
        this.type = Type;
    }

    //id generation

    public void idGenerator(){
        Random rand = new Random();
        this.id = rand.nextInt(50);
    }

}
