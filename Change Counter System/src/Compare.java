import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;


public class Compare {

	private int totalAddedLOC = 0;
	private int totalDeletedLOC = 0;
	private int totalModdedLOC = 0;
	private int totalLOC = 0;
	
	public Compare(){
		
	}
	public Compare(LinkedList<Differ.Diff> d, ChangeHeader c, String author, String reason){
		CompareSources(d, c, author, reason);
	}
	private void CompareSources(LinkedList<Differ.Diff> d, ChangeHeader c, String author, String reason){
		Counter counter = new Counter(Counter.PL.JAVA);
		int curChanges = c.getTotalChanges();
		int LOC;
		boolean bMod = false;
		Differ.Diff lastDiff = null;
		
		for (Differ.Diff myDiff : d) {

			
			 
			  
			  if(myDiff.operation == Differ.Operation.EQUAL){
				  lastDiff = myDiff;
				  continue;
			  }
				  
			 if(bMod){
					  bMod = false;
					  setTotalModdedLOC(1);
					  if(myDiff.operation == Differ.Operation.INSERT){
						  String[] p = myDiff.text.split("\n");
						  myDiff.text = "";
						  for(int i = 0; i < p.length; i++){
							  
							  if(i==1){
								  LOC = counter.CountLOC(p[i]);
								  if(LOC == 0)
								  {
									    lastDiff = myDiff;
							    		continue; 
								  }
								  curChanges++;
								  myDiff.text = myDiff.text + "\r\n//#"  + curChanges + "++ Added LOC: " + LOC +  "\r\n" + p[i] + "\n";
								  c.addChange("#"  + curChanges + " = " + reason + " by " + author + ", Added   LOC = " + LOC);
								  setTotalAddedLOC(LOC);
							  }else{
								  myDiff.text = myDiff.text + p[i] + "\n";
							  }
						  }
					  }
					  lastDiff = myDiff;
					  continue;

					  
			  }
			  if(!myDiff.text.contains("\n") && !myDiff.text.contains("\r\n") && lastDiff != null){
				  LOC = counter.CountLOC(myDiff.text);

			    	if(LOC == 0){
			    		lastDiff = myDiff;
			    		continue;
			    	}
				  bMod = true;

				  String[] p = lastDiff.text.split("\n");
				  lastDiff.text = "";
				  for(int i = 0; i < p.length-1; i++){
					  lastDiff.text = lastDiff.text + p[i] + "\n";
				  }
				  curChanges++;
				  c.addChange("#"  + curChanges + " = " + reason + " by " + author + ", Modification");
				  lastDiff.text = lastDiff.text + "\n" + "//#"  + curChanges + "%% Modification";
				  lastDiff.text = lastDiff.text + "\r\n" + p[p.length-1];
				  myDiff.text = "";
				  lastDiff = myDiff;
				  continue;
			  }
			  
			  


		      if (myDiff.operation == Differ.Operation.INSERT) {
		    	LOC = counter.CountLOC(myDiff.text);

		    	if(LOC == 0){
		    		lastDiff = myDiff;
		    		continue; //Its not a line of code
		    	}
			    curChanges++;
			    myDiff.text = "//#"  + curChanges + "++ Added LOC: " + LOC +  "\r\n" + myDiff.text;
			    c.addChange("#"  + curChanges + " = " + reason + " by " + author + ", Added   LOC = " + LOC);
			    setTotalAddedLOC(LOC);
		      }
		      if (myDiff.operation == Differ.Operation.DELETE) {	
		    	LOC = counter.CountLOC(myDiff.text);

		    	if(LOC == 0){
		    		lastDiff = myDiff;
		    		continue; //Its not a line of code
		    	}
			    curChanges++;
			    myDiff.text = "\r\n" + "//#"  + curChanges + "-- Deleted LOC: " + LOC +  "\r\n";///*\r\n" + myDiff.text + "\r\n*/";
			    c.addChange("#"  + curChanges + " = " + reason + " by " + author + ", Deleted LOC = " + LOC);
		    	setTotalDeletedLOC(LOC);
		      }

		      lastDiff = myDiff;
		}
		String ret = "";
		for (Differ.Diff myDiff : d) {
			ret = ret + myDiff.text;	
		}
		DataBase db = new DataBase();
		try {
			db.writeTo(db.getDataBasePath() + "\\aaa", ret);
			ret = readFile(db.getDataBasePath() + "\\aaa");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File(db.getDataBasePath() + "\\aaa");
		file.delete();
		System.out.println("TOTAL LOC = " + counter.CountLOC(ret));
		setTotalLOC(counter.CountLOC(ret)-totalModdedLOC-c.getTotalMods());	
	}



	public int getTotalAddedLOC() {
		return totalAddedLOC;
	}
	public void setTotalAddedLOC(int totalAddedLOC) {
		this.totalAddedLOC += totalAddedLOC;
	}
	public void setTotalModdedLOC(int totalModdedLOC) {
		this.totalModdedLOC += totalModdedLOC;
	}
	public int getTotalDeletedLOC() {
		return totalDeletedLOC;
	}
	public void setTotalDeletedLOC(int totalDeletedLOC) {
		this.totalDeletedLOC += totalDeletedLOC;
	}
	public int getTotalLOC() {
		return totalLOC;
	}
	public void setTotalLOC(int totalLOC) {
		this.totalLOC = totalLOC;
	}
	
	String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
}


