import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import au.com.bytecode.opencsv.CSVWriter;
import org.javatuples.Pair;

public class ClientLog {
    private ArrayList<Pair<Integer, Integer>> shoppingActions;

    public ClientLog() {
        shoppingActions = new ArrayList<>();
    }

    public void log(int productNum, int amount){
        shoppingActions.add(new Pair<>(productNum, amount));
    }

    public void exportAsCSV(File txtFile){
        try (CSVWriter writer = new CSVWriter(new FileWriter(txtFile))){
            writer.writeNext(new String[]{"productNum", "amount"});

            for (Pair<Integer, Integer> action : shoppingActions) {
                writer.writeNext(new String[]{action.getValue0().toString(), action.getValue1().toString()});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
