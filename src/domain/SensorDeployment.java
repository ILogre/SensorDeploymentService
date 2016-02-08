package domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import message.BuildSensorHostingHierarchyMsg;
import message.DeclareCatalogMsg;
import message.DescribeObservationPatternAsw;
import message.DescribeObservationPatternMsg;
import message.DescribeSensorAsw;
import message.DescribeSensorMsg;
import message.IsDefinedAsw;
import message.IsDefinedMsg;
import message.IsValidatedCatalogAsw;
import message.IsValidatedCatalogMsg;
import message.RecordEventBasedSensorMsg;
import message.RecordPeriodicSensorMsg;
import message.SearchAllObservationPatternsAsw;
import message.SearchAllObservationPatternsMsg;
import message.SearchAllSensorsAsw;
import message.SearchAllSensorsMsg;
import message.SearchSensorsByObservationPatternAsw;
import message.SearchSensorsByObservationPatternMsg;
import message.SketchPatternMsg;
import message.ValidateAndPersistCatalogMsg;

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
import sensorDeploymentLanguage.DataType;
import sensorDeploymentLanguage.Event_Based;
import sensorDeploymentLanguage.Observation;
import sensorDeploymentLanguage.Periodic;
import sensorDeploymentLanguage.Sensor;
import sensorDeploymentLanguage.SensorDeploymentLanguageFactory;
import transfer.Service;
import businessobject.Continuous;
import businessobject.Discrete;
import businessobject.Field;
import errors.UnknownCatalogException;
import errors.UnknownContainerException;
import errors.UnknownObservationPatternException;
import errors.UnkonwnSensorException;
import errors.UnreachableCodeException;

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
			System.out.println("--> [Warning] : Catalog " + name + " already exists" + "\t\t (" + System.currentTimeMillis() + " )");
		}
		catch(UnknownCatalogException e){
			Catalog c = SensorDeploymentLanguageFactory.eINSTANCE.createCatalog();
			c.setName(name);
			updateCatalog(name, c);;
			System.out.println("Catalog " + name + " created" + "\t\t (" + System.currentTimeMillis() + " )");
		}
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
		
		System.out.println("Catalog " + catalog + " contains periodic sensor " + sensorName + " in " + container + " observing " + observationPattern + " every "+ period+"s"
				+ "\t\t (" + System.currentTimeMillis() + " )");
		
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
		
		System.out.println("Catalog " + catalog + " contains event-based sensor " + sensorName + " in " + container + " observing " + observationPattern
				+ "\t\t (" + System.currentTimeMillis() + " )");
		
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
		
		System.out.println("Catalog " + catalog + " knows sketch " + name
				+ "\t\t (" + System.currentTimeMillis() + " )");
		
		
		validated = false;
	}
	
	
	public static IsDefinedAsw isDefined ( IsDefinedMsg msg ) throws UnknownCatalogException {
		String catalog = msg.getCatalog();
		String sensor= msg.getSensor();
		
		Catalog preexisting = getCatalog(catalog);
		
		boolean answer = false;
		for(Container container : preexisting.getRecords())
			answer = isDefinedInContainer(sensor, container);
				
		return new IsDefinedAsw(msg.getCatalog(), msg.getSensor(), answer);
		
	}
	
	private static boolean isDefinedInContainer(String sensor, Container container){
		if(container.getContains().isEmpty())
			return false;
		else{
			boolean asw = false;
			for(Containable containable : container.getContains())
				if(Sensor.class.isInstance(containable)){
					if(containable.getName().equals(sensor))
						return true;
				}
				else
					asw = asw || isDefinedInContainer(sensor,(Container) containable);
			return asw;
		}

	}
	
	private static Sensor getSensorDefinedInContainer(String sensor, Container container) throws UnkonwnSensorException{
		if(container.getContains().isEmpty())
			return null;
		else
		for(Containable containable : container.getContains())
			if(Sensor.class.isInstance(containable)){
				if(containable.getName().equals(sensor))
					return (Sensor) containable;
			}
			else{
				Sensor bellow = getSensorDefinedInContainer(sensor,(Container) containable);
				if(bellow!=null)
					return bellow;
			}
		return null;
	}
	
	private static Sensor getSensorDefinedInCatalog(String sensor, Catalog catalog) throws UnkonwnSensorException{
		for(Container c : catalog.getRecords())
			if(isDefinedInContainer(sensor, c))
				return getSensorDefinedInContainer(sensor, c);
		throw new UnkonwnSensorException("Sensor "+sensor+" unknown in catalog "+catalog.getName());
	}
	
	public static DescribeSensorAsw describeSensor(DescribeSensorMsg msg) throws UnknownCatalogException, UnkonwnSensorException, UnreachableCodeException{
		String catalogName = msg.getCatalogName();
		String sensorName= msg.getSensorName();
		
		Catalog catalog = getCatalog(catalogName);
		
		Sensor sensor = getSensorDefinedInCatalog(sensorName,catalog);
		boolean isPeriodic = Periodic.class.isInstance(sensor);
		boolean isEventBased = Event_Based.class.isInstance(sensor);
		int period = -1;
		if(isPeriodic){
			Periodic p = (Periodic)sensor;
			period = p.getPeriod();
		}
		Observation o = sensor.getObserves();
		List<Field> fields = new ArrayList<Field>();
		sensorDeploymentLanguage.Field time = o.getTime();
		sensorDeploymentLanguage.Continuous continuousTime = (sensorDeploymentLanguage.Continuous)time.getRange();
		fields.add(new Continuous<>(time.getName(), continuousTime.getMin(), continuousTime.getMax() ));
		for(sensorDeploymentLanguage.Field f : o.getValues()){
			sensorDeploymentLanguage.Range r = f.getRange();
			if(sensorDeploymentLanguage.Continuous.class.isInstance(r)){
				sensorDeploymentLanguage.Continuous continuousField = (sensorDeploymentLanguage.Continuous) r;
				Object valuemin = getDataTypeValue(continuousField.getMin());
				Object valuemax = getDataTypeValue(continuousField.getMax());
				fields.add(new Continuous<>(f.getName(), valuemin, valuemax));			
			}else if(sensorDeploymentLanguage.Discrete.class.isInstance(r)){
				sensorDeploymentLanguage.Discrete discreteField = (sensorDeploymentLanguage.Discrete) r;
				String[] l = new String[discreteField.getValues().size()];
				for(DataType d : discreteField.getValues())
					l[l.length]=getDataTypeValue(d).toString();
				fields.add(new Discrete(f.getName(),l));
			}else
				throw new UnreachableCodeException();			
		}
		DescribeSensorAsw answer = new DescribeSensorAsw(sensorName, o.getName(),isPeriodic,isEventBased, period, fields);
		return answer;
	}
	
	private static Object getDataTypeValue(DataType d) throws UnreachableCodeException{
		if(sensorDeploymentLanguage.Integer.class.isInstance(d)){
			sensorDeploymentLanguage.Integer i = (sensorDeploymentLanguage.Integer) d;
			return i.getValue();
		}else if(sensorDeploymentLanguage.Float.class.isInstance(d)){
			sensorDeploymentLanguage.Float f = (sensorDeploymentLanguage.Float) d;
			return f.getValue();
		}else if(sensorDeploymentLanguage.String.class.isInstance(d)){
			sensorDeploymentLanguage.String s = (sensorDeploymentLanguage.String) d;
			return s.getValue();
		}
		throw new UnreachableCodeException();
	}
	
	public static SearchAllSensorsAsw searchAllSensors(SearchAllSensorsMsg msg) throws UnknownCatalogException, UnknownContainerException{
		String catalogName = msg.getCatalogName();
		String containerName= msg.getContainer();
		
		Catalog catalog = getCatalog(catalogName);
		Container container = getContainerByName(catalog,containerName);
		
		List<String> names = new ArrayList<String>();
		for(Sensor sensor : getSensorsByContainer(container))
			names.add(sensor.getName());
		
		return new SearchAllSensorsAsw(names);
		
	}
	
	private static List<Sensor> getSensorsByContainer(Container container){
		List<Sensor> result = new ArrayList<Sensor>();
		for(Containable containable : container.getContains())
			if(Sensor.class.isInstance(containable))
				result.add((Sensor) containable);
			else
				result.addAll(getSensorsByContainer((Container)containable));
		return result;
	}
	
	
	private static List<Sensor> getSensorsByCatalog(Catalog catalog){
		List<Sensor> result = new ArrayList<Sensor>();
		for(Container container : catalog.getRecords())
			result.addAll(getSensorsByContainer(container));
		return result;
	}
	
	private static Container getContainerByName(Container c, String containerName) throws UnknownContainerException{
		for(Containable containable : c.getContains())
			if (Container.class.isInstance(containable)){
				Container container = (Container) containable;
				if (container.getName().equalsIgnoreCase(containerName))
					return container;
				else{
					Container result = getContainerByName(container, containerName);
					if(result != null)
						return result;
				}
			}
		return null;
	}
	
	private static Container getContainerByName(Catalog catalog, String containerName) throws UnknownContainerException{
		for(Container container : catalog.getRecords()){
			if (container.getName().equalsIgnoreCase(containerName)){
				return container;
			}
			else{
				Container result = getContainerByName(container, containerName);
				if(result != null)
					return result;
			}
		}
		throw new UnknownContainerException("Container "+containerName+" is not known in catalog "+catalog.getName());
	}
	
	public static DescribeObservationPatternAsw describeObservationPattern(DescribeObservationPatternMsg msg) throws UnknownCatalogException, UnknownObservationPatternException{
		String catalogName = msg.getCatalogName();
		String observationName= msg.getObservationName();
		
		Catalog catalog = getCatalog(catalogName);
		
		List<Observation> observations = catalog.getPatterns();
		Observation observation = null;
		for(Observation o : observations){
			if(o.getName().equalsIgnoreCase(observationName))
				observation=o;
		}
		if(observation==null)
			throw new UnknownObservationPatternException("Observation pattern "+observationName+" is not known.");
		Map<String, Boolean> values = new HashMap<>();
		for(sensorDeploymentLanguage.Field field : observation.getValues())
			values.put(field.getName(), Continuous.class.isInstance(field.getRange()));
		return new DescribeObservationPatternAsw(observationName,observation.getTime().getName(),sensorDeploymentLanguage.Continuous.class.isInstance(observation.getTime().getRange()),values);
	}
	
	public static SearchAllObservationPatternsAsw searchAllObservationPatterns(SearchAllObservationPatternsMsg msg) throws UnknownCatalogException{
		String catalogName = msg.getCatalog();
		Catalog catalog = getCatalog(catalogName);
		List<Observation> observations = catalog.getPatterns();
		List<String> names = new ArrayList<String>();
		for(Observation o : observations)
			names.add(o.getName());
		return new SearchAllObservationPatternsAsw(names);
	}
	
	private static List<Sensor> filterSensorsByObservationPattern(List<Sensor> sensors, String obsPat){
		List<Sensor> result = new ArrayList<Sensor>();
		for(Sensor sensor : sensors)
			if(sensor.getObserves().getName().equalsIgnoreCase(obsPat))
				result.add(sensor);
		return result;
		
	}
	
	public static SearchSensorsByObservationPatternAsw searchSensorsByObservationPattern (SearchSensorsByObservationPatternMsg msg) throws UnknownCatalogException{
		String catalogName = msg.getCatalog();
		String observationPattern = msg.getPattern();
		Catalog catalog = getCatalog(catalogName);
		List<String> sensorNames = new ArrayList<String>();
		List<Sensor> sensors = filterSensorsByObservationPattern(getSensorsByCatalog(catalog), observationPattern);
		for(Sensor sensor : sensors)
			sensorNames.add(sensor.getName());
		return new SearchSensorsByObservationPatternAsw(sensorNames);
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

	public static void validateAndPersist(ValidateAndPersistCatalogMsg msg) throws IOException {
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
	
	public static IsValidatedCatalogAsw isValidated(IsValidatedCatalogMsg msg){
		return new IsValidatedCatalogAsw(validated);
	}

}
