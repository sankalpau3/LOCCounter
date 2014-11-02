//Counts LOC according to set programming language
//TSP Cycle 1 only supports inputs with Java language.
//Next cycle will support C# and C++

public class Counter {
	
	public enum PL {
		JAVA, CPP, CSHARP
	}
	
	private PL setPL = PL.JAVA; //Default value is Java
	
	public Counter(PL ProgrammingLanguage){
		setPL = ProgrammingLanguage;
	}
	
	public int CountLOC(String code){
		int LOC = 0;
		boolean inComment = false;
		
		if(setPL == PL.JAVA || setPL == PL.CPP || setPL == PL.CSHARP){
			String[] sp = code.split("\n");
			
			for(int i = 0; i < sp.length; i++){
				sp[i] = sp[i].trim();
				sp[i] = sp[i].replaceAll("\\t|\\s|\\n", "");

				if(inComment && sp[i].contains("*/")){
					inComment = false;
					continue;
				}else if(!inComment && sp[i].contains("/*")){
					inComment = true;
					if(sp[i].contains("*/"))
							inComment = false;
					continue;
				}else if(inComment){
					continue;
				}
				if(sp[i].startsWith("//"))
					continue;
				if(sp[i].startsWith("/*"))
					continue;
				if(sp[i].startsWith("*/"))
					continue;
				if(sp[i].length() == 0)
					continue;
				if(sp[i].startsWith(";"))
					continue;
				LOC++;
				
			}
		}
		
		return LOC;
	}
	
	
}
