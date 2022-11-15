package agents.seller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.logging.FileHandler;

import org.yaml.snakeyaml.Yaml;

import agents.TimePOAAgent;
import fishmarketOntology.Collect;
import fishmarketOntology.Deposit;
import fishmarketOntology.Lot;
import fishmarketOntology.Register;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

/**
 * Clase que define la funcionalidad del agente vendedor.
 * @author alejandroramon.lopezr@um.es
 */
public class SellerAgent extends TimePOAAgent {
	
	// Variables propias del vendedor.
	private float fund;						// Fondos del vendedor obtenidos por la venta de lotes.
	
	// Variables de acceso rápido.
	private AID fishmarket;
	private SellerAgentConfig config;
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 */
	public SellerAgent() {
		fund = 0;
	}
		
	/**
	 * Clase que implementa la funcionalidad del agente.
	 */
	public void setup() {
		super.setup();
		
		Object[] args = getArguments();
		if (args != null && args.length == 2) {
			
			// Inicializar el resto de atributos.
			String configFile = (String) args[0];
			config = initAgentFromConfigFile(configFile);
			fund = 0;
			
			// Añadir el handler de escritura común a todos los agentes.
			getLogger().addFileHandler((FileHandler) args[1]);
			
			if(config != null) {
				// Registrar al vendedor en el DF.
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				ServiceDescription sd = new ServiceDescription();
				sd.setType("seller");
				sd.setName(getLocalName());
				dfd.addServices(sd);
				try {
					DFService.register(this, dfd);
				} catch(FIPAException fe) {
					fe.printStackTrace();
				}
				
				// Buscara a la lonja en el DF.
				dfd = new DFAgentDescription();
				sd = new ServiceDescription();
				sd.setType("fishmarket_simulation");
				dfd.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(this, dfd);
					fishmarket = result[0].getName();
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
				
				// Lanzar protocolo de registro.
				addBehaviour(new SellerRegisterBehaviour(this));		
			} else {
				doDelete();
			}
		} else {
			getLogger().info("ERROR", "Requiere fichero de cofiguración.");
			doDelete();
		}
	}
	
