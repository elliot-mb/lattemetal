import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Assembler {
    // this class contains the basic assembler that turns our raw instructions into machine readable objects

    private final String fileName;
    private final ArrayList<String> rawLines;
    private static final Boolean FAIL = false;
    private static final Boolean PASS = true;
    private static final int EOF = -1;
    private static final String DOT = ".";
    private static final String EXT = "latte";

    /**
     *
     * @param fileName must have the '.latte' file extension, please note set files according to where you run the
     *                 compile&run script
     */
    public Assembler(String fileName) throws RuntimeException{
        if(!extensionCheck(fileName)) throw new RuntimeException("Assembler: fileName has the wrong extension, requires '." + EXT + "' file");
        this.fileName = fileName;
        this.rawLines = new ArrayList<String>();
    }

    /**
     * @return whether it passed the check or not
     */
    private static boolean extensionCheck(String fileName){
        String[] pathSplit = fileName.split("/"); //always at least 1 thing in here
        String[] dotSplit = Utils.last(pathSplit).split("\\.");
        if(dotSplit.length <= 1 ||
           !Utils.last(dotSplit).equals(Assembler.EXT)){
            return FAIL;
        }
        return PASS;
    }

    /**
     * read the assembly program
     * @return pass (true) or fail (false) at finding our file
     */
    public boolean readFile(){
        try(FileReader reader = new FileReader(fileName, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader)){
            String ln = br.readLine();
            while(ln != null){
                rawLines.add(ln);
                ln = br.readLine();
            }
        }catch(FileNotFoundException err){
            System.out.println(err.getMessage());
            return FAIL; //failure
        }catch(IOException err){
            System.out.println(err.getMessage());
            return FAIL;
        }

        return PASS;
    }

    public ArrayList<String> getRawLines(){
        return rawLines; //this cant be modified because strings are immutable
    }

}
