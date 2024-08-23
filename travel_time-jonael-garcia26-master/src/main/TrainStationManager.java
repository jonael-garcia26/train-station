package main;

import data_structures.ArrayList;
import data_structures.HashSet;
import data_structures.HashTableSC;
import data_structures.LinkedListStack;
import data_structures.SimpleHashFunction;
import interfaces.List;
import interfaces.Map;
import interfaces.Set;
import interfaces.Stack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Esta clase maneja de logica para encontrar las rutas mas cortas de las estaciones de trenes. Implementada con el uso de Mapas, Sets, Stacks y Arrays.
 */
public class TrainStationManager {
	
	private Map<String, List<Station>> StationMap = new HashTableSC<String, List<Station>>(1, new SimpleHashFunction<String>());
	private Map<String, Station> ShortStationMap = new HashTableSC<String, Station>(1, new SimpleHashFunction<String>());
	private Map<String, Double> TravelTimeMap = new HashTableSC<String, Double>(1, new SimpleHashFunction<String>());
	
	private Stack<Station> StationStack = new LinkedListStack<Station>();
	private Set<Station> StationSet = new HashSet<Station>();

	/** Constructor que lee el archivo en el parametro y llena el mapa de las estaciones con la informacion del archivo. Tambien llama el metodo para la logica de las rutas mas cortas.
	 * 
	 * @param station_file - String
	 */
	public TrainStationManager(String station_file) {
		
		try {
			
			//Proceso para leer el archivo con la informacion de las estaciones
			BufferedReader Rstations = new BufferedReader(new FileReader("inputFiles/" + station_file));
			Rstations.readLine();
			
			String line = Rstations.readLine();
			while(line != null) {
				//convierte cada linea en un array para poder manipular la informacion facilmente
				String[] StationL = line.split(",");
				
				//variables para el nombre de la estacion y la distancia de la estacion
				String key1 = StationL[0];
				Station val1 = new Station(StationL[1], Integer.parseInt(StationL[2]));
				
				//Revisa si el nombre de la estacion ya existe en el mapa, de no estarlo se inicializa su lista de los vecinos y se añade los vecinos a la lista
				if(StationMap.containsKey(key1)) {
					
					StationMap.get(key1).add(val1);
				} else {
					
					List<Station> mapList1 = new ArrayList<Station>();
					mapList1.add(val1);
					StationMap.put(key1, mapList1);
				}
				
				//Se repite el proceso anterior pero esta vez con el nombre de las estaciones invertidas ya que si A = B significa que B = A
				// Se guarda en el mismo mapa con distinto key pero misma distancia
				String key2 = StationL[1];
				Station val2 = new Station(StationL[0], Integer.parseInt(StationL[2]));
				
				if(StationMap.containsKey(key2)) {
					
					StationMap.get(key2).add(val2);
				} else {
					
					List<Station> mapList2 = new ArrayList<Station>();
					mapList2.add(val2);
					StationMap.put(key2, mapList2);
				}
				
				line = Rstations.readLine();
			}
			// se llama la logica para obtener el mapa con las distancias mas cortas
			findShortestDistance();
			
			Rstations.close();
		} catch(IOException e) {
			e.printStackTrace();
			
		}
	}
	
	/**
	 * Este metodo implementa del algoritmo de Dijkstra donde consiste en buscar las rutas mas cortas a la estacion de origen dentro de un mapa.
	 */
	private void findShortestDistance() {
		
		//se crea el mapa para las distancias mas cortas siendo el key las estaciones y el valor siendo (Westside, INFINITO)
		for(String s : StationMap.getKeys()) {
			
			ShortStationMap.put(s, new Station("Westside", Integer.MAX_VALUE));
		}
		
		//Se le cambia el valor de la estacion de partida ya que se le conoce la distancia "0"
		ShortStationMap.put("Westside", new Station("Westside", 0));
		
		//El stack ya inicializado de le añade la primera estacion
		sortStack(ShortStationMap.get("Westside"), StationStack);
		
		//implementacion del algoritmo de Dijkstra
		while(!StationStack.isEmpty()) {
			
			Station curStation = StationStack.pop();
			StationSet.add(curStation);
			
			for(Station vecino : StationMap.get(curStation.getCityName())) {
				
				int A = ShortStationMap.get(vecino.getCityName()).getDistance();
				
				int B = ShortStationMap.get(curStation.getCityName()).getDistance();
				
				int C = vecino.getDistance();
				
				if(B + C < A) {
					
					ShortStationMap.put(vecino.getCityName(), new Station(curStation.getCityName(), (B + C)));
				}
				
				if(!StationSet.isMember(vecino)) {
					sortStack(vecino, StationStack);
				}
			}
		}
	}

