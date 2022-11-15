package agents.fishmarket;

/**
 * Clase que define la configuración de la lonja.
 * @author alejandroramon.lopezr@um.es
 */
public class FishMarketAgentConfig {
	
	// Parámetros lonja
	private int numberOfLanes;			// Número de cintas para la subasta.
	private float commission; 			// Porcentaje de comisión que se lleva la lonja por cada lote vendido.
	
	// Parámetros subasta
	private float reductionRate;		// Porcentaje que decrementa el precio de un lote en cada ronda de subasta.
	private int latency;				// Tiempo mínimo en milisegundos que tiene que pasar entre dos subastas en la misma cinta.
	private int opportunityWindow;		// Tiempo mínimo en milisegundos que tiene que pasar entre dos precios sucesivos de una subasta.
	
	/**
	 * Método que convierte a cadena de caracteres la configuración de la lonja.
	 * @return La cadena equivalente.
	 */
	@Override
	public String toString() {
		return "[numberOfLanes=" + numberOfLanes + ", commission=" + commission + "]";
	}
	
	/**
	 * Método que devuelve el número de cintas de la lonja.
	 * @return El número de cintas.
	 */
	public int getNumberOfLanes() {
		return numberOfLanes;
	}
	
	/**
	 * Método que devuelve la comisión de la lonja.
	 * @return El porcentaje de comisión 
	 */
	public float getCommission() {
		return commission;
	}
	
	/**
	 * Método que retorna el ratio de decremento.
	 * @return El porcentaje de reducción del precio.
	 */
	public float getReductionRate() {
		return reductionRate;
	}
	
	/**
	 * Método que devuelve la latencia de la lonja.
	 * @return La latencia en milisegundos.
	 */
	public int getLatency() {
		return latency;
	}
	
	/**
	 * Método que devuelve la ventana de oportunidad de la lonja.
	 * @return La ventana de oportunidad en milisegundos.
	 */
	public int getOpportunityWindow() {
		return opportunityWindow;
	}
	
	/**
	 * Método que establece el número de cintas de la lonja.
	 * @param numberOfLanes Número de cintas.
	 */
	public void setNumberOfLanes(int numberOfLanes) {
		this.numberOfLanes = numberOfLanes;
	}
	
	/**
	 * Método que establece la comisión que gana la lonja por cada lote.
	 * @param commission Porcentaje de comisión.
	 */
	public void setCommission(float commission) {
		this.commission = commission;
	}
	
	/**
	 * Método que establece el ratio de decremento entre dos precios de subasta.
	 * @param reductionRate Porcentaje de reducción.
	 */
	public void setReductionRate(float reductionRate) {
		this.reductionRate = reductionRate;
	}
	
	/**
	 * Método que establece el tiempo que transcurre entre dos subastas de la misma cinta.
	 * @param latency Latencia en milisegundos.
	 */
	public void setLatency(int latency) {
		this.latency = latency;
	}
	
	/**
	 * Método que establece el tiempo que transcurre entre dos precios sucesivos de una subasta.
	 * @param opportunityWindow Ventana de oportunidad en milisegundos.
	 */
	public void setOpportunityWindow(int opportunityWindow) {
		this.opportunityWindow = opportunityWindow;
	}
}
