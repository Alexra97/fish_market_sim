package utils;

import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Clase que define el formato del log en HTML.
 * @author pablo y alejandroramon.lopezr@um.es
 */
public class AgentLoggingHTMLFormatter extends java.util.logging.Formatter {
	
	private String name;					// Nombre de agente o del escenario para indicarlo en el título del log.
	
	/**
	 * Constructor de la clase. Inicializa los atributos.
	 * @param n Nombre de agente o escenario.
	 */
	public AgentLoggingHTMLFormatter(String n) {
		name = n;
	}
	
	/**
	 * Método que crea una cadena de caracteres formateada a partir de un LogRecord dado.
	 * @param record LogRecord a pasar a cadena.
	 * @return La cadena formateada.
	 */
	@Override
	public String format(LogRecord record) {
		Object[] params = record.getParameters();
		return ("<tr><td><font color=\""+params[2]+"\">" + (new Date(record.getMillis())).toString() + "</font></td>"+
				"<td><font color=\""+params[2]+"\">"+params[0]+"</font></td>"+
				"<td><font color=\""+params[2]+"\">"+params[1]+"</font></td>"+
		"<td><font color=\""+params[2]+"\">" + record.getMessage() + "</font></td></tr>\n");
	}

	/**
	 * Método que devuelve la cabecera formateada a registrar en el log.
	 * @param h Handler donde escribir la cabecera (opcional).
	 * @return La cabecera formateada.
	 */
	@Override
	public String getHead(Handler h) {
		return ("<html>\n "+
	"<head>\n" + 
	"  <title>" + name +"</title>\n" + 
	"  <meta charset=\"utf-8\">\n" + 
	"  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" + 
	"  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css\">\n" + 
	"  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n" + 
	"  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js\"></script>\n" + 
	"</head>"
				+" <body>\n" + "<table class=\"table table-hover\">\n<tr>"+
				"<td>Time</td>"+
				"<td>Agent</td>"+
				"<td>Behaviour</td>"+
				"<td>Log Message</td></tr>\n");
	}

	/**
	 * Método que devuelve el pie del log formateado para registrarlo.
	 * @param h Handler donde escribir el pie (opcional).
	 * @return El pie formateado.
	 */
	@Override
	public String getTail(Handler h) {
		return ("</table>\n</body>\n</html>");
	}
}
