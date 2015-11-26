package domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import message.BuildSensorHostingHierarchyMsg;
import message.DeclareCatalogMsg;
import message.IsDefinedAsw;
import message.IsDefinedMsg;
import message.RecordEventBasedSensorMsg;
import message.RecordPeriodicSensorMsg;
import message.SketchPatternMsg;
import sensorDeploymentLanguage.Catalog;
import sensorDeploymentLanguage.Containable;
import sensorDeploymentLanguage.Container;
import sensorDeploymentLanguage.ContainerType;
import sensorDeploymentLanguage.Observation;
import sensorDeploymentLanguage.Periodic;
import sensorDeploymentLanguage.SensorDeploymentLanguageFactory;
import sensorDeploymentLanguage.SensorDeploymentLanguagePackage;
import transfer.Service;

public class SensorDeployment extends Service{

	
	public static void declareCatalog (DeclareCatalogMsg msg){
		String name = msg.getCatalog();
		Catalog preexisting = loadModel(name);
		if (preexisting == null) {
			Catalog c = SensorDeploymentLanguageFactory.eINSTANCE.createCatalog();
			c.setName(name);
			saveModel(c);
			System.out.println("Catalog " + name + " created" + "\t\t (" + System.currentTimeMillis() + " )");
		} else {
			System.out.println("--> [Warning] : Catalog " + name + " already exists"
					+ "\t\t (" + System.currentTimeMillis() + " )");
		}
	}

	public static void buildSensorHostingHierarchy ( BuildSensorHostingHierarchyMsg msg){
		String name = msg.getCatalogName();
		LinkedHashMap<String, ContainerType> container = msg.getContainer();
		Catalog preexisting = loadModel(name);
		if (preexisting != null) {
			Container last = null;
			for(String c : container.keySet()){
				Container con = SensorDeploymentLanguageFactory.eINSTANCE.createContainer();
				con.setName(c);
				con.setType(container.get(c));
				if(last!=null)
					last.getContains().add(con);
				else
					preexisting.getRecords().add(con);
				last=con;

				saveModel(preexisting);
			}
			System.out.println("Catalog " + name + " filled with container(s) " + container.keySet()
					+ "\t\t (" + System.currentTimeMillis() + " )");
		}else {
			System.err.println("--> [ERROR] : Catalog " + name + " does not exist"
					+ "\t\t (" + System.currentTimeMillis()	+ " )");
		}
	}
	
	public static void recordPeriodicSensor ( RecordPeriodicSensorMsg msg ){
		String catalog = msg.getCatalog();
		String container = msg.getContainer();
		String sensorName = msg.getName();
		String observationPattern = msg.getObservationPattern();
		int period = msg.getPeriod();
		
		Catalog preexisting = loadModel(catalog);
		if (preexisting != null) {
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
			saveModel(preexisting);
		}else {
			System.err.println("--> [ERROR] : Catalog " + catalog + " does not exist"
					+ "\t\t (" + System.currentTimeMillis()	+ " )");
		}
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
	
	public static void recordEventBasedSensor ( RecordEventBasedSensorMsg msg ){
		
	}
	
	public static void sketchPattern ( SketchPatternMsg msg){
		
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
		XMIResource resource = new XMIResourceImpl(
				URI.createURI("resources/" + name + ".xmi"));
		try {
			resource.load(null);
		} catch (IOException e) {
			return null;
		}

		// get the root of the model
		Catalog cat = (Catalog) resource.getContents().get(0);

		return cat;
	}

	public static void saveModel(Catalog c) {
		String fileName = "resources/" + c.getName() + ".xmi";

		ResourceSet resSet = new ResourceSetImpl();
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		Resource res = resSet.createResource(URI.createFileURI(fileName));
		res.getContents().add(c);

		try {
			res.save(Collections.EMPTY_MAP);
		} catch (Exception e) {
			System.err.println("ERREUR sauvegarde du modèle : " + e);
			e.printStackTrace();
		}

	}

}
