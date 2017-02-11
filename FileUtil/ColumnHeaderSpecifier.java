package FileUtil;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Riko
 */
public class ColumnHeaderSpecifier {
    protected List<String> colNames;
    private int numOfHeaderLinesToSkip ;
    
    
    /**
     * 
     * @param colNames: A list of strings where each string represents one column title
     * @param n : this denotes how many lines in the file needs to be skipped before data is seen
     */
    public ColumnHeaderSpecifier(List<String> colNames, int n) {
        this.colNames = colNames;
        numOfHeaderLinesToSkip = n;
    } 
    protected int getLinesToSkip(){
        return numOfHeaderLinesToSkip;
    }
    
}
