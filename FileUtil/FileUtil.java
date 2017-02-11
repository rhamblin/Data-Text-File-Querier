package FileUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.awt.Desktop;

/**
 * Overview: 
 * This utility class provides an API to operate on a text-file handling actions such as retrieval, writing, updating and deleting of information to
 * a file.
 *
 * @author Riko Hamblin, Ibukun Oluwayomi
 */
public final class FileUtil extends File{
   
    /**
     * Effect: 
     * Returns all lines in filePath
     * 
     * @param filePath: A string denoting the full path to the file to be used
     * @return list representing all lines contained in filePath. Each string corresponds to one line in the file.
     */   
    public static List<String> getAllLines(String filePath) {
        List<String> resultFromFile = new ArrayList<>();
        
        if(filePath == null) return resultFromFile; //return an empty array if null if given
        
        //if filePath is blank return an empty array
        filePath = filePath.trim();
        if(filePath.isEmpty()) return resultFromFile;
        
        createFile(filePath);
        
        //add all lines from file top list 
        BufferedReader br = null;
        try {
            File f1 = new File(filePath);
            br = new BufferedReader(new FileReader(f1));

            String line;
            
            //remove header lines
            for(int i =0;i<FileUtil.numOfLinesForHeader; i++)
                line = br.readLine();
            
            while ((line = br.readLine()) != null) {
                if(line.isEmpty()) continue;
                resultFromFile.add(line);
            }
            br.close();
        }catch(Exception e){}
        return resultFromFile;

    }
    
    /**
     * Effect:
     * Returns all lines from filePath that matches the criteria given
     * 
     * Requires: 
     * column is case-sensitive and must be consistent with file database specifications. Also , filename must be the full path to the file
     *
     * @param filePath: A string denoting the full path to the file to be used
     * @param column: Name of column to be queried
     * @param relation: Choose one: "&gt","&gt=","&lt","&lt=","="
     * @param value: value that query will be based on
     * @return list representing all lines contained in filePath that matched criteria. Each string corresponds to one line.
     */
    public static List<String> linesWith(String filePath, String column, String relation, String value) {
        Predicate<String> predicate = null;
        switch (relation) {
            case "<" : 
                predicate = lessThan(column,value);
                break;
            case "<=":
                predicate = lessThanOrEqual(column, value);
                break;
            case ">" : 
                predicate = greaterThan(column,value);
                break;
            case ">=":
                predicate = greaterThanOrEqual(column, value);
                break;
            case "=":
            case "==": 
                predicate = equals(column, value);
                break;
            default: throw new IllegalArgumentException();
                
        }

        return linesWith(filePath, Arrays.asList(predicate));
    }

    /**
     * Effect:
     * Returns all lines from filePath that matches the criteria given.
     * 
     * Requires: 
     * Also , filename must be the full path to the file
     * 
     * @param filePath: A string denoting the full path to the file to be used
     * @param relations: A list of predicates denoting the criteria that the file will be queried upon.
     * @return list representing all lines contained in filePath that matched criteria. Each string corresponds to one line.
     */
    public static List<String> linesWith(String filePath, List<Predicate<String>> relations) {//IllegalArgumentException{
        List<String> resultFromFile = getAllLines(filePath);
        
        if(relations == null || relations.isEmpty()) throw new IllegalArgumentException("No predicates given");
        
        Predicate<String> compositePredicate = relations.stream()
                .reduce(s -> true, Predicate::and);
        resultFromFile = resultFromFile.stream()
                .filter(compositePredicate)
                .collect(Collectors.toList());
        return resultFromFile;
    }

    /**
     * Effect:
     * Finds all lines in filePath that match criteria and updates the specified column to newValue
     * 
     * Requires:
     * column, colToBeChanged is case-sensitive and must be consistent with file database specifications. Also , filename must be the full path to the file
     * 
     * @param filePath: A string denoting the full path to the file to be used
     * @param column: Name of column to be queried
     * @param relation: Choose one: "&lt=","&gt=","&lt","&lt=","="
     * @param originalValue: value that query will be based on
     * @param colToBeChanged: Name of column to be changed
     * @param newValue: value that will be placed in colToBeChanged
     */
    public static void updateLinesWith(String filePath, String column, String relation, String originalValue, String colToBeChanged,String newValue){
        createFile(filePath);
        List<String> lines = linesWith(filePath, column, relation, originalValue);
        
        for(String s: lines){
        String newString = "";
            String[] info = s.split(" ");
            info[lookupTable.get(colToBeChanged)] = newValue;
            
            for(String x: info)
              newString += x + " ";
            
            rewriteContentOfFile(filePath,s, newString);
        }
        
    }
 
    /**
     * Effect:
     * Finds all lines in filePath that match criteria and updates the specified column, colToBeChanged, to newValue
     * 
     * Requires:
     * column, colToBeChanged is case-sensitive and must be consistent with file database specifications. Also , filename must be the full path to the file
     * 
     * @param filePath: A string denoting the full path to the file to be used
     * @param relations: A list of predicates denoting the criteria that the file will be queried upon
     * @param colToBeChanged: Name of column to be changed
     * @param newValue: value that will be placed in colToBeChanged
     */
    public static void updateLinesWith(String filePath, List<Predicate<String>> relations, String colToBeChanged,String newValue){
        createFile(filePath);
        List<String> lines = linesWith(filePath, relations);
        
        for(String s: lines){
        String newString = "";
            String[] info = s.split(" ");
            info[lookupTable.get(colToBeChanged)] = newValue;
            
            for(String x: info)
              newString += x + " ";
            
            rewriteContentOfFile(filePath,s, newString);
        }
        
    }

    /**
     * Effect: 
     * Deletes all lines from filePath that satisfies the criteria
     * 
     * Requires: 
     * column is case-sensitive and must be consistent with file database specifications. Also , filename must be the full path to the file
     
     * @param filePath: A string denoting the full path to the file to be used
     * @param column : Name of column to be queried
     * @param relation: Choose one: "&lt=","&gt=","&lt","&lt=","="
     * @param value: value that query will be based on
     */
    public static void deleteLinesWith(String filePath, String column, String relation, String value){
        createFile(filePath);   
        List<String> lines = linesWith(filePath, column, relation, value);
        
        for(String s: lines){
            deleteLineFromFile(filePath, s);
        }
        
    }
 
    /**
     * Effects: 
     * Deletes all lines from filePath that satisfies the criteria
     *
     * @param filePath: A string denoting the full path to the file to be used
     * @param relations: A list of predicates denoting the criteria that the file will be queried upon
     */
    public static void deleteLinesWith(String filePath, List<Predicate<String>> relations ){
        createFile(filePath);
        
        List<String> lines = linesWith(filePath, relations);
        
        for(String s: lines){
            deleteLineFromFile(filePath, s);
        }
        
    }
    
    /**
     * Effect:
     * Returns true if s in in filePath. False otherwise.
     * 
     * @param filePath: A string denoting the full path to the file to be used
     * @param content: String that needs to be verified
     * @return Returns true if s in in filePath. False otherwise.
     */
    public static boolean isStringInFile(String filePath, String content) {

        createFile(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            //open file 
            String currentLine;

            //while there are lines in the file
            while ((currentLine = br.readLine()) != null) {
                if (currentLine.equals(content)) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     *Effects:
     * Appends the specified line, line, to file.
     * 
     * @param filePath : A string denoting the full path to the file to be used
     * @param line: line to be added to filePath
     */
    public static void addLineToFile(String filePath, String line) {
        if(line.trim().isEmpty() || line == null) return;
        
        createFile(filePath);
        File file = new File(filePath);

        try {

            FileWriter fileW = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter buffw = new BufferedWriter(fileW);
            buffw.write(line);
            buffw.newLine();
            buffw.close();

        } catch (FileNotFoundException ex) {

        } catch (Exception e) {

        }

    }

    /**
     * Effects:
     * Replaces all lines, originalContent with newContent.
     * 
     * @param filePath: A string denoting the full path to the file to be used
     * @param originalContent: Line to be overwritten
     * @param newContent: line to be used for overwriting
     * @return True if lines were replaced. False if error occured.
     */
    public static boolean rewriteContentOfFile(String filePath, String originalContent, String newContent) {
        if(originalContent.equals(newContent) || originalContent == null || newContent == null || originalContent.trim().isEmpty())
                return false;
        
        createFile(filePath);

        String oldFileName = filePath;

        BufferedReader br = null;
        BufferedWriter bw = null;

        ArrayList<String> lines = new ArrayList<>();
        try {
            File f1 = new File(oldFileName);
            br = new BufferedReader(new FileReader(f1));

            String line;
            while ((line = br.readLine()) != null) {
                if(line.equals(originalContent)) {
                    lines.add(newContent);
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                lines.add(line);

            }
            br.close();

            FileWriter fw = new FileWriter(f1);
            try (BufferedWriter out = new BufferedWriter(fw)) {
                for (String s : lines) {
                    out.write(s);
                    if(!s.isEmpty()) out.newLine();
                }
                out.flush();
                out.close();
            }

        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                //
            }
            try {

                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                //
            }

        }
        return true;
    }

    /**
     * Effects:
     * Deletes the line specified from filePath
     * 
     * @param filePath: A string denoting the full path to the file to be used
     * @param content: line to be deleted
     * @return True if successful; false otherwise
     */
    public static boolean deleteLineFromFile(String filePath, String content) {
        if(content.trim().isEmpty() || content == null) return false;
        
        createFile(filePath);
        
        rewriteContentOfFile(filePath, content, "");

        return true;
    }

    //Predicates:
    /**
     * Predicate Abstraction:
     * Is the {col} column of string,e, {less than} {value}? true : false
     * 
     * @param col: Coloumn of interest
     * @param value: value that predicate is based on
     * @return A predicate representing  the above question
     */
    public final static Predicate<String> lessThan(String col, String value){
       return e -> {
                    int n = lookupTable.get(col);
                    String  str = Arrays.asList(e.split(" ")).get(n);
                    
                 try{
                     int num = Integer.parseInt(value);
                     int valueInFile = Integer.parseInt(str);
                     return valueInFile < num;
                     
                    }catch (NumberFormatException exp){
                        int result = str.compareTo(value);
                     
                        return result < 0;
                    }
                    
                    
                };

    }
    
    /**
     * Predicate Abstraction:
     * Is the {col} column of string,e, {greaterThan} {value}? true : false
     * 
     * @param col: Column of interest
     * @param value: value that predicate is based on
     * @return A predicate representing  the above question
     */
    public final static Predicate<String> greaterThan(String col, String value){
        return e-> !(lessThanOrEqual(col,value).test(e));
    }
   
     /**
     * Predicate Abstraction:
     * Is the {col} column of string,e, {equal to} {value}? true : false
     * 
     * @param col: Column of interest
     * @param value: value that predicate is based on
     * @return A predicate representing  the above question
     */
    public final static Predicate<String> equals(String col, String value) {

        return e -> {
                    int n = lookupTable.get(col);
                    String  str = Arrays.asList(e.split(" ")).get(n);
                   

                    return str.equals(value);
                };}
    
     /**
     * Predicate Abstraction:
     * Is the {col} column of string,e, {less than or equals to} {value}? true : false
     * 
     * @param col: Column of interest
     * @param value: value that predicate is based on
     * @return A predicate representing  the above question
     */
    public final static Predicate<String> lessThanOrEqual(String col, String value){
        return e-> lessThan(col,value).test(e) || equals(col,value).test(e);
    } 
    
     /**
     * Predicate Abstraction:
     * Is the {col} column of string,e, {greater than or equal to} {value}? true : false
     * 
     * @param col: Column of interest
     * @param value: value that predicate is based on
     * @return A predicate representing  the above question
     */
    public final static Predicate<String> greaterThanOrEqual(String col, String value) {
        return e-> !(lessThan(col,value).test(e));
    }
//---------------------------------------------------------------------------------------------------------------
   //if file has a header at top of file e.g. "AGE NAME GENDER" then set to 1;
    //if file has a header at top of file e.g.
    //  "AGE NAME GENDER
    //   ----------------
    //  then set to 0"
   //if 0 it means no headers exist. Reading of file will begin from first line
    private static int numOfLinesForHeader; //
    private static ColumnHeaderSpecifier chs ;
    private static Map<String, Integer> lookupTable ;  
    //configure method below based on the structure of ilfe and define the order 
    //in which the data columns are stored:   
    private static final Map<String, Integer> createMap(){
        Map<String,Integer> lookupTable = new HashMap<String,Integer>();
        int i=0;
        for(String s: chs.colNames)
        {lookupTable.put(s, i);
        i++;
        }
        
        return lookupTable;
    }
    private FileUtil(String filename) {super(filename);}
    private static void createFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {  //if file doesnt exist then create it
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void setColumnHeaderSpecifier(ColumnHeaderSpecifier chs){
        FileUtil.chs = chs;
        numOfLinesForHeader = chs.getLinesToSkip();
        lookupTable = createMap();
    }
       
/**
 * main method used to test api
 * @param args 
 */
    public static void main(String[] args) {
    /*    
        String path = System.getProperty("user.home") 
                + System.getProperty("file.separator")
                    + "Desktop" + System.getProperty("file.separator")
                        +"test.txt";
      
        ColumnHeaderSpecifier c = new ColumnHeaderSpecifier(Arrays.asList(
                "AGE","NAME","GENDER"),1);
        
        FileUtil.setColumnHeaderSpecifier(c);
       
        System.out.println("Before\n"+FileUtil.getAllLines(path));
              
        FileUtil.addLineToFile(path, "100 J M");
        FileUtil.addLineToFile(path, "222 JF M");
        System.out.println("\nNEW INFO:\n"+FileUtil.getAllLines(path));
        
        FileUtil.updateLinesWith(path, "AGE", ">=", "50", "NAME","XXXX");
        System.out.println("\nUpdated:\n"+FileUtil.getAllLines(path));

        FileUtil.deleteLinesWith(path, "AGE", ">=", "100");
        System.out.println("\nAfter removal:\n"+FileUtil.getAllLines(path));
        
        System.out.println("\nQuerying:\n" + FileUtil.linesWith(path,  Arrays.asList(FileUtil.greaterThanOrEqual("AGE","15"), 
                                                 FileUtil.equals("NAME", "RIKO"), FileUtil.equals("GENDER", "M"))));
    
     */      
    }
 
}
