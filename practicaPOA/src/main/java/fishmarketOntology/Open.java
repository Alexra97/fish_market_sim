package fishmarketOntology;

import jade.content.AgentAction;

/**
 * Clase que define la estructura de la acción Open (Abrir).
 * @author alejandroramon.lopezr@um.es
 */
public class Open implements AgentAction {
	
	private float balance;				// Saldo del comprador.
	
	/**
	 * Método que devuelve el saldo del comprador.
	 * @return El saldo del comprador.
	 */
	public float getBalance() {
		return balance;
	}
	
	/**
	 * Método que establece el saldo de apertura.
	 * @param balance Saldo inicial del comprador.
	 */
	public void setBalance(float balance) {
		this.balance = balance;
	}
}