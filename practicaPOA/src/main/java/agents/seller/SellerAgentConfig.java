package agents.seller;

import java.util.List;

import fishmarketOntology.Lot;

/**
 * Clase que define la configuraci�n del vendedor.
 * @author pablo
 */
public class SellerAgentConfig {

	private List<Lot> lots;

	/**
	 * M�todo que convierte a cadena de caracteres la configuraci�n del vendedor.
	 * @return La cadena equivalente.
	 * @author alejandroramon.lopezr@um.es
	 */
	@Override
	public String toString() {
		String aux = "[lots=";
		for (Lot lot : lots) {
			aux += "\n  -  "+lot;
		}
		return   aux + "]";
	}
	
	/**
	 * M�todo que devuelve la lisat de lotes del vendedor.
	 * @return La lista de lotes.
	 */
	public List<Lot> getLots() {
		return lots;
	}
	
	/**
	 * M�todo que establece la nueva lista de lotes del vendedor.
	 * @param lots Lista de lotes.
	 */
	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}
}
