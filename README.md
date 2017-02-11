# Data-Text-File-Querier
This is a tool that enables basic SQL-Like queries directly on a text file containing data. It even accommodates for files that have header lines. If you find this helpful and have any questions about how to use it or suggestions on how it can be improved feel free to contact me. 

#How to Use:
1. You can utilize this as a regular File.class object by making objects of FileUtil with the path to the file.

2. Use it's static methods which all returns a list of strings that satisfies the given criteria

For e.g.:
Let say we have a text file on our Desktop called "test.txt" at path = " ..\Desktop\test.txt". 
The format of file is as follows:
    AGE   NAME    GENDER
    24     Riko     M
    34     Jane     F
    12     Joe      M
    50     Mary     F

1. Create a ColumnHeaderSpecifier object to define coloumns of file (as list of strings) in the order they appear in the fiile and also we define how many lines need to be skiped in file. Because our file has the first line as the coloumn names we send 1 as part of the constructor:

ColumnHeaderSpecifier c = new ColumnHeaderSpecifier( Arrays.asList( "AGE","NAME","GENDER"),1);

2. Set the coloumn names for the tool (the good thing with doing it this way is that you can dynamically query various files in a logical way):
>FileUtil.setColumnHeaderSpecifier(c);
       
 You can now interact with the file however you chose using the predicates given of create your own. I will go through the operations
 
 |Main Operations|
 |------|
 |Get all lines in file that satisfies given criteria|
 |Remove all lines in file that satisfies given criteria|
 |Change column of all lines in file that satisfies given criteria to value|
 
 *Get all lines in file that satisfies given criteria:*
 >String result = FileUtil.linesWith(path,  
 >       Arrays.asList(FileUtil.greaterThanOrEqual("AGE","15"), 
 >                      FileUtil.equals("NAME", "RIKO"), 
 >                            FileUtil.equals("GENDER", "M")))
      
*Remove all lines in file that satisfies given criteria:*      
> FileUtil.deleteLinesWith(path, "AGE", ">=", "50");
> FileUtil.deleteLinesWith(path, Arrays.asList(FileUtil.greaterThanOrEqual("AGE","50"));

*Change column of all lines in file that satisfies given criteria to value:*
Here we will change the name of all entries with age>= 50 to "XXXX":
>        FileUtil.updateLinesWith(path, "AGE", ">=", "50", "NAME","XXXX");
