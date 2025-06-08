package main.rules.designPatterns;

public interface Observable {

    //I deleted the public before these 3 statements
	 void addObserver(Observer o);
     void removeObserver(Observer o);
     Object get();
	
}
