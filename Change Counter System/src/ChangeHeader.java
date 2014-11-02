import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;




public class ChangeHeader {
	private int totalChanges;
	private int totalMods;
	private int oldtotalChanges;
	private double currentVer;
	private String programname;
	private String separator = "****************************************";
	private String changeList[]; //Line-labels
	private String versionHistory[];
	private final String nl = "\r\n";
	
	public ChangeHeader(){
		changeList = new String[0];
		versionHistory = new String[0];
	}
	public boolean parseHeader(String s){
		if(!s.contains(separator))
			return false;
		
		String[] sub = s.substring(s.indexOf(separator), s.indexOf(separator, s.indexOf(separator) + separator.length())).split("\r\n|\r|\n");
		programname = sub[2].substring(2);
		currentVer = Double.parseDouble(sub[3].substring(2));
		totalChanges = Integer.parseInt(sub[5].substring(sub[5].indexOf("=") + 2));
		oldtotalChanges = totalChanges;
		changeList = new String[totalChanges];
		int c = 0;
		for(int i = 0; i < sub.length; i++){
			if(sub[i].contains("* #")){
				changeList[c] = sub[i].substring(2);
				if(changeList[c].contains("Modification"))
					totalMods++;
				c++;
			}
		}
		versionHistory = new String[(int) currentVer];
		c = 0;
		for(int i = 0; i < sub.length; i++){
			if(sub[i].contains("* -")){
				versionHistory[c] = sub[i].substring(2);
				c++;
			}
		}
		return true;
	}
	public String createHeader(int LOC){
		String header = "/* " + separator + nl + "*" + nl;
		header += "* " + programname + nl;
		if(LOC != -1)
			header += "* " + (currentVer+1.0)  + nl;
		else
			header += "* " + (currentVer)  + nl;
		header += "* " + nl;
		header += "* " + "Total Changes = " + (totalChanges == -1 ? 0 : totalChanges) + nl;
		header += "* Change List:" + nl;
		header += "* " + nl;
		if(changeList.length != 0){
			for(int i = 0; i < changeList.length; i++)
				header += "* " + changeList[i] + nl;
		}
		header += "* " + nl;
		header += "* Version History:" + nl;
		header += "* " + nl;
		if(LOC != -1){
			if(totalChanges == -1 )
				header += "* -" + (currentVer+1.0) + " : No changes" +nl;//, LOC = " + LOC + nl;
			else
				header += "* -" + (currentVer+1.0) + " : Changes " + (oldtotalChanges+1) + " to " + totalChanges + nl;//, LOC = " + LOC + nl;
		}
		if(versionHistory.length != 0){
			for(int i = 0; i < versionHistory.length; i++){
				if(versionHistory[i] != null)
					header += "* " + versionHistory[i] + nl;
			}
		}
		header += "* " + nl;
		header += "* " + separator + nl + "*/" + nl;
		return header;
	}
	public int getTotalChanges(){
		return totalChanges;
	}
	public int getTotalMods(){
		return totalMods;
	}
	public void setTotalMods(int total){
		totalMods = total;
	}
	public int getOldTotalChanges(){
		return oldtotalChanges;
	}
	public void setTotalChanges(int total){
		totalChanges = total;
	}
	public String getProgramName(){
		return programname;
	}
	public void setProgramName(String name){
		programname = name;
	}
	public double getCurrentVer(){
		return currentVer;
	}
	public void setCurrentVer(double ver){
		currentVer = ver;
	}
	public void addChange(String s){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar cal = Calendar.getInstance();
		String[] nc = new String[changeList.length + 1];
		for(int i = 0; i < changeList.length; i++)
			nc[i] = changeList[i];
		nc[changeList.length] = s + " on " + dateFormat.format(cal.getTime());
		changeList = nc;
		totalChanges++;
	}
	public String removeHeader(String s){
		String str = "";
		if((s.indexOf(separator) - 3) < 0){
			return s;
		}
		str = s.substring(0, s.indexOf(separator) - 3);
		str += s.substring(s.indexOf(separator, s.indexOf(separator) + separator.length()) + 4 + separator.length());
		return str;
	}
}
