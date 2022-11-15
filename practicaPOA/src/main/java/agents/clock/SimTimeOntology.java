package agents.clock;

/**
 * Clase que define la estructura de los objetos que almacenan la información de la simulación.
 * @author pablo
 */
public class SimTimeOntology {
	
	// Constantes para los estados.
	public static String RUNNING = "RUNNING";
	public static String END = "END";
	
	// Atributos de la simulación.
	private String simState;
	private int day;
	private int time;

	/**
	 * Constructor de la clase. Inicializa el estado de la simulación a RUNNING.
	 */
	public SimTimeOntology() {
		this.simState = RUNNING;
	}
	
	/**
	 * COnstructor que permite inicializar a un tiempo concreto de la simulación.
	 * @param day Día de la simulación.
	 * @param time Número de ticks que han pasado en el día.
	 */
	public SimTimeOntology(int day, int time) {
		this();
		this.day = day;
		this.time = time;
	}
	
	/**
	 * Método que devuelve el estado de la simulación.
	 * @return El estado de la simulación.
	 */
	public String getSimState() {
		return simState;
	}
	
	/**
	 * Método que establece el estado de la simulación.
	 * @param sim_state Nuevo estado de la simulación.
	 */
	public void setSimState(String sim_state) {
		this.simState = sim_state;
	}
	
	/**
	 * Método que devuelve el día de la simulación.
	 * @return El día actual de la simulación.
	 */
	public int getDay() {
		return day;
	}
	
	/**
	 * Método que devuelve el número de ticks ocurridos en el día.
	 * @return El número de ticks del día actual.
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Método para pasar el objeto de simulación a cadena de caracteres.
	 * @return La cadena equivalente.
	 */
	public String toString() {
		return "(day=" + day + ",time=" + time + ",simState=" + simState + ")";
	}
}
