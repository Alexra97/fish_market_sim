package agents.buyer;

/**
 * Clase que define la configuraci�n del comprador.
 * @author pablo
 */
public class BuyerAgentConfig {
	
	private float budget; // Cartera.
	
	/**
	 * M�todo que pasa la configuraci�n del comprador a String.
	 * @return La cadena equivalente.
	 */
	@Override
	public String toString() {
		return "[budget=" + budget + "]";
	}
	
	/**
	 * M�todo que devuelve la cartera del comprador.
	 * @return La cartera.
	 */
	public float getBudget() {
		return budget;
	}
	
	/**
	 * M�todo que establece el nuevo valor de la cartera.
	 * @param budget El valor de la cartera.
	 */
	public void setBudget(float budget) {
		this.budget = budget;
	}
}
