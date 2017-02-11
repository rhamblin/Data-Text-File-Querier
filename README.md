# Data-Text-File-Querier
This is a tool that enables basic SQL-Like queries directly on a text file containing data. It even accommodates for files that have header lines. If you find this helpful or think this may be what you're looking but the documentation is clear enough and have any questions about how to use it or suggestions on how it can be improved feel free to contact me. 

Feel free to use in your commercial/non-commercial applications. My only clause is you let me know if it helped you and what you used it to do!

#How to Use:
You can utilize this as a regular File.class object by making objects of FileUtil with the path to the file or you can use it's static methods which all returns a list of strings that satisfies the given criteria. I will be explaining the latter as this is what I implemented. I only extended File.class so this is compatible in all File.class situation.

For e.g.:
Let say we have a text file on our Desktop called "test.txt" at path = " ..\Desktop\test.txt";
The format of file is as follows:
    AGE   NAME    GENDER
    24     Riko     M
    34     Jane     F
    12     Joe      M
    50     Mary     F

1. Import library:
> import FileUtil.*;

2. Create a ColumnHeaderSpecifier object to define coloumns of file (as list of strings) in the order they appear in the fiile and also we define how many lines need to be skiped in file. Because our file has the first line as the coloumn names we send 1 as part of the constructor:
>ColumnHeaderSpecifier c = new ColumnHeaderSpecifier( Arrays.asList( "AGE","NAME","GENDER"),1);

3. Set the coloumn names for the tool (the good thing with doing it this way is that you can dynamically query various files in a logical way simply by switchin the ColumnHeaderSpecifier object):
>FileUtil.setColumnHeaderSpecifier(c);
       
 4. You can now interact with the file however you chose using the predicates given of create your own. I will go through the operations
 
 |Main Operations|
 |------|
 |Get all lines in file that satisfies given criteria|
 |Remove all lines in file that satisfies given criteria|
 |Change column of all lines in file that satisfies given criteria to value|

 |Supported Queries|
 |------|
 |greaterThan()|
 |greaterThanOrEqual()|
 |LessThan()|
 |LessThanOrEqual()|
 |Equals()|
#Main Operations:
+ *Get all lines in file that satisfies given criteria:*
 ..1. If you want to combine queries:
 >String result = FileUtil.linesWith(path,  
 >       Arrays.asList(FileUtil.greaterThanOrEqual("AGE","15"), 
 >                      FileUtil.equals("NAME", "BOBBY"), 
 >                            FileUtil.equals("GENDER", "M")))
 
 The result variable will contain an empty list of strings as there is no people named Bobby who are of gender M who are older than 15  the following list of strings

 >[]
 
 ..2. If you only have one criteria
 >String result = FileUtil.linesWith(path, "GENDER","==","M");
 
 The result variable will contain the following list of strings:

 >[[24 Riko M],[12 Joe M]]
 
+ *Remove all lines in file that satisfies given criteria:*      
  > FileUtil.deleteLinesWith(path, "AGE", ">=", "50");


 *Or by doing:*

    > FileUtil.deleteLinesWith(path, Arrays.asList(FileUtil.greaterThanOrEqual("AGE","50"));



  The current file would be:
    AGE   NAME    GENDER
    24     Riko     M
    34     Jane     F
    12     Joe      M

+ *Change column of all lines in file that satisfies given criteria to value:*
  Here we will change the name of all entries with age>= 20 to "XXXX":
 >         FileUtil.updateLinesWith(path, "AGE", ">=", "20", "NAME","XXXX");


  The current file would be:
    AGE   NAME    GENDER
    24     XXXX     M
    34     XXXX     F
    12     Joe      M
    
