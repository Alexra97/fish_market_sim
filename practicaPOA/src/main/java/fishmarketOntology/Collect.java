package fishmarketOntology;

import jade.content.AgentAction;

/**
 * Clase que define la estructura de la acci�n Collect (Cobrar).
 * @author alejandroramon.lopezr@um.es
 */
public class Collect implements AgentAction {
	
	private float fund;				// Fondos a cobrar.

	/**
	 * M�todo que devuelve los fondos que la lonja ofrece al vendedor.
	 * @return Los fondos a cobrar.
	 */
	public float getFund() {
		return fund;
	}
	
	/**
	 * M�todo que establece los fondos a cobrar por el vendedor.
	 * @param fund Fondos a cobrar.
	 */
	public void setFund(float fund) {
		this.fund = fund;
	}
}