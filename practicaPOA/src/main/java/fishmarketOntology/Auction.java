package fishmarketOntology;

import jade.content.AgentAction;

/**
 * Clase que define la estructura de la acción Auction (Subastar).
 * @author alejandroramon.lopezr@um.es
 */
public class Auction implements AgentAction {
	
	private Lot lot;				// Lote en subasta.
	private float price;			// Precio del lote.

	/**
	 * Método que devuelve el lote en subasta.
	 * @return El lote en subasta.
	 */
	public Lot getLot() {
		return lot;
	}
	
	/**
	 * Método que devuelve el precio del lote.
	 * @return El precio del lote.
	 */
	public float getPrice() {
		return price;
	}
	
	/**
	 * Método que establece el lote de la subasta.
	 * @param lot Lote a subastar.
	 */
	public void setLot(Lot lot) {
		this.lot = lot;
	}
	
	/**
	 * Método que establece el precio del lote en subasta.
	 * @param price Precio del lote.
	 */
	public void setPrice(float price) {
		this.price = price;
	}
}