package main;

/**
 * Esta clase representa una estacion de trenes.
 * Contiene el nombre de la estacion y la distancia hacia la estacion
 */
public class Station {
	
	private String name;
	private int dist;
	
	/**
	 * Constructor de la clase Station, crea una estacion con el debido nombre y distancia a otra estacion
	 * 
	 * @param name - String
	 * @param dist - int
	 */
	public Station(String name, int dist) {
		this.name = name;
		this.dist = dist;
	}
	
	/**Getter donde devuelve el nombre de la estacion
	 * 
	 * @return name - String
	 */
	public String getCityName() {
		return this.name;
		
	}
	
	/**Setter que cambia el nombre de la estacion al nombre deseado
	 * 
	 * @param cityName - String
	 */
	public void setCityName(String cityName) {
		this.name = cityName;
		
	}
	
	/**Getter donde devuelve la distancia de la estacion
	 * 
	 * @return dist - int
	 */
	public int getDistance() {
		return this.dist;
	}
	
	/**Setter que cambia la distancia de la estacion a la distancia deseada
	 * 
	 * @param distance - int
	 */
	public void setDistance(int distance) {
		this.dist = distance;
		
	}

	/**Comparador de dos objetos para la clase Station
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Station other = (Station) obj;
		return this.getCityName().equals(other.getCityName()) && this.getDistance() == other.getDistance();
	}
	
	/**
	 * Metodo de la clase Station para convertir el objeto a un String
	 */
	@Override
	public String toString() {
		return "(" + this.getCityName() + ", " + this.getDistance() + ")";
	}

}
