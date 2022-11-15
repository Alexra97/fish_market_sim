package fishmarketOntology;

import jade.content.AgentAction;

/**
 * Clase que define la estructura de la acción Collect (Cobrar).
 * @author alejandroramon.lopezr@um.es
 */
public class Collect implements AgentAction {
	
	private float fund;				// Fondos a cobrar.

	/**
	 * Método que devuelve los fondos que la lonja ofrece al vendedor.
	 * @return Los fondos a cobrar.
	 */
	public float getFund() {
		return fund;
	}
	
	/**
	 * Método que establece los fondos a cobrar por el vendedor.
	 * @param fund Fondos a cobrar.
	 */
	public void setFund(float fund) {
		this.fund = fund;
	}
}