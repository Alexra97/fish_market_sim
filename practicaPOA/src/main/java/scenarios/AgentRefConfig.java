package scenarios;

/**
 * Clase que define una estructura del tipo (nombre de agente, fichero de configuraci�n).
 * @author pablo
 */
public class AgentRefConfig {
	private String name;
	private String config;
	
	/**
	 * M�todo que convierte el objeto en cadena de caracteres.
	 * @return La cadena equivalente.
	 */
	@Override
	public String toString() {
		return "[name=" + name + ", config=" + config + "]";
	}
	
	/**
	 * M�todo que devuelve el nombre del agente.
	 * @return El nombre del agente.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * M�todo que establece el nombre del agente.
	 * @param name Nombre del agente.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * M�todo que devuelve el fichero de configuraci�n del agente.
	 * @return El fichero de configuraci�n.
	 */
	public String getConfig() {
		return config;
	}
	
	/**
	 * M�todo que establece el fichero de configuraci�n del agente.
	 * @param config Fichero de configuraci�n.
	 */
	public void setConfig(String config) {
		this.config = config;
	}
}
