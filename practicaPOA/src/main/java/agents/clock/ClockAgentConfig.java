package agents.clock;

/**
 * Clase que define la configuración del reloj.
 * @author pablo
 */
public class ClockAgentConfig {
	private int unitTimeMillis;
	private int numUnitDay;
	private int numSimDays;
	
	/**
	 * Método que convierte a cadena de caracteres le configuración del reloj.
	 * @return La cadena equivalente.
	 */
	@Override
	public String toString() {
		return "ClockAgentConfig [unitTimeMillis=" + unitTimeMillis + ", numUnitDay=" + numUnitDay + ", numSimDays="
				+ numSimDays + "]";
	}
	
	/**
	 * Método que devuelve el tiempo en milisegundos que dura un tick de reloj.
	 * @return Los milisegundos de un tick.
	 */
	public int getUnitTimeMillis() {
		return unitTimeMillis;
	}
	
	/**
	 * Método que devuelve la cantidad de ticks que forman un día.
	 * @return El número de ticks que hay en un día.
	 */
	public int getNumUnitDay() {
		return numUnitDay;
	}

	/**
	 * Método que devuelve el número de días de la simulación.
	 * @return La cantidad de días de la simulación.
	 */
	public int getNumSimDays() {
		return numSimDays;
	}

	/**
	 * Método que establece los milisegundos que debe durar cada tick de reloj.
	 * @param unitTimeMillis Milisegundos que pasan entre dos ticks.
	 */
	public void setUnitTimeMillis(int unitTimeMillis) {
		this.unitTimeMillis = unitTimeMillis;
	}
	
	/**
	 * Método que establece el número de ticks que ocurren en un día.
	 * @param numUnitDay Número de ticks que forman un día.
	 */
	public void setNumUnitDay(int numUnitDay) {
		this.numUnitDay = numUnitDay;
	}
	
	/**
	 * Método que establece el número de días de simulación.
	 * @param numSimDays Número de días de la simulación.
	 */
	public void setNumSimDays(int numSimDays) {
		this.numSimDays = numSimDays;
	}
}
