package com.intersys.gds.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.intersys.gds.Connection;
import com.intersys.gds.Document;
import com.intersys.gds.DocumentMap;
import com.intersys.gds.DocumentType;
import com.intersys.gds.ElementType;

/**
 * Use the GDS API of the Globals Database.
 * @author iranhutchinson
 *
 */
public class GlobalsDBStart {

	public GlobalsDBStart(){
		
	}
	public static void main(String args[]){
		int documentCount = 100;
		//1. Connect to GlobalsDB
		Connection connection = new Connection();
		connection.connect();
		
		//2. Generate test data
		List<Document> worldJugs = new ArrayList<Document>();
		Document jug = null;
		Document location = null;
		for(int i=0; i<documentCount ; i++){
			jug = new Document();
			jug.put("jugVisit-" + i, new String("Date: " + Calendar.getInstance().getTime()));
			location = new Document();
			location.put("country", "(Axis-Of-Evil) - " + i);
			location.put("venue", "Hotel-" + i*3);
			jug.put("location", location);
			worldJugs.add(jug);
		}
		//3 TODO: Without schema you will get nulls instead of Exceptions.  Add exception/error notification.
		Document firstJUG = worldJugs.get(0);
		DocumentType jugType = DocumentType.createDocumentType("WorldJUGs", firstJUG);
		jugType.setReference("location", ElementType.TYPE_REFERENCE, "Location", "country");
		connection.saveDocumentType(jugType);
		
		Document jugLocation = (Document) firstJUG.get("location");
		DocumentType locationType = DocumentType.createDocumentType("Location", jugLocation);
		locationType.setReference("venue", ElementType.TYPE_BACK_REFERENCE, "WorldJUGs", "NONE");
		connection.saveDocumentType(locationType);
		
		//4.Create the db object handle
		DocumentMap dbDocHandle = connection.getDocumentMap("WorldJUGs");
		
		//5. Store the data in the database
		for(int j=0; j<documentCount; j++){
			Document ljug = worldJugs.get(j);
			dbDocHandle.store(Integer.toString(j), ljug);
		}
		//6. Close the connection
		connection.close();
	}
}