	/**Este metodo toma una estacion y lo añade a un Stack de tipo Station de forma ordenada ascendente por sus distancias
	 * 
	 * @param station - Station
	 * @param stackToSort - Stack type Station
	 */
	public void sortStack(Station station, Stack<Station> stackToSort) {
		//Se crea un stack temporero para aguantar los objetos 
		Stack<Station> temp = new LinkedListStack<Station>();
		
		//El primer objeto se añade facilmente
		if (stackToSort.isEmpty()) {
			stackToSort.push(station);
			
		} else {
			//Si el objeto en el top del stack es mayor que el objeto que se desea añadir, ese objeto se mueve temporalmente a temp
			while(!stackToSort.isEmpty() && station.getDistance() > stackToSort.top().getDistance()) {
				
				temp.push(stackToSort.pop());
			}
			
			stackToSort.push(station);
			//una vez ya encontrado la posicion adecuada para el objeto, se coloca en su lugar los objetos de temp al stack original
			while(!temp.isEmpty()) {
				stackToSort.push(temp.pop());
			}
		}
		
		
		
	}
	
	/** Getter donde se hace la logica y se devuelve un mapa con los minutos que se tarda en llegar a cada estacion. El key es el nombre de la estacion y el value son los minutos
	 * 
	 * @return Map
	 */
	public Map<String, Double> getTravelTimes() {
		// 2.5 minutes per kilometer
		// 15 min per station
		
		//se itera por todos los keys del mapa 
		for(String s : ShortStationMap.getKeys()) {
			
			int StationMins = 0;
			
			//loop que itera por todas las estaciones por las que hay que pasar para poder llegar al origen (Westside)
			String stop = ShortStationMap.get(s).getCityName();
			while(stop != "Westside") {
				
				StationMins += 15;
				stop = ShortStationMap.get(stop).getCityName();
			}
			
			//Se calcula el tiempo de cada ruta y se añade al mapa
			TravelTimeMap.put(s, ShortStationMap.get(s).getDistance() * 2.5 + StationMins);
		}
		return TravelTimeMap;
	}

	/**Getter que devuelve el mapa de todas las estaciones con sus vecinos. Donde el key es la estacion y el value es una lista de vecinos
	 * 
	 * @return StationMap
	 */
	public Map<String, List<Station>> getStations() {
		
		return StationMap;
	}

	/**Setter donde se cambia el mapa de las estaciones al mapa deseado
	 * 
	 * @param cities - List type Station
	 */
	public void setStations(Map<String, List<Station>> cities) {
		
		this.StationMap = cities;
	}

	/**Getter que devuelve un mapa con las rutas mas cortas y rapidas. 
	 * 
	 * @return ShortestStationMap
	 */
	public Map<String, Station> getShortestRoutes() {
		
		return ShortStationMap;
	}

	/**Setter donde se cambia el mapa de las estaciones con rutas cortas al mapa deseado
	 * 
	 * @param shortestRoutes
	 */
	public void setShortestRoutes(Map<String, Station> shortestRoutes) {
		
		this.ShortStationMap = shortestRoutes;
	}
	
	/**
	 * BONUS EXERCISE THIS IS OPTIONAL
	 * Returns the path to the station given. 
	 * The format is as follows: Westside->stationA->.....stationZ->stationName
	 * Each station is connected by an arrow and the trace ends at the station given.
	 * 
	 * @param stationName - Name of the station whose route we want to trace
	 * @return (String) String representation of the path taken to reach stationName.
	 */
	public String traceRoute(String stationName) {
		
		//Se crea un stack para guardar en orden las estaciones 
		Stack<String> routes = new LinkedListStack<String>();
		String result = "";
		
		if(stationName == "Westside") return stationName;
		
		routes.push(stationName);
		
		//se busca la estacion anterior de la estacion del parametro
		String stop = ShortStationMap.get(stationName).getCityName();
		
		//si es Westside se añade y el while loop nunca entra
		if(stop == "Westside") routes.push(stop);
		
		//En caso de no serlo, se itera por cada estacion anterior hasta llegar a Westside
		while(stop != "Westside") {
			
			routes.push(stop);
			stop = ShortStationMap.get(stop).getCityName();
			
			if(stop == "Westside") {
				routes.push(stop);
			}
		}
		
		//teniendo ya el stack con la informacion, se vacia para crear el String del resultado
		while(!routes.isEmpty()) {
			
			result += (routes.pop() + "->");
		}
		
		//se devuelve un substring del resultado sin los ultimos dos caracteres "->"
		return result.substring(0, result.length() - 2);
	}

}