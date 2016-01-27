package domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import message.BuildSensorHostingHierarchyMsg;
import message.DeclareCatalogMsg;
import message.IsDefinedAsw;
import message.IsDefinedMsg;
import message.RecordEventBasedSensorMsg;
import message.RecordPeriodicSensorMsg;
import message.SketchPatternMsg;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import sensorDeploymentLanguage.Atomic;
import sensorDeploymentLanguage.Catalog;
import sensorDeploymentLanguage.Containable;
import sensorDeploymentLanguage.Container;
import sensorDeploymentLanguage.Event_Based;
import sensorDeploymentLanguage.Observation;
import sensorDeploymentLanguage.Periodic;
import sensorDeploymentLanguage.SensorDeploymentLanguageFactory;
import transfer.IsValidatedAsw;
import transfer.IsValidatedMsg;
import transfer.Service;
import transfer.ValidateAndPersistMsg;
import businessobject.Field;
import errors.UnknownCatalogException;

/*
 * This class represent the domain knowledge of the Sensor Deployment domain
 * It implements the exposed operations with EMF stack
 */

public class SensorDeployment extends Service{
	
	static private Map<String,Catalog> currents = new HashMap<String,Catalog>();
	static private boolean validated = false;
	
	static private Catalog getCatalog(String name) throws UnknownCatalogException{
		if(!currents.containsKey(name)){
			Catalog preexisting = loadModel(name);
			if (preexisting==null){
				throw new UnknownCatalogException("[ERROR] : Catalog " + name + " does not exist"+ "\t\t (" + System.currentTimeMillis() + " )");
			}else{
				currents.put(name, preexisting); 
				return preexisting;
			}
		}else
			return currents.get(name);
	}
	
	static private void updateCatalog(String name, Catalog c){
		if(!currents.containsKey(name))
			currents.put(name, c);
		else
			currents.replace(name, currents.get(name), c);
	}

	
	public static void declareCatalog (DeclareCatalogMsg msg){
		String name = msg.getCatalog();
		try{
			getCatalog(name);
		}
		catch(UnknownCatalogException e){
			Catalog c = SensorDeploymentLanguageFactory.eINSTANCE.createCatalog();
			c.setName(name);
			updateCatalog(name, c);;
			System.out.println("Catalog " + name + " created" + "\t\t (" + System.currentTimeMillis() + " )");
		}
		System.out.println("--> [Warning] : Catalog " + name + " already exists" + "\t\t (" + System.currentTimeMillis() + " )");
		validated = false;
	}

	public static void buildSensorHostingHierarchy ( BuildSensorHostingHierarchyMsg msg) throws UnknownCatalogException{
		String name = msg.getCatalogName();
		LinkedHashMap<String, businessobject.ContainerType> container = msg.getContainer();
		Catalog preexisting = getCatalog(name);

		Container last = null;
		for(String c : container.keySet()){
			Container con = SensorDeploymentLanguageFactory.eINSTANCE.createContainer();
			con.setName(c);
			con.setType(sensorDeploymentLanguage.ContainerType.getByName(container.get(c).name()));
			if(last!=null)
				last.getContains().add(con);
			else
				preexisting.getRecords().add(con);
			last=con;
		}
		System.out.println("Catalog " + name + " filled with container(s) " + container.keySet()
				+ "\t\t (" + System.currentTimeMillis() + " )");
		validated = false;

	}
	
	public static void recordPeriodicSensor ( RecordPeriodicSensorMsg msg ) throws UnknownCatalogException{
		String catalog = msg.getCatalog();
		String container = msg.getContainer();
		String sensorName = msg.getName();
		String observationPattern = msg.getObservationPattern();
		int period = msg.getPeriod();
		
		Catalog preexisting = getCatalog(catalog);

		EList<Container> existingContainers = preexisting.getRecords();
		Container host = fillingExistingContainerList(existingContainers,container);
		EList<Observation> existingObservation = preexisting.getPatterns();
		Observation obs = null;
		for(Observation o : existingObservation)
			if(o.getName().equalsIgnoreCase(observationPattern))
				obs=o;
		if(obs==null){
			obs = SensorDeploymentLanguageFactory.eINSTANCE.createObservation();
			obs.setName(observationPattern);
		}
		
		Periodic sensor = SensorDeploymentLanguageFactory.eINSTANCE.createPeriodic();
		sensor.setName(sensorName);
		sensor.setObserves(obs);
		sensor.setPeriod(period);
		
		host.getContains().add(sensor);
		validated = false;
	}
	
