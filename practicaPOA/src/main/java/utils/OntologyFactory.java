package utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import agents.clock.SimTimeOntology;

/**
 * Clase que define la factor�a que crea los objetos SimTimeOntology a partir de un String y viceversa.
 * @author pablo
 *
 */
public class OntologyFactory {

	/**
	 * M�todo que retorna un objeto de la simulaci�n a partir de una cadena.
	 * @param json Cadena que define el objeto de la ontolog�a de la simulaci�n.
	 * @return Un objeto SimTimeOntology equivalente a la cadena pasada como par�metro.
	 */
	public static SimTimeOntology getSimTimeOntologyObject(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(json, SimTimeOntology.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * M�todo que retorna una cadena de caracteres a partir de un objeto SimTimeOntology.
	 * @param obj Objeto que representa el momento y estado de la simulaci�n.
	 * @return Una cadena equivalente al objeto pasado como par�metro.
	 */
	public static String getSimTimeOntologyJSON(SimTimeOntology obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
