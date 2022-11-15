package fishmarketOntology;

import jade.content.AgentAction;

/**
 * Clase que define la estructura de la acci�n Register (Registrar).
 * @author alejandroramon.lopezr@um.es
 */
public class Register implements AgentAction {
	
	private String type;				// Tipo de agente que solicita el registro.
	
	/**
	 * M�todo que devuelve el tipo de agente del registro.
	 * @return El tipo de agente.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * M�todo que establece el tipo de agente del registro.
	 * @param type Tipo de agente.
	 */
	public void setType(String type) {
		this.type = type;
	}
}