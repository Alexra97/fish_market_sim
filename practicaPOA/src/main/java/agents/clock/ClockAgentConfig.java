package agents.clock;

/**
 * Clase que define la configuraci�n del reloj.
 * @author pablo
 */
public class ClockAgentConfig {
	private int unitTimeMillis;
	private int numUnitDay;
	private int numSimDays;
	
	/**
	 * M�todo que convierte a cadena de caracteres le configuraci�n del reloj.
	 * @return La cadena equivalente.
	 */
	@Override
	public String toString() {
		return "ClockAgentConfig [unitTimeMillis=" + unitTimeMillis + ", numUnitDay=" + numUnitDay + ", numSimDays="
				+ numSimDays + "]";
	}
	
	/**
	 * M�todo que devuelve el tiempo en milisegundos que dura un tick de reloj.
	 * @return Los milisegundos de un tick.
	 */
	public int getUnitTimeMillis() {
		return unitTimeMillis;
	}
	
	/**
	 * M�todo que devuelve la cantidad de ticks que forman un d�a.
	 * @return El n�mero de ticks que hay en un d�a.
	 */
	public int getNumUnitDay() {
		return numUnitDay;
	}

	/**
	 * M�todo que devuelve el n�mero de d�as de la simulaci�n.
	 * @return La cantidad de d�as de la simulaci�n.
	 */
	public int getNumSimDays() {
		return numSimDays;
	}

	/**
	 * M�todo que establece los milisegundos que debe durar cada tick de reloj.
	 * @param unitTimeMillis Milisegundos que pasan entre dos ticks.
	 */
	public void setUnitTimeMillis(int unitTimeMillis) {
		this.unitTimeMillis = unitTimeMillis;
	}
	
	/**
	 * M�todo que establece el n�mero de ticks que ocurren en un d�a.
	 * @param numUnitDay N�mero de ticks que forman un d�a.
	 */
	public void setNumUnitDay(int numUnitDay) {
		this.numUnitDay = numUnitDay;
	}
	
	/**
	 * M�todo que establece el n�mero de d�as de simulaci�n.
	 * @param numSimDays N�mero de d�as de la simulaci�n.
	 */
	public void setNumSimDays(int numSimDays) {
		this.numSimDays = numSimDays;
	}
}
