import java.io.*;
import java.util.List;

public class De_Serialized_data implements Serializable{

    private static final long serialVersionUID = 1L;
    private String GameResult;
    private String Player1;
    private String Player2;

    public De_Serialized_data(String GameResult, String UserName1, String UserName2) {
        this.GameResult = GameResult;
        this.Player1 = UserName1;
        this.Player2 = UserName2;
    }

    // Getters and setters
    public String getGameResult() {
        return this.GameResult;
    }

    public void setGameResult(String gameResult) {
        this.GameResult = gameResult;
    }

    public String getPlayer1() {
        return this.Player1;
    }

    public void setPlayer1(String userName) {
        this.Player1 = userName;
    }

    public String getPlayer2() {
        return this.Player2;
    }

    public void setPlayer2(String userName) {
        this.Player2 = userName;
    }

    //serialisation of the object list
    public static void DataSerialisation(List<De_Serialized_data> result) {
        try (FileOutputStream fileOut = new FileOutputStream("results_list.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(result);
            System.out.println("Serialized list is saved in results_list.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //deserialisation of the object list
    public static List<De_Serialized_data> DataDeserialisation() {
        try (FileInputStream fileIn = new FileInputStream("results_list.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (List<De_Serialized_data>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //method create for only test purpose
    @Override
    public String toString() {
        List<De_Serialized_data> results;

        results = DataDeserialisation();

        for (int i = 0; i < results.size(); i++) {
            System.out.println(results.get(i).getPlayer1()+" has "+ results.get(i).getGameResult()+ " against "+results.get(i).getPlayer2());
        }
        
        return null;
    }
}

