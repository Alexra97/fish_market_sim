package agents.buyer;

/**
 * Clase que define la configuración del comprador.
 * @author pablo
 */
public class BuyerAgentConfig {
	
	private float budget; // Cartera.
	
	/**
	 * Método que pasa la configuración del comprador a String.
	 * @return La cadena equivalente.
	 */
	@Override
	public String toString() {
		return "[budget=" + budget + "]";
	}
	
	/**
	 * Método que devuelve la cartera del comprador.
	 * @return La cartera.
	 */
	public float getBudget() {
		return budget;
	}
	
	/**
	 * Método que establece el nuevo valor de la cartera.
	 * @param budget El valor de la cartera.
	 */
	public void setBudget(float budget) {
		this.budget = budget;
	}
}
