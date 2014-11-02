import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class DataBase {

	public DataBase(){
		Path path = Paths.get(getDataBasePath());
		System.out.println("DB = " + getDataBasePath());
		
		if (Files.notExists(path)) {
			boolean success = (new File(getDataBasePath())).mkdirs();
			if (!success) {
			    // Directory creation failed
				System.out.println("Could not create DB folder!");
			}
		}
	}
	
	public String getDataBasePath(){
		 File file = new File(".");  
	     try {
			return file.getCanonicalPath() + "\\DB";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	public String getOldVersion(File f) throws IOException{
		//Returns empty string if database does not hold old version of source code.
		 File file = new File(getDataBasePath());  
	     File[] files = file.listFiles();  
	     System.out.println("Current dir : " + file.getCanonicalPath());
	     for (int fileInList = 0; fileInList < files.length; fileInList++)  
	     {  
	     	System.out.println(files[fileInList].toString()); 
	     	if(f.getName().equalsIgnoreCase(files[fileInList].getName())){
	     		return files[fileInList].getAbsolutePath();
	     	}
	     }
	     return "";
	}
	public void copyToDB(File f) throws IOException{
		Path source = Paths.get(f.getAbsolutePath());
		Path newdir = Paths.get(getDataBasePath());
		Files.copy(source, newdir.resolve(source.getFileName()));
	}
	public void copyToDB(String name, String src) throws IOException{
		PrintWriter writer = new PrintWriter(getDataBasePath() + "\\" + name, "UTF-8");
		writer.println(src);
		writer.close();
	}
	public void writeTo(String name, String src) throws IOException{
		PrintWriter writer = new PrintWriter(name, "UTF-8");
		writer.println(src);
		writer.close();
	}
}