	/**
	 * Método que inicializa el objeto configuración a partir de un archivo YAML.
	 * @param fileName Archivo de configuración.
	 * @return Un objeto de configuración de lonja.
	 * @author pablo
	 */
	private SellerAgentConfig initAgentFromConfigFile(String fileName) {
		SellerAgentConfig config = null;
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream;
			inputStream = new FileInputStream(fileName);
			config = yaml.load(inputStream);
			getLogger().info("initAgentFromConfigFile", config.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return config;
	}
	
	/**
	 * Método que mata al agente.
	 */
	public void takeDown() {
		try {
			DFService.deregister(this);
			getLogger().info("takeDown", "\n - Ingresos de " + getLocalName() + ": " + 
					getFund() + "\n - " + getLocalName() + " finalizando...");
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		super.takeDown();
	}
	
	/**
	 * Método que devuelve los fondos actuales del vendedor (los que ha cobrado).
	 * @return Los fondos del vendedor.
	 */
	public float getFund() {
		return fund;
	}
	
	/**
	 * Método que devuelve el identificador de la lonja para facilitar la comunicación con ella.
	 * @return El AID de la lonja.
	 */
	public AID getFishmarket() {
		return fishmarket;
	}
	
	/**
	 * Método que devuelve el objeto de configuración del vendedor.
	 * @return El objeto de configuración del vendedor.
	 */
	public SellerAgentConfig getConfig() {
		return config;
	}
	
	/**
	 * Método que establece los nuevos fondos del vendedor.
	 * @param fund Fondos del vendedor actualizados.
	 */
	public void setFund(float fund) {
		this.fund = fund;
	}
}

/**
 * Clase que define el comportamiento para el PROTOCOLO-REGISTRO-VENDEDOR.
 * @author alejandroramon.lopezr@um.es
 */
class SellerRegisterBehaviour extends OneShotBehaviour {
	
	SellerAgent a;
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 * @param agent Agente que realiza el comportamiento.
	 */
	public SellerRegisterBehaviour(SellerAgent agent) {
		super(agent);
		a = agent;
	}
	
	/**
	 * Método que implementa la funcionalidad del comportamiento.
	 */
	@Override
	public void action() {
		// Mandar peticion de registro al RRV de la lonja.
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.addReceiver(a.getFishmarket());
		request.setLanguage(a.getCodec().getName());
		request.setOntology(a.getOntology().getName()); 
		
		// Crear un objeto Register para representar la acción.
		Register reg = new Register();
		reg.setType("seller");
		Action actionOp = new Action(a.getAID(), reg);
		
		// Añadir el comportamiento para comenzar un protocolo de interacción FIPA-Request.
		try {
			a.getContentManager().fillContent(request, actionOp);
			a.addBehaviour(new AchieveREInitiator(a, request) {
				
				/**
				 * Método que maneja la recepción de mensajes ACL de tipo INFORM.
				 * @param inform Mensaje recibido.
				 */
				@Override
				protected void handleInform(ACLMessage inform) {
					a.getLogger().info("PROTOCOLO-REGISTRO-VENDEDOR", a.getLocalName()+" registrado correctamente.");
					// Cuando el vendedor ha sido registrado se lanza el protocolo de deposito y de cobro.
					a.addBehaviour(new DepositInitiatorBehaviour(a));
					a.addBehaviour(new CollectResponderBehaviour(a));
				}
			});	
		}
		catch (CodecException | OntologyException e) {
			e.printStackTrace();
		}
	}
}

/**
 * Clase que define el comportamiento para el PROTOCOLO-DEPOSITO.
 * @author alejandroramon.lopezr@um.es
 */
class DepositInitiatorBehaviour extends SimpleBehaviour {
	
	SellerAgent a;
	LinkedList<Lot> notArrivedLots;
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 * @param agent Agente que realiza el comportamiento.
	 */
	public DepositInitiatorBehaviour(SellerAgent agent) {
		super(agent);
		a = agent;
	}
		
	/**
	 * Método que implementa la funcionalidad del comportamiento.
	 */
	@Override
	public void action() {
		// Crear un objeto Deposit para representar la acción.
		Deposit dep = new Deposit();
		LinkedList<Lot> arrivedLots = new LinkedList<Lot>();
		notArrivedLots = new LinkedList<Lot>();;
	
		// Rellenar la lista de lotes llegados con los lotes cuyo tiempo de llegada ya ocurrió.
		for (Lot l : a.getConfig().getLots()) {
			if ((l.getDay() <= a.getSimTime().getDay()) &&
				(l.getTime() <= a.getSimTime().getTime())) {
				arrivedLots.add(l);
			}
			// El resto forman la lista de lotes no llegados.
			else notArrivedLots.add(l);
		}
		
		// Si la lista de lotes llegados contiene lotes se depositan.
		if (!arrivedLots.isEmpty()) {
			
			// Rellenar el objeto Deposit y actualizar los lotes del vendedor.
			dep.setLots(arrivedLots);
			a.getConfig().setLots(notArrivedLots);
			Action actionOp = new Action(a.getAID(), dep);
			
			// Crear petición de depósito para el RRV de la lonja.
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			request.addReceiver(a.getFishmarket());
			request.setLanguage(a.getCodec().getName());
			request.setOntology(a.getOntology().getName()); 
			
			// Añadir el comportamiento para comenzar un protocolo de interacción FIPA-Request.
			try {
				a.getContentManager().fillContent(request, actionOp);
				myAgent.addBehaviour(new AchieveREInitiator(a, request) {
					
					/**
					 * Método que maneja la recepción de mensajes ACL de tipo INFORM.
					 * @param inform Mensaje recibido.
					 */
					@Override
					protected void handleInform(ACLMessage inform) {
						a.getLogger().info("PROTOCOLO-DEPOSITO", "Lotes del "+a.getLocalName()+" depositados correctamente.");
					}
				});	
			} catch (CodecException | OntologyException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Método que indica cuando debe salir el comportamiento de la cola de comportamientos activos del agente.
	 * @return True si debe acabar, False si no.
	 */
	@Override
	public boolean done() {
		// Cuando al vendedor no le queden lotes por llegar el comportamiento finaliza.
		if (notArrivedLots.isEmpty()) return true;
		else return false;
	}
}

/**
 * Clase que define el comportamiento para el PROTOCOLO-COBRO.
 * @author alejandroramon.lopezr@um.es
 */
class CollectResponderBehaviour extends OneShotBehaviour {
	
	SellerAgent a;
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 * @param agent Agente que realiza el comportamiento.
	 */
	public CollectResponderBehaviour(SellerAgent agent) {
		super(agent);
		a = agent;
	}

	/**
	 * Método que implementa la funcionalidad del comportamiento.
	 */
	@Override
	public void action() {
		
		// Crear plantilla que marca los mensajes que debe recibir el comportamiento Responder.
		MessageTemplate mt = 
				AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);

		// Añadir el comportamiento para recibir mensajes del protocolo de interacción FIPA-Request.
		a.addBehaviour(new AchieveREResponder(a, mt) {	
			
			/**
			 * Método que maneja la recepción de mensajes ACL de tipo REQUEST.
			 * @param request Mensaje recibido.
			 * @return Un mensaje de tipo AGREE o REFUSE para aceptar/rechazar la petición.
			 */
			@Override
			protected ACLMessage handleRequest (ACLMessage request) {
				ACLMessage reply = request.createReply();
				reply.setPerformative(ACLMessage.REFUSE);
				
				try {
					Action ce = (Action) a.getContentManager().extractContent(request);
					if (ce.getAction() instanceof Collect) {
						Collect col = (Collect) ce.getAction();
						
						boolean accept = true;
						// Si el vendedor tiene lotes pendientes de llegar en el día actual el cobro es rechazado.
						for (Lot lot : a.getConfig().getLots()) {
							if (lot.getDay() == a.getSimTime().getDay()) {
								accept = false;
								break;
							}
						}
						// a no ser que el cobro tenga un valor de 500 o más, en cuyo caso siempre se acepta.
						if ((col.getFund() >= 500) || (accept)) reply.setPerformative(ACLMessage.AGREE);
					}
					
				} catch (CodecException | OntologyException e) {
					e.printStackTrace();
				}	
				return reply;
			}
			
			/**
			 * Método que realiza la acción requerida y devuelve un mensaje de tipo INFORM.
			 * @param request Petición recibida en el método anterior.
			 * @param response Respuesta devuelta, en caso de ser un REFUSE no se ejecuta el método.
			 * @return Un mensaje de tipo INFORM de confirmación.
			 */
			@Override
			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
				try {
					Action ce = (Action) a.getContentManager().extractContent(request);
					if (ce.getAction() instanceof Collect) {
						Collect col = (Collect) ce.getAction();
						// Obtener los fondos y añadirlos al vendedor.
						a.setFund(a.getFund() + col.getFund());
						a.getLogger().info("PROTOCOLO-COBRO", "Fondos de "+a.getLocalName()+" incrementados en " + col.getFund() + 
										   " (" + a.getFund() + ")");
					}
				} catch (CodecException | OntologyException e) {
					e.printStackTrace();
				}
				
				// Crear y enviar una confirmación en forma de INFORM.
				ACLMessage informDone = request.createReply();
				informDone.setPerformative(ACLMessage.INFORM);
				informDone.setContent("Collect done");
				return informDone;
			}
		});
	}
}
