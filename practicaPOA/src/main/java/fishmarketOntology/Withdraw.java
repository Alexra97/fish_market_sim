package fishmarketOntology;

import java.util.List;

import jade.content.AgentAction;

/**
 * Clase que define la estructura de la acción Withdraw (Retirar).
 * @author alejandroramon.lopezr@um.es
 */
public class Withdraw implements AgentAction {
	
	private List<Lot> lots;				// Lista de lotes a retirar.
	
	/**
	 * Método que devuelve la lista de lotes a retirar.
	 * @return Lista de lotes a retirar.
	 */
	public List<Lot> getLots() {
		return lots;
	}
	
	/**
	 * Método que establece la lista de lotes a retirar.
	 * @param lots Lista de lotes para retirar.
	 */
	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}
}