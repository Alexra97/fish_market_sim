package agents;

import utils.AgentLoggerWrapper;
import utils.AgentLoggingHTMLFormatter;

import java.io.IOException;
import java.util.logging.FileHandler;
import jade.core.Agent;

/**
 * Clase que hereda de Agent y define el logger propio a cada agente.
 * @author pablo
 */
public class POAAgent extends Agent {
	
	private AgentLoggerWrapper logger;
	private FileHandler privHandler;
	
	/**
	 * Método que inicializa el agente creando el logger y su handler privado.
	 * @author alejandroramon.lopezr@um.es
	 */
	public void setup() {
		// Inicializar el logger mediante el Wrapper.
		this.logger = new AgentLoggerWrapper(this);
		
		// Añadir un Handler privado para cada agente, que permite tener un registro de las transacciones del agente.
		try {
			privHandler = new FileHandler("logs/" + getLocalName() + ".html");
		} catch (SecurityException | IOException e1) {
			e1.printStackTrace();
		}
	    privHandler.setFormatter(new AgentLoggingHTMLFormatter(getLocalName()));
		logger.addFileHandler(privHandler);
	}
	
	/**
	 * Método que devuelve el logger del agente.
	 * @return El logger.
	 */
	public AgentLoggerWrapper getLogger() {
		return this.logger;
	}
	
	/**
	 * Método que cierra el logger y mata al agente.
	 */
	public void takeDown() {
		super.takeDown();
		// Cerrar solo el handler privado ya que si cerramos el global ningún agente podrá escribir en él.
		this.logger.close(privHandler);
	}
}
