package view;

import java.util.ArrayList;
import java.util.List;

public class StandardFileList extends MapFileList {

	public StandardFileList() {
		super();
		List<String> saveList = new ArrayList<String>();
		for(String s : mapStringList){
			if(s.indexOf(".sav") == -1 && s.indexOf(".user") == -1){
				saveList.add(s);
			}
		}
		this.setListData(saveList.toArray(new String[saveList.size()]));
	}
	
}
