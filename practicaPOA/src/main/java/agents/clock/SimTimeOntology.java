package agents.clock;

/**
 * Clase que define la estructura de los objetos que almacenan la informaci�n de la simulaci�n.
 * @author pablo
 */
public class SimTimeOntology {
	
	// Constantes para los estados.
	public static String RUNNING = "RUNNING";
	public static String END = "END";
	
	// Atributos de la simulaci�n.
	private String simState;
	private int day;
	private int time;

	/**
	 * Constructor de la clase. Inicializa el estado de la simulaci�n a RUNNING.
	 */
	public SimTimeOntology() {
		this.simState = RUNNING;
	}
	
	/**
	 * COnstructor que permite inicializar a un tiempo concreto de la simulaci�n.
	 * @param day D�a de la simulaci�n.
	 * @param time N�mero de ticks que han pasado en el d�a.
	 */
	public SimTimeOntology(int day, int time) {
		this();
		this.day = day;
		this.time = time;
	}
	
	/**
	 * M�todo que devuelve el estado de la simulaci�n.
	 * @return El estado de la simulaci�n.
	 */
	public String getSimState() {
		return simState;
	}
	
	/**
	 * M�todo que establece el estado de la simulaci�n.
	 * @param sim_state Nuevo estado de la simulaci�n.
	 */
	public void setSimState(String sim_state) {
		this.simState = sim_state;
	}
	
	/**
	 * M�todo que devuelve el d�a de la simulaci�n.
	 * @return El d�a actual de la simulaci�n.
	 */
	public int getDay() {
		return day;
	}
	
	/**
	 * M�todo que devuelve el n�mero de ticks ocurridos en el d�a.
	 * @return El n�mero de ticks del d�a actual.
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * M�todo para pasar el objeto de simulaci�n a cadena de caracteres.
	 * @return La cadena equivalente.
	 */
	public String toString() {
		return "(day=" + day + ",time=" + time + ",simState=" + simState + ")";
	}
}