	private static Container fillingExistingContainerList(EList<Container> current, String searching){
		for(Container c : current)
			if(c.getName().equalsIgnoreCase(searching))
				return c;
		EList<Containable> containables = new BasicEList<Containable>();
		for(Container c : current)
			containables.addAll(c.getContains());
		
		EList<Container> containers = new BasicEList<Container>();
		for(Containable c : containables)
			if(Container.class.isInstance(c))
				containers.add((Container) c);
			
		return fillingExistingContainerList(containers, searching);
	}
	
	public static void recordEventBasedSensor ( RecordEventBasedSensorMsg msg ) throws UnknownCatalogException{
		String catalog = msg.getCatalog();
		String container = msg.getContainer();
		String sensorName = msg.getName();
		String observationPattern = msg.getObservationPattern();
		String trigger = msg . getTrigger();
		
		Catalog preexisting = getCatalog(catalog);

		EList<Container> existingContainers = preexisting.getRecords();
		Container host = fillingExistingContainerList(existingContainers,container);
		EList<Observation> existingObservation = preexisting.getPatterns();
		Observation obs = null;
		for(Observation o : existingObservation)
			if(o.getName().equalsIgnoreCase(observationPattern))
				obs=o;
		if(obs==null){
			obs = SensorDeploymentLanguageFactory.eINSTANCE.createObservation();
			obs.setName(observationPattern);
		}
		
		Event_Based sensor = SensorDeploymentLanguageFactory.eINSTANCE.createEvent_Based();
		sensor.setName(sensorName);
		sensor.setObserves(obs);
		sensor.setTrigger(trigger);
		
		host.getContains().add(sensor);
		validated = false;
	}
	
	public static void sketchPattern ( SketchPatternMsg msg) throws UnknownCatalogException{
		String catalog = msg.getCatalog();
		String name= msg.getName();
		Field key= msg.getKey();
		Field[] values= msg.getValues();
		
		Catalog preexisting = getCatalog(catalog);
			
		Observation obs = SensorDeploymentLanguageFactory.eINSTANCE.createObservation();
		obs.setName(name);
		//TODO handle the calculated case
		
		Atomic truekey = SensorDeploymentLanguageFactory.eINSTANCE.createAtomic();
		truekey.setName(key.getName());
		key.ConcretizeToEMF(truekey);
		obs.setTime(truekey);
		
		Atomic val;
		for(Field f : values){
			val = SensorDeploymentLanguageFactory.eINSTANCE.createAtomic();
			val.setName(f.getName());
			f.ConcretizeToEMF(val);
			obs.getValues().add(val);
		}
		preexisting.getPatterns().add(obs);
		validated = false;
	}
	
	
	public static IsDefinedAsw isDefined ( IsDefinedMsg msg ) {
		return new IsDefinedAsw(msg.getCatalog(), msg.getData(),true);
		
	}
	
	static { // register the language
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> packageRegistry = reg.getExtensionToFactoryMap();
		packageRegistry.put(sensorDeploymentLanguage.SensorDeploymentLanguagePackage.eNS_URI,
				sensorDeploymentLanguage.SensorDeploymentLanguagePackage.eINSTANCE);

	}

	public static Catalog loadModel(String name) {
		// load the xmi file
		XMIResource resource = new XMIResourceImpl(URI.createURI("resources/" + name + ".xmi"));
		try {
			resource.load(null);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}

		// get the root of the model
		Catalog cat = (Catalog) resource.getContents().get(0);

		return cat;
	}

	public static void validateAndPersist(ValidateAndPersistMsg msg) throws IOException {
		String fileName = "resources/" + msg.getModelName() + ".xmi";
		File file = new File(fileName);
		Files.deleteIfExists(file.toPath());

		ResourceSet resSet = new ResourceSetImpl();
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		Resource res = resSet.createResource(URI.createFileURI(fileName));
		res.getContents().add(currents.get(msg.getModelName()));

		try {
			res.save(Collections.EMPTY_MAP);
		} catch (Exception e) {
			System.err.println("ERREUR sauvegarde du modèle : " + e);
			e.printStackTrace();
		}
		validated = true;

	}
	
	public static IsValidatedAsw isValidated(IsValidatedMsg msg){
		return new IsValidatedAsw(validated);
	}

}
