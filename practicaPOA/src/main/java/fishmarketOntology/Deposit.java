package fishmarketOntology;

import java.util.List;

import jade.content.AgentAction;

/**
 * Clase que define la estructura de la acci�n Deposit (Depositar).
 * @author alejandroramon.lopezr@um.es
 */
public class Deposit implements AgentAction {
	
	private List<Lot> lots;				// Lista de lotes a depositar.

	/**
	 * M�todo que devuelve la lista de lotes que se quiere depositar.
	 * @return La lista de lotes a depositar.
	 */
	public List<Lot> getLots() {
		return lots;
	}
	
	/**
	 * M�todo que establece la lista de lotes a depositar.
	 * @param lots Lista de lotes para depositar.
	 */
	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}
}