package agents.buyer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;

import org.yaml.snakeyaml.Yaml;

import agents.TimePOAAgent;
import agents.clock.SimTimeOntology;
import fishmarketOntology.Auction;
import fishmarketOntology.Lot;
import fishmarketOntology.Open;
import fishmarketOntology.Register;
import fishmarketOntology.Withdraw;
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
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.ContractNetResponder;

/**
 * Clase que define la funcionalidad del agente comprador.
 * @author alejandroramon.lopezr@um.es
 */
public class BuyerAgent extends TimePOAAgent {
	
	// Propiedades de la simulaci�n.
	private int numUnitDay;
	private int numDays;
	
	// Variables de acceso r�pido.
	private AID fishmarket;
	private BuyerAgentConfig config;
	
	// Variables propias al comprador.
	private List<Lot> lots;							 // Lotes obtenidos.
	private int unwithdrawnLots;					 // N�mero de lotes sin retirar.
	private float balance; 							 // Saldo del comprador en la lonja.
	private HashMap<String, Float> fishInterest;	 // Porcentaje de inter�s por cada tipo de pescado.
	
	/**
	 * Constructor del comprador. Inicializa las variables.
	 */
	public BuyerAgent() {
		lots = new LinkedList<Lot>();
		unwithdrawnLots = 0;
		balance = 0;
		fishInterest = new HashMap<String, Float>();
	}
	
	/**
	 * M�todo que inicializa el agente.
	 */
	public void setup() {
		super.setup();

		Object[] args = getArguments();
		if (args != null && args.length == 4) {
			// Inicializar las variables restantes.
			String configFile = (String) args[0];
			config = initAgentFromConfigFile(configFile);
			numUnitDay = (Integer) args[1];
			numDays = (Integer) args[2];
			
			// A�adir el handler para escritura com�n a todos los agentes.
			getLogger().addFileHandler((FileHandler) args[3]);
			
			if(config != null) {
				// Registrar al comprador en el DF.
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				ServiceDescription sd = new ServiceDescription();
				sd.setType("buyer");
				sd.setName(getLocalName());
				dfd.addServices(sd);
				try {
					DFService.register(this, dfd);
				} catch(FIPAException fe) {
					fe.printStackTrace();
				}
				
				// Buscar la lonja en el DF.
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
				addBehaviour(new BuyerRegisterBehaviour(this));
				
			} else {
				doDelete();
			}
		} else {
			getLogger().info("ERROR", "Requiere fichero de cofiguraci�n.");
			doDelete();
		}
	}
	
