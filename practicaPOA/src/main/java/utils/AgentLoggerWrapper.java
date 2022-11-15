package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import jade.core.Agent;
import jade.util.Logger;

/**
 * Clase que define un envoltorio para el Logger de jade, ofreciendo un sistema de log con colores.
 * @author pablo y alejandroramon.lopezr@um.es
 */
public class AgentLoggerWrapper {	
	private Logger logger;
	private Agent agent;
	private String color;
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 * @param agent Agente al que pertenece el logger.
	 */
	public AgentLoggerWrapper(Agent agent) {
		this.agent = agent;
		color = ColorManager.getColor(agent.getLocalName());
		logger = Logger.getMyLogger(agent.getLocalName());
		
		// Añadir el logger una vez creado.
		LogManager lm = LogManager.getLogManager();
		lm.addLogger(logger);
	}
	
	/**
	 * Método que añade un handler al logger.
	 * @param fh Handler a añadir.
	 */
	public void addFileHandler(FileHandler fh) {
		logger.addHandler(fh);
	}
	
	/**
	 * Método que permite escribir un mensaje en el log en el nivel de INFO.
	 * @param behaviour Comportamiento que lanza el mensaje.
	 * @param msg Mensaje a loggear.
	 */
	public void info(String behaviour, String msg) {
		Object[] params = {this.agent.getLocalName(), behaviour, color};
		System.out.println(this.agent.getLocalName()+", "+behaviour+", "+msg);
		// Una vez se ha imprimido en pantalla se sustituyen los retornos de carro por etiquetas <br>
		// para añadir saltos de línea en el HTML.
		msg = msg.replace("\n", "<br>");
		this.logger.log(Level.INFO, msg, params);
	}
	
	/**
	 * Método que cierra un handler concreto del logger.
	 * @param fh Handler a cerrar.
	 */
	public void close(FileHandler fh) {
		fh.close();
	}
}

/**
 * Clase que asocia el nombre de cada agente con un color para el log.
 * @author pablo
 */
class ColorManager {
	private static Map<String,String> colorMapping = new HashMap<String, String>();
	private static String[] colors = {"#8BA900", "#0080A9", "#7600A9", "#0017A9",  "#DABF00", "#DA7700", "#DA1600", "#A9008B"};
	private static int index = -1;
	
	/**
	 * Método que retorna un color para el nombre de agente dado.
	 * @param name Nombre de agente.
	 * @return Un String que representa un color en HTML.
	 */
	public static String getColor(String name) {
		String color = colorMapping.get(name);
		if(color == null) {
			index = (index + 1) % colors.length;
			color =  colors[index];
			colorMapping.put(name, color);
		}
		return color;
	}
}
