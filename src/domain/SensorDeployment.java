package domain;

import message.BuildSensorHostingHierarchyMsg;
import message.DeclareCatalogMsg;
import message.IsDefinedAsw;
import message.IsDefinedMsg;
import message.RecordEventBasedSensorMsg;
import message.RecordPeriodicSensorMsg;
import message.SketchPatternMsg;
import transfer.Service;


public class SensorDeployment extends Service{

	
	public static void declareCatalog (DeclareCatalogMsg msg){
		
	}

	public static void buildSensorHostingHierarchy ( BuildSensorHostingHierarchyMsg msg){
		
	}
	
	public static void recordPeriodicSensor ( RecordPeriodicSensorMsg msg ){
		
	}
	
	public static void recordEventBasedSensor ( RecordEventBasedSensorMsg msg ){
		
	}
	
	public static void sketchPattern ( SketchPatternMsg msg){
		
	}
	
	public static IsDefinedAsw isDefined ( IsDefinedMsg msg ) {
		return new IsDefinedAsw(true);
		
	}

}
