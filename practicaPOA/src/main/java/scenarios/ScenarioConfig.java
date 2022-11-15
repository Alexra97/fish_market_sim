package scenarios;

import java.util.List;

import agents.clock.ClockAgentConfig;

/**
 * Define los parámetros que conforman un escenario para su simulación desde un fichero YAML.
 * Ref: https://www.baeldung.com/java-snake-yaml
 * @author pablo
 */
public class ScenarioConfig {
	private String name;									// Nombre del escenario.
	private String description;								// Descripción del escenario.
	private ClockAgentConfig clock;							// Configuración del agente reloj.
	private AgentRefConfig fishMarket;						// Configuración y nombre del agente lonja.
	private List<AgentRefConfig> buyers;					// Lista de configuraciones y nombres de los agentes compradores.
	private List<AgentRefConfig> sellers;					// Lista de configuraciones y nombres de los agentes vendedores.
	
	/**
	 * Método que convierte la configuración del escenario en una cadena de caracteres.
	 * @return La cadena equivalente.
	 */
	@Override
	public String toString() {
		return "ScenarioConfig [name=" + name + ", description=" + description + ",\n"+
				"clock=" + clock + ",\n"+
				"fishMarket=" + fishMarket + ",\n"+
				"buyers=" + buyers + ",\n"+
				"sellers=" + sellers + "]";
	}

	/**
	 * Método que devuelve el nombre del escenario.
	 * @return El nombre del escenario.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Método que establece el nombre del escenario.
	 * @param name Nombre del escenario.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Método que devuelve la descripción del escenario.
	 * @return La descripción del escenario.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Método que establece la descripción del escenario.
	 * @param description Descripción del escenario.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Método que devuelve el nombre y la configuración del agente lonja.
	 * @return El nombre y la configuración de la lonja.
	 */
	public AgentRefConfig getFishMarket() {
		return fishMarket;
	}
	
	/**
	 * Método que establece el nombre y la configuración del agente lonja.
	 * @param fishMarket Nombre y la configuración de la lonja.
	 */
	public void setFishMarket(AgentRefConfig fishMarket) {
		this.fishMarket = fishMarket;
	}
	
	/**
	 * Método que devuelve la lista de nombres y configuraciones de los compradores.
	 * @return Los nombres y configuraciones de los compradores.
	 */
	public List<AgentRefConfig> getBuyers() {
		return buyers;
	}
	
	/**
	 * Método que establece la lista de nombres y configuraciones de los compradores.
	 * @param buyers Nombres y configuraciones de los compradores.
	 */
	public void setBuyers(List<AgentRefConfig> buyers) {
		this.buyers = buyers;
	}
	
	/**
	 * Método que devuelve la lista de nombres y configuraciones de los vendedores.
	 * @return Los nombres y configuraciones de los vendedores.
	 */
	public List<AgentRefConfig> getSellers() {
		return sellers;
	}
	
	/**
	 * Método que establece la lista de nombres y configuraciones de los vendedores.
	 * @param sellers Lista de nombres y configuraciones de los vendedores.
	 */
	public void setSellers(List<AgentRefConfig> sellers) {
		this.sellers = sellers;
	}
	
	/**
	 * Método que devuelve la configuración del agente reloj.
	 * @return La configuración del reloj.
	 */
	public ClockAgentConfig getClock() {
		return clock;
	}
	
	/**
	 * Método que establece la configuración del agente reloj.
	 * @param clock Configuración del reloj.
	 */
	public void setClock(ClockAgentConfig clock) {
		this.clock = clock;
	}
}
