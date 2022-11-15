package scenarios;

/**
 * Clase que define una estructura del tipo (nombre de agente, fichero de configuración).
 * @author pablo
 */
public class AgentRefConfig {
	private String name;
	private String config;
	
	/**
	 * Método que convierte el objeto en cadena de caracteres.
	 * @return La cadena equivalente.
	 */
	@Override
	public String toString() {
		return "[name=" + name + ", config=" + config + "]";
	}
	
	/**
	 * Método que devuelve el nombre del agente.
	 * @return El nombre del agente.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Método que establece el nombre del agente.
	 * @param name Nombre del agente.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Método que devuelve el fichero de configuración del agente.
	 * @return El fichero de configuración.
	 */
	public String getConfig() {
		return config;
	}
	
	/**
	 * Método que establece el fichero de configuración del agente.
	 * @param config Fichero de configuración.
	 */
	public void setConfig(String config) {
		this.config = config;
	}
}