	/**
	 * M�todo que inicializa la configuraci�n del comprador mediante un archivo YAML.
	 * @param fileName Archivo de configuraci�n.
	 * @return Un objeto de configuraci�n del comprador.
	 * @author pablo
	 */
	private BuyerAgentConfig initAgentFromConfigFile(String fileName) {
		BuyerAgentConfig config = null;
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
	 * M�todo que mata al agente.
	 */
	public void takeDown() {
		try {
			DFService.deregister(this);
			String lotsStr = "";
			if (getLots().isEmpty()) lotsStr = "Ninguno";
			else {
				for (Lot lot : getLots()) {
					lotsStr += "\n  -  "+lot;
				}
			}
			// Imprimir la informaci�n del comprador antes de morir.
			getLogger().info("takeDown", "\n - Lotes adquiridos: " + lotsStr + ".\n - Cartera: " + 
					getConfig().getBudget() + "\n" + getLocalName() + " finalizando...");
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		super.takeDown();	}
	
	/**
	 * M�todo que devuelve n�mero de ticks que forman un d�a.
	 * @return El n�mero de ticks.
	 */
	public int getNumUnitDay() {
		return numUnitDay;
	}
	
	/**
	 * M�todo que devuelve el n�mero de d�as de la simulaci�n.
	 * @return El n�mero de d�as.
	 */
	public int getNumDays() {
		return numDays;
	}
	
	/**
	 * M�todo que devuelve el identificador de la lonja para facilitar la comunicaci�n con ella.
	 * @return El AID de la lonja.
	 */
	public AID getFishmarket() {
		return fishmarket;
	}
	
	/**
	 * M�todo que devuelve el objeto de configuraci�n del comprador.
	 * @return El objeto de configuraci�n del comprador.
	 */
	public BuyerAgentConfig getConfig() {
		return config;
	}
	
	/**
	 * M�todo que retorna la lista de lotes obtenidos por el comprador.
	 * @return La lista de lotes del comprador.
	 */
	public List<Lot> getLots() {
		return lots;
	}
	
	/**
	 * M�todo que devuelve el n�mero de lotes comprados pero sin retirar del comprador.
	 * @return El n�mero de lotes sin retirar.
	 */
	public int getUnwithdrawnLots() {
		return unwithdrawnLots;
	}
	
	/**
	 * M�todo que devuelve el saldo restante del comprador en la lonja.
	 * @return El saldo del comprador.
	 */
	public float getBalance() {
		return balance;
	}
	
	/**
	 * M�todo que devuelve el mapa con los porcentajes de inter�s para cada tipo de pescado ofertado.
	 * @return El mapa Pescado - Porcentaje de inter�s.
	 */
	public HashMap<String, Float> getFishInterest() {
		return fishInterest;
	}
	
	/**
	 * M�todo que establece el nuevo n�mero de lotes sin retirar.
	 * @param unwithdrawnLots N�mero de lotes sin retirar actualizado.
	 */
	public void setUnwithdrawnLots(int unwithdrawnLots) {
		this.unwithdrawnLots = unwithdrawnLots;
	}
	
	/**
	 * M�todo que establece el saldo del comprador en la lonja.
	 * @param balance Saldo del comprador actualizado.
	 */
	public void setBalance(float balance) {
		this.balance = balance;
	}
}

/**
 * Clase que define el comportamiento para el PROTOCOLO-REGISTRO-COMPRADOR.
 * @author alejandroramon.lopezr@um.es
 */
class BuyerRegisterBehaviour extends OneShotBehaviour {
	
	BuyerAgent a;
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 * @param agent Agente que realiza el comportamiento.
	 */
	public BuyerRegisterBehaviour(BuyerAgent agent) {
		super(agent);
		a = agent;
	}
	
	/**
	 * M�todo que implementa la funcionalidad del comportamiento.
	 */
	@Override
	public void action() {
		// Crear peticion de registro para el RRC de la lonja
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.addReceiver(a.getFishmarket());
		request.setLanguage(a.getCodec().getName());
		request.setOntology(a.getOntology().getName()); 
		
		// Crear un objeto Register para representar la acci�n.
		Register reg = new Register();
		reg.setType("buyer");
		Action actionOp = new Action(a.getAID(), reg);
		
		// A�adir el comportamiento para comenzar un protocolo de interacci�n FIPA-Request.
		try {
			a.getContentManager().fillContent(request, actionOp);
			a.addBehaviour(new AchieveREInitiator(a, request) {
				
				/**
				 * M�todo que maneja la recepci�n de mensajes ACL de tipo INFORM.
				 * @param inform Mensaje recibido.
				 */
				@Override
				protected void handleInform(ACLMessage inform) {
					a.getLogger().info("PROTOCOLO-REGISTRO-COMPRADOR", a.getLocalName()+" registrado correctamente.");
					// Cuando el comprador se ha registrado se lanza el PROTOCOLO-APERTURA-CREDITO.
					a.addBehaviour(new OpenBalanceBehaviour(a));
				}
			});	
		}
		catch (CodecException | OntologyException e) {
			e.printStackTrace();
		}
	}
}

/**
 * Clase que define el comportamiento para el PROTOCOLO-APERTURA-CREDITO.
 * @author alejandroramon.lopezr@um.es
 */
class OpenBalanceBehaviour extends OneShotBehaviour {
	
	BuyerAgent a;
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 * @param agent Agente que realiza el comportamiento.
	 */
	public OpenBalanceBehaviour(BuyerAgent agent) {
		super(agent);
		a = agent;
	}
	
	/**
	 * M�todo que implementa la funcionalidad del comportamiento.
	 */
	@Override
	public void action() {
		// Crear petici�n de apertura de cr�dito para el RGC de la lonja.
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.addReceiver(a.getFishmarket());
		request.setLanguage(a.getCodec().getName());
		request.setOntology(a.getOntology().getName()); 
		
		// Crear un objeto Open para representar la acci�n.
		Open o = new Open();
		a.setBalance(a.getConfig().getBudget());
		o.setBalance(a.getBalance());
		Action actionOp = new Action(a.getAID(), o);
		
		// A�adir el comportamiento para comenzar un protocolo de interacci�n FIPA-Request.
		try {
			a.getContentManager().fillContent(request, actionOp);
			a.addBehaviour(new AchieveREInitiator(a, request) {
				
				/**
				 * M�todo que maneja la recepci�n de mensajes ACL de tipo INFORM.
				 * @param inform Mensaje recibido.
				 */
				@Override
				protected void handleInform(ACLMessage inform) {
					a.getLogger().info("PROTOCOLO-APERTURA-CREDITO", "Saldo de "+a.getLocalName()+" abierto correctamente.");
					// Cuando el comprador tiene la l�nea de cr�dito abierta se lanzan los protocolos de subasta y retirada de compras.
					a.addBehaviour(new BidderBehaviour(a));
					a.addBehaviour(new WithdrawInitiatorBehaviour(a, a.getNumUnitDay(), a.getNumDays()));
				}
			});	
		}
		catch (CodecException | OntologyException e) {
			e.printStackTrace();
		}
	}
}

/**
 * Clase que define el comportamiento para el PROTOCOLO-SUBASTA.
 * @author alejandroramon.lopezr@um.es
 */
class BidderBehaviour extends OneShotBehaviour {
	
	BuyerAgent a;
	float price;
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 * @param agent Agente que realiza el comportamiento.
	 */
	public BidderBehaviour(BuyerAgent agent) {
		super(agent);
		a = agent;
	}
	
	/**
	 * M�todo que implementa la funcionalidad del comportamiento.
	 */
	@Override
	public void action() {
		
		// Crear plantilla que marca los mensajes que debe recibir el comportamiento Responder.
		MessageTemplate mt = 
				ContractNetResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION);

		// A�adir el comportamiento para recibir mensajes del protocolo de interacci�n FIPA-IteratedContractNet.
		a.addBehaviour(new ContractNetResponder(a, mt) {
			
			/**
			 * M�todo que maneja la recepci�n de mensajes ACL de tipo CFP.
			 * @param cfp Mensaje recibido.
			 * @return Un mensaje de tipo PROPOSAL o REFUSE para aceptar/rechazar la oferta.
			 */
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) {
				ACLMessage reply = cfp.createReply();
				try {
					// Obtener el lote subastado y su precio.
					Action ce = (Action) a.getContentManager().extractContent(cfp);
					Auction auc = (Auction) ce.getAction();
					Lot lot = auc.getLot();
					price = auc.getPrice();
					
					// Si el precio es mayor que el saldo del comprador se rechaza la oferta.
					if (price > a.getBalance()) {
						reply.setPerformative(ACLMessage.REFUSE);
						a.getLogger().info("SUBASTA", "A " + a.getLocalName() + " NO le interesa el lote de " + 
								lot.getKind() + " (" + price + ") ya que no tiene fondos suficientes para pagarlo (" +
								a.getBalance() + ")");
					}
					// En otro caso se comprueba el inter�s (si existe) o se genera uno aleatoriamente.
					else {
						String fish = lot.getKind();
						float interestPercent;
						if (a.getFishInterest().containsKey(fish)) {
							interestPercent = a.getFishInterest().get(fish);
						}
						else {
							interestPercent = (float) Math.round((Math.random()*101) * 100) / 100;
							a.getFishInterest().put(fish, interestPercent);
						}	
						
						// Estimar valor multiplicando el inter�s por el saldo.
						float estimatedValue = (float) Math.round((interestPercent/100)*a.getBalance() * 100) / 100;
						
						// Si el precio es menor o igual que el valor estimado se acepta la oferta.
						if (price <= estimatedValue) {
							reply.setPerformative(ACLMessage.PROPOSE);
							a.getLogger().info("SUBASTA", "A " + a.getLocalName() + " le interesa el lote de " + 
									lot.getKind() + " (" + price + ")\n" +
									"[interes = " + interestPercent + "% / " + estimatedValue + "]");
						}
						// En otro caso se rechaza.
						else {
							reply.setPerformative(ACLMessage.REFUSE);
							a.getLogger().info("SUBASTA", "A " + a.getLocalName() + " NO le interesa el lote de " + 
									lot.getKind() + " (" + price + ")\n" +
									"[interes = " + interestPercent + "% / " + estimatedValue + "]");
						}
					}
					
				} catch (CodecException | OntologyException e) {
					e.printStackTrace();
				}

				return reply;
			}
			
			/**
			 * M�todo que maneja la recepci�n de mensajes ACL de tipo ACCEPT_PROPOSAL.
			 * @param cfp Mensaje CFP inicial.
			 * @param propose Mensaje enviado como respuesta a la oferta de subasta.
			 * @param accept Mensaje recibido.
			 * @return Un mensaje de tipo INFORM de confirmaci�n.
			 */
			@Override
			protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
				// Decrementar el saldo del comprador y aumentar el n�mero de lotes sin retirar.
				a.setBalance(a.getBalance()-price);
				a.setUnwithdrawnLots(a.getUnwithdrawnLots()+1);
				
				// Crear el mensaje INFORM de respuesta.
				ACLMessage inform = accept.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				inform.setContent("Auction confirmated");
				return inform;
			}
		});
	}
}

