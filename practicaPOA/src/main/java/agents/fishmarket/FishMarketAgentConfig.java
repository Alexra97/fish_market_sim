package agents.fishmarket;

/**
 * Clase que define la configuraci�n de la lonja.
 * @author alejandroramon.lopezr@um.es
 */
public class FishMarketAgentConfig {
	
	// Par�metros lonja
	private int numberOfLanes;			// N�mero de cintas para la subasta.
	private float commission; 			// Porcentaje de comisi�n que se lleva la lonja por cada lote vendido.
	
	// Par�metros subasta
	private float reductionRate;		// Porcentaje que decrementa el precio de un lote en cada ronda de subasta.
	private int latency;				// Tiempo m�nimo en milisegundos que tiene que pasar entre dos subastas en la misma cinta.
	private int opportunityWindow;		// Tiempo m�nimo en milisegundos que tiene que pasar entre dos precios sucesivos de una subasta.
	
	/**
	 * M�todo que convierte a cadena de caracteres la configuraci�n de la lonja.
	 * @return La cadena equivalente.
	 */
	@Override
	public String toString() {
		return "[numberOfLanes=" + numberOfLanes + ", commission=" + commission + "]";
	}
	
	/**
	 * M�todo que devuelve el n�mero de cintas de la lonja.
	 * @return El n�mero de cintas.
	 */
	public int getNumberOfLanes() {
		return numberOfLanes;
	}
	
	/**
	 * M�todo que devuelve la comisi�n de la lonja.
	 * @return El porcentaje de comisi�n 
	 */
	public float getCommission() {
		return commission;
	}
	
	/**
	 * M�todo que retorna el ratio de decremento.
	 * @return El porcentaje de reducci�n del precio.
	 */
	public float getReductionRate() {
		return reductionRate;
	}
	
	/**
	 * M�todo que devuelve la latencia de la lonja.
	 * @return La latencia en milisegundos.
	 */
	public int getLatency() {
		return latency;
	}
	
	/**
	 * M�todo que devuelve la ventana de oportunidad de la lonja.
	 * @return La ventana de oportunidad en milisegundos.
	 */
	public int getOpportunityWindow() {
		return opportunityWindow;
	}
	
	/**
	 * M�todo que establece el n�mero de cintas de la lonja.
	 * @param numberOfLanes N�mero de cintas.
	 */
	public void setNumberOfLanes(int numberOfLanes) {
		this.numberOfLanes = numberOfLanes;
	}
	
	/**
	 * M�todo que establece la comisi�n que gana la lonja por cada lote.
	 * @param commission Porcentaje de comisi�n.
	 */
	public void setCommission(float commission) {
		this.commission = commission;
	}
	
	/**
	 * M�todo que establece el ratio de decremento entre dos precios de subasta.
	 * @param reductionRate Porcentaje de reducci�n.
	 */
	public void setReductionRate(float reductionRate) {
		this.reductionRate = reductionRate;
	}
	
	/**
	 * M�todo que establece el tiempo que transcurre entre dos subastas de la misma cinta.
	 * @param latency Latencia en milisegundos.
	 */
	public void setLatency(int latency) {
		this.latency = latency;
	}
	
	/**
	 * M�todo que establece el tiempo que transcurre entre dos precios sucesivos de una subasta.
	 * @param opportunityWindow Ventana de oportunidad en milisegundos.
	 */
	public void setOpportunityWindow(int opportunityWindow) {
		this.opportunityWindow = opportunityWindow;
	}
}
