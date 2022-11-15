package scenarios;

import java.util.List;

import agents.clock.ClockAgentConfig;

/**
 * Define los par�metros que conforman un escenario para su simulaci�n desde un fichero YAML.
 * Ref: https://www.baeldung.com/java-snake-yaml
 * @author pablo
 */
public class ScenarioConfig {
	private String name;									// Nombre del escenario.
	private String description;								// Descripci�n del escenario.
	private ClockAgentConfig clock;							// Configuraci�n del agente reloj.
	private AgentRefConfig fishMarket;						// Configuraci�n y nombre del agente lonja.
	private List<AgentRefConfig> buyers;					// Lista de configuraciones y nombres de los agentes compradores.
	private List<AgentRefConfig> sellers;					// Lista de configuraciones y nombres de los agentes vendedores.
	
	/**
	 * M�todo que convierte la configuraci�n del escenario en una cadena de caracteres.
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
	 * M�todo que devuelve el nombre del escenario.
	 * @return El nombre del escenario.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * M�todo que establece el nombre del escenario.
	 * @param name Nombre del escenario.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * M�todo que devuelve la descripci�n del escenario.
	 * @return La descripci�n del escenario.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * M�todo que establece la descripci�n del escenario.
	 * @param description Descripci�n del escenario.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * M�todo que devuelve el nombre y la configuraci�n del agente lonja.
	 * @return El nombre y la configuraci�n de la lonja.
	 */
	public AgentRefConfig getFishMarket() {
		return fishMarket;
	}
	
	/**
	 * M�todo que establece el nombre y la configuraci�n del agente lonja.
	 * @param fishMarket Nombre y la configuraci�n de la lonja.
	 */
	public void setFishMarket(AgentRefConfig fishMarket) {
		this.fishMarket = fishMarket;
	}
	
	/**
	 * M�todo que devuelve la lista de nombres y configuraciones de los compradores.
	 * @return Los nombres y configuraciones de los compradores.
	 */
	public List<AgentRefConfig> getBuyers() {
		return buyers;
	}
	
	/**
	 * M�todo que establece la lista de nombres y configuraciones de los compradores.
	 * @param buyers Nombres y configuraciones de los compradores.
	 */
	public void setBuyers(List<AgentRefConfig> buyers) {
		this.buyers = buyers;
	}
	
	/**
	 * M�todo que devuelve la lista de nombres y configuraciones de los vendedores.
	 * @return Los nombres y configuraciones de los vendedores.
	 */
	public List<AgentRefConfig> getSellers() {
		return sellers;
	}
	
	/**
	 * M�todo que establece la lista de nombres y configuraciones de los vendedores.
	 * @param sellers Lista de nombres y configuraciones de los vendedores.
	 */
	public void setSellers(List<AgentRefConfig> sellers) {
		this.sellers = sellers;
	}
	
	/**
	 * M�todo que devuelve la configuraci�n del agente reloj.
	 * @return La configuraci�n del reloj.
	 */
	public ClockAgentConfig getClock() {
		return clock;
	}
	
	/**
	 * M�todo que establece la configuraci�n del agente reloj.
	 * @param clock Configuraci�n del reloj.
	 */
	public void setClock(ClockAgentConfig clock) {
		this.clock = clock;
	}
}