/**
 * Clase que define el comportamiento para el PROTOCOLO-RETIRADA-COMPRAS.
 * @author alejandroramon.lopezr@um.es
 */
class WithdrawInitiatorBehaviour extends SimpleBehaviour {
	
	BuyerAgent a;
	int nud;					// N�mero de ticks al d�a.
	int nd;						// N�mero de d�as de la simulaci�n.
	int timeMark;				// Marca de tiempo que indica cuando ha pasado medio d�a de simulaci�n.
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 * @param agent Agente que realiza el comportamiento.
	 * @param numUnitDay N�mero de ticks por d�a.
	 * @param numDays N�mero de d�as de simulaci�n.
	 */
	public WithdrawInitiatorBehaviour(BuyerAgent agent, int numUnitDay, int numDays) {
		super(agent);
		a = agent;
		nud = numUnitDay;
		nd = numDays;
		timeMark = nud/2;
	}
	
	/**
	 * M�todo que implementa la funcionalidad del comportamiento.
	 */
	@Override
	public void action() {
		
		// Si hay lotes sin retirar y ha pasado medio d�a desde la �ltima petici�n, se vuelve a realizar la petici�n.
		// Adem�s si nos encontramos en el �ltimo tick antes de finalizar la simulaci�n realizaremos la petici�n mientras
		// haya lotes sin retirar, para conseguir lotes que hayan sido subastados a �ltima hora.
		if ((a.getUnwithdrawnLots() > 0) && ((a.getSimTime().getTime() == timeMark) || 
				((a.getSimTime().getTime() == nud-1) && (a.getSimTime().getDay() == nd-1)))) {
			if (timeMark == nud/2) timeMark = nud-1;
			else timeMark = nud/2;
			
			// Crear peticion de retirada de compras al RGC de la lonja
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			request.addReceiver(a.getFishmarket());
			request.setLanguage(a.getCodec().getName());
			request.setOntology(a.getOntology().getName()); 
			
			// Crear un objeto Withdraw para representar la acci�n.
			Withdraw wd = new Withdraw();
			Action actionOp = new Action(a.getAID(), wd);
			
			// A�adir el comportamiento para comenzar un protocolo de interacci�n FIPA-Request.
			try {
				a.getContentManager().fillContent(request, actionOp);
				a.addBehaviour(new AchieveREInitiator(a, request) {
					
					/**
					 * M�todo que maneja la recepci�n de mensajes ACL de tipo INFORM.
					 * @param inform Mensaje recibido.
					 */
					@Override
					protected void handleInform(ACLMessage inform) {
						try {
							Action ce = (Action) a.getContentManager().extractContent(inform);
							if (ce.getAction() instanceof Withdraw) {
								float amount = 0;
								Withdraw wdraw = (Withdraw) ce.getAction();
								
								// Obtener los lotes del mensaje y sumar su precio.
								for (Lot lot : wdraw.getLots()) {
									a.getLots().add(lot);
									amount += lot.getFinalPrice();
								};
								
								// Disminuir el n�mero de lotes sin retirar y la cartera del comprador.
								a.setUnwithdrawnLots(a.getUnwithdrawnLots() - wdraw.getLots().size());
								a.getConfig().setBudget(a.getConfig().getBudget() - amount);
								String lotsStr = "";
								for (Lot lot : wdraw.getLots()) {
									lotsStr += "\n  -  "+lot;
								}
								a.getLogger().info("PROTOCOLO-RETIRADA-COMPRAS", "Lotes retirados:" + lotsStr + "\nCartera de " +
												   a.getLocalName() + " actualizada (" + a.getConfig().getBudget() + ")");
							}
						} catch (CodecException | OntologyException e) {
							e.printStackTrace();
						}
					}
				});	
			}
			catch (CodecException | OntologyException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * M�todo que indica cuando debe salir el comportamiento de la cola de comportamientos activos del agente.
	 * @return True si debe acabar, False si no.
	 */
	@Override
	public boolean done() {
		// Si se acaba la simulaci�n o si el comprador no tiene ni saldo ni lotes sin retirar, el comportamiento puede acabar.
		if (a.getSimTime().getSimState().equals(SimTimeOntology.END)) return true;
		else if ((a.getUnwithdrawnLots() == 0) && (a.getBalance() == 0)) return true;
		return false;
	}
}