package agents.fishmarket;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.FileHandler;

import org.yaml.snakeyaml.Yaml;

import agents.TimePOAAgent;
import agents.clock.SimTimeOntology;
import fishmarketOntology.Auction;
import fishmarketOntology.Collect;
import fishmarketOntology.Deposit;
import fishmarketOntology.Lot;
import fishmarketOntology.Open;
import fishmarketOntology.Register;
import fishmarketOntology.Withdraw;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
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
import jade.proto.ContractNetInitiator;
import utils.OntologyFactory;

/**
 * Clase que define la funcionalidad del agente lonja.
 * @author alejandroramon.lopezr@um.es
 */
public class FishMarketAgent extends TimePOAAgent {
	
	// Propiedades de la simulaci�n.
	private int numUnitDay;
	private int numDays;
	
	// Variables propias de la lonja.
	private float income;							// Ingresos.
	private int lotsCount;							// N�mero de lotes registrados, para establecer el siguiente id de lote.
	private int auctionedLotsCount;					// N�mero de lotes subastados del total de lotes disponibles.
	private List<AID> sellers;						// Lista de vendedores registrados.
	private HashMap<AID, Float> sellersFund;		// Fondos obtenidos por los vendedores en la venta de lotes.
	private HashMap<AID, Boolean> confirmations;	// Se usa en el control de la conversaci�n en el PROTOCOLO-SUBASTA.
	private List<AID> buyers;						// Lista de compradores registrados.
	private HashMap<AID, Float> buyersBalance;		// Saldo de los compradores.
	private List<Lot> lots;							// Lista de lotes depositados.
	private boolean[] freeLanes;					// Cintas libres para subastar.
	
	// Variables de acceso r�pido.
	private FishMarketAgentConfig config;
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 */
	public FishMarketAgent() {
		income = lotsCount = auctionedLotsCount = 0;
		sellers = new LinkedList<AID>();
		sellersFund = new HashMap<AID, Float>();
		confirmations = new HashMap<AID, Boolean>();
		buyers = new LinkedList<AID>();
		buyersBalance = new HashMap<AID, Float>();
		lots = new LinkedList<Lot>();
	}
	
	/**
	 * Clase que implementa la funcionalidad del agente.
	 */
	public void setup() {
		super.setup();
		
		Object[] args = getArguments();
		if (args != null && args.length == 4) {
			
			// Inicializar el resto de atributos.
			String configFile = (String) args[0];
			config = initAgentFromConfigFile(configFile);
			numUnitDay = (Integer) args[1];
			numDays = (Integer) args[2];
			
			// A�adir el handler de escritura com�n a todos los agentes.
			getLogger().addFileHandler((FileHandler) args[3]);
			
			if(config != null) {
				// Inicializar el registro de cintas libres
				freeLanes = new boolean[config.getNumberOfLanes()];
				for (int i = 0; i < freeLanes.length; i++) freeLanes[i] = true;
				
				// Registrar lonja en el DF
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				ServiceDescription sd = new ServiceDescription();
				sd.setType("fishmarket_simulation");
				sd.setName(getLocalName());
				dfd.addServices(sd);
				try {
					DFService.register(this, dfd);
				} catch(FIPAException fe) {
					fe.printStackTrace();
				}
				
				// Coger el RD y lanzar el resto de roles:
				ParallelBehaviour pb = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
				// RRV, RRC, RGC.
				pb.addSubBehaviour(new ReceiverAndManagerBehaviour(this));
				// Un RS por cinta.
				for (int i = 0; i < freeLanes.length; i++) pb.addSubBehaviour(new AuctioneerBehaviour(this, freeLanes, i));
				// RGV.
				pb.addSubBehaviour(new CollectInitiatorBehaviour(this, numUnitDay, numDays));
				addBehaviour(pb);
			}
		} else {
			getLogger().info("ERROR", "Requiere fichero de cofiguraci�n.");
			doDelete();
		}
	}
	
	/**
	 * M�todo que inicializa el objeto configuraci�n a partir de un archivo YAML.
	 * @param fileName Archivo de configuraci�n.
	 * @return Un objeto de configuraci�n de lonja.
	 * @author pablo
	 */
	private FishMarketAgentConfig initAgentFromConfigFile(String fileName) {
		FishMarketAgentConfig config = null;
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
			getLogger().info("takeDown", "\n - Ingresos de " + getLocalName() + ": " +
							getIncome() + "\n - " + getLocalName() + " finalizando...");
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		super.takeDown();	
	}
	
	/**
	 * M�todo que devuelve los ingresos actuales de la lonja.
	 * @return Los ingresos de la lonja.
	 */
	public float getIncome() {
		return income;
	}
	
	/**
	 * M�todo que devuelve el n�mero de lotes registrados por la lonja.
	 * @return El n�mero de lotes registrados.
	 */
	public int getLotsCount() {
		return lotsCount;
	}
	
	/**
	 * M�todo que devuelve el n�mero de lotes que han sido subastados del total de lotes de la lonja.
	 * @return N�mero de lotes subastados.
	 */
	public int getAuctionedLotsCount() {
		return auctionedLotsCount;
	}
	
	/**
	 * M�todo que devuelve la lista de vendedores registrados.
	 * @return Los vendedores registrados.
	 */
	public List<AID> getSellers() {
		return sellers;
	}
	
	/**
	 * M�todo que devuelve los fondos obtenidos por los vendedores con los lotes vendidos.
	 * @return Los fondos de los vendedores.
	 */
	public HashMap<AID, Float> getSellersFund() {
		return sellersFund;
	}
	
	/**
	 * M�todo que devuelve que compradores realizaron su confirmaci�n en la puja de un lote.
	 * @return Los compradores que realizaron la confirmaci�n.
	 */
	public HashMap<AID, Boolean> getConfirmations() {
		return confirmations;
	}
	
	/**
	 * M�todo que devuelve la lista de compradores registrados en la lonja.
	 * @return Los compradores registrados.
	 */
	public List<AID> getBuyers() {
		return buyers;
	}
	
	/**
	 * M�todo que devuelve el saldo de los compradores registrados.
	 * @return El saldo de los compradores.
	 */
	public HashMap<AID, Float> getBuyersBalance() {
		return buyersBalance;
	}
	
	/**
	 * M�todo que devuelve los lotes depositados en la lonja (que no han sido ni retirados ni desechados). 
	 * @return Los lotes que se encuentran en la lonja.
	 */
	public List<Lot> getLots() {
		return lots;
	}
	
	/**
	 * M�todo que devuelve el objeto de configuraci�n de la lonja.
	 * @return La configuraci�n de la lonja.
	 */
	public FishMarketAgentConfig getConfig() {
		return config;
	}
	
	/**
	 * M�todo que establece los ingresos de la lonja.
	 * @param income Ingresos actualizados.
	 */
	public void setIncome(float income) {
		this.income = income;
	}
	
	/**
	 * M�todo que establece el n�mero de lotes registrados. 
	 * @param lotsCount N�mero de lotes actualizado.
	 */
	public void setLotsCount(int lotsCount) {
		this.lotsCount = lotsCount;
	}
	
	/**
	 * M�todo que establece el n�mero de lotes subastados.
	 * @param auctionedLotsCount N�mero de lotes subastados actualizado.
	 */
	public void setAuctionedLotsCount(int auctionedLotsCount) {
		this.auctionedLotsCount = auctionedLotsCount;
	}
	
	/**
	 * M�todo que establece la lista de lotes que se encuentran en la lonja.
	 * @param lots Nueva lista de lotes.
	 */
	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}
}

/**
 * Clase que define el comportamiento para el PROTOCOLO-REGISTRO-VENDEDOR/COMPRADOR, PROTOCOLO-DEPOSITO,
 * PROTOCOLO-APERTURA-CREDITO y PROTOCOLO-RETIRADA-COMPRAS.
 * @author alejandroramon.lopezr@um.es
 */
class ReceiverAndManagerBehaviour extends OneShotBehaviour {
	
	FishMarketAgent a;
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 * @param agent Agente que realiza el comportamiento.
	 */
	public ReceiverAndManagerBehaviour(FishMarketAgent agent) {
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
				AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);
		
		// A�adir el comportamiento para recibir mensajes del protocolo de interacci�n FIPA-Request.
		a.addBehaviour(new AchieveREResponder(a, mt) {	
			
			/**
			 * M�todo que maneja la recepci�n de mensajes ACL de tipo REQUEST.
			 * @param request Mensaje recibido.
			 * @return Un mensaje de tipo AGREE o REFUSE para aceptar/rechazar la petici�n.
			 */
			@Override
			protected ACLMessage handleRequest (ACLMessage request) {
				ACLMessage agree = request.createReply();
				agree.setPerformative(ACLMessage.AGREE);
				try {
					Action ce = (Action) a.getContentManager().extractContent(request);
					// Peticiones de registro para el RRV y el RRC (PROTOCOLO-REGISTRO-VENDEDOR/COMPRADOR).
					if (ce.getAction() instanceof Register) {
						Register reg = (Register) ce.getAction();
						AID agent = ce.getActor();
						String type = reg.getType();
						
						// Si el comprador/vendedor no est� registrado se acepta la petici�n.
						if ((type.equals("buyer")) && (!a.getBuyers().contains(agent))) return agree;
						else if ((type.equals("seller")) && (!a.getSellers().contains(agent))) return agree;
					}
					// Peticiones de dep�sito para el RRV (PROTOCOLO-DEPOSITO).
					else if (ce.getAction() instanceof Deposit) {
						AID seller = ce.getActor();
						
						// Si el vendedor est� registrado se acepta la petici�n.
						if (a.getSellers().contains(seller)) return agree;
					}
					// Peticiones de apertura de l�nea para el RGC (PROTOCOLO-APERTURA-CREDITO).
					else if (ce.getAction() instanceof Open) {
						Open op = (Open) ce.getAction();
						AID buyer = ce.getActor();
						float balance = op.getBalance();
						
						// Si el comprador est� registrado y tiene saldo se acepta la petici�n.
						if ((a.getBuyers().contains(buyer)) &&
							(balance > 0)) return agree;
					}
					// Peticiones de retirada de compras para el RGC (PROTOCOLO-RETIRADA-COMPRAS).
					else if (ce.getAction() instanceof Withdraw) {
						AID buyer = ce.getActor();
						
						// Si el comprador est� registrado y existe alg�n lote en la lonja que ha sido comprado por este comprador
						// se acepta la petici�n.
						if (a.getBuyers().contains(buyer)) {
							for (Lot lot : a.getLots()) {
								if (lot.getBuyer() != null) {
									if (lot.getBuyer().equals(buyer)) return agree;
								}
							}
						}
					}
				} catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				}
				
				// Si no se cumple ning�n caso se rechaza la petici�n.
				ACLMessage refuse = request.createReply();
				refuse.setPerformative(ACLMessage.REFUSE);
				return refuse;
			}
			
			/**
			 * M�todo que realiza la acci�n requerida y devuelve un mensaje de tipo INFORM.
			 * @param request Petici�n recibida en el m�todo anterior.
			 * @param response Respuesta devuelta, en caso de ser un REFUSE no se ejecuta el m�todo.
			 * @return Un mensaje de tipo INFORM de confirmaci�n.
			 */
			@Override
			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
				ACLMessage informDone = request.createReply();
				informDone.setPerformative(ACLMessage.INFORM);
				try {
					Action ce = (Action) a.getContentManager().extractContent(request);
					// PROTOCOLO-REGISTRO-VENDEDOR/COMPRADOR del RRV y del RRC
					if (ce.getAction() instanceof Register) {
						Register reg = (Register) ce.getAction();
						AID agent = ce.getActor();
						String type = reg.getType();
						
						// Si la petici�n es del tipo del comprador se a�ade el AID del agente a la lista de compradores.
						if (type.equals("buyer")) {
							a.getBuyers().add(agent);
						}
						// En el caso de que sea del tipo del vendedor, se a�ade el agente a la lista de vendedores, se inicializan sus 
						// fondos a 0 y se pone su confirmaci�n a True por defecto.
						else if (type.equals("seller")) {
							a.getSellers().add(agent);
							a.getSellersFund().put(agent, (float) 0);
							a.getConfirmations().put(agent, true);
						}
						informDone.setContent("Register done");
					}
					// PROTOCOLO-DEPOSITO del RRV
					else if (ce.getAction() instanceof Deposit) {
						Deposit dep = (Deposit) ce.getAction();
						AID seller = ce.getActor();
						List<Lot> newLots = dep.getLots();
						
						// Para cada lote a depositar: Establecer el momento en el que registr�, su vendedor y su id, a�adirlo a la lista de lotes
						// de la lonja y aumentar el contador de lotes.
						String lotsStr = "";
						for (Lot lot : newLots) {
							lot.setRegisterTime(OntologyFactory.getSimTimeOntologyJSON(a.getSimTime()));
							lot.setSeller(seller);
							lot.setId(""+a.getLotsCount());
							a.getLots().add(lot);
							a.setLotsCount(a.getLotsCount()+1);
							lotsStr += "\n  -  "+lot;
						}

						a.getLogger().info("PROTOCOLO-DEPOSITO", "Depositando los lotes:" + lotsStr + "\ndel " + 
																	seller.getLocalName() + ".");
						informDone.setContent("Deposit done");	
					}
					// PROTOCOLO-APERTURA-CREDITO del RGC
					else if (ce.getAction() instanceof Open) {
						Open op = (Open) ce.getAction();
						AID buyer = ce.getActor();
						float balance = op.getBalance();
						
						// A�adir el saldo del comprador, equivalente a su cartera.
						a.getBuyersBalance().put(buyer, balance);						
						informDone.setContent("Open done");
					}
					// PROTOCOLO-RETIRADA-COMPRAS del RGC
					else if (ce.getAction() instanceof Withdraw) {
						Withdraw wd = (Withdraw) ce.getAction();
						AID buyer = ce.getActor();
						List<Lot> buyerLots = new LinkedList<Lot>();
						List<Lot> othersLots = new LinkedList<Lot>();
						
						// Crear dos nuevas listas de lotes, una para el comprador que realiz� la solicitud y otra para el resto de lotes.
						for (Lot lot : a.getLots()) {
							if (lot.getBuyer() != null) {
								if (lot.getBuyer().equals(buyer)) buyerLots.add(lot);
								else othersLots.add(lot);
							}
							else othersLots.add(lot);
						}
						// Colocar la lista de lotes a retirar en un objeto Withdraw y la de lotes restantes como nueva lista de la lonja.
						// Restar los lotes subastados ya que estos lotes van a desaparecer de la lonja.
						wd.setLots(buyerLots);
						a.setLots(othersLots);
						a.setAuctionedLotsCount(a.getAuctionedLotsCount() - buyerLots.size());
						Action actionOp = new Action(a.getAID(), wd);
						
						// Rellenar el mensaje INFORM con la acci�n Withdraw de la ontolog�a de la lonja.
						informDone.setLanguage(a.getCodec().getName());
						informDone.setOntology(a.getOntology().getName());
						a.getContentManager().fillContent(informDone, actionOp);
					}
				} catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				}
				return informDone;
			}
		});
	}
}

/**
 * Clase que define el comportamiento para el PROTOCOLO-SUBASTA.
 * @author alejandroramon.lopezr@um.es
 */
class AuctioneerBehaviour extends SimpleBehaviour {	

	FishMarketAgent a;
	int lane;
	boolean[] freeLanes;
	float currentPrice;				// Precio actual del lote subastado.
	AID winner;						// AID del agente comprador ganador.
	Lot auctionedLot;				// Lote en subasta.
	long timeMarkL;					// Marca de tiempo de latencia que indica si debemos esperar m�s tiempo o se puede continuar con otra subasta.
	
	/**
	 * Constructor de la clase. Inicializa sus atibutos.
	 * @param agent Agente que va a realizar el comportamiento.
	 * @param fl Registro de cintas libres.
	 * @param l Cinta en la que debe lanzarse el protocolo.
	 */
	public AuctioneerBehaviour(FishMarketAgent agent, boolean[] fl, int l) {
		super(agent);
		a = agent;
		lane = l;
		freeLanes = fl;
		winner = null;
		auctionedLot = null;
		timeMarkL = 0;
	}

	/**
	 * M�todo que implementa la funcionalidad del comportamiento.
	 */
	@Override
	public void action() {
		// Acceder en exclusi�n mutua a la lista de lotes para que no se produzcan errores de concurrencia al acceder varios hilos a la misma lista
		// y modificarla. 
		synchronized (a.getLots()) {
			// Si hay lotes sin subastar, la cinta est� libre y se ha cumplido el per�odo de latencia se 
			// ejecuta el protocolo.
			if ((a.getLots().size() - a.getAuctionedLotsCount() > 0) && (freeLanes[lane]) && (System.currentTimeMillis() >= timeMarkL)) {
				// Ocupar la cinta.
				freeLanes[lane] = false;
				
				// Buscar un lote a subastar.
				for (Lot lot : a.getLots()) {
					if (lot.getAuctioned() == false) {
						auctionedLot = lot;
						// Marcar el lote como subastado para que otra cinta no lo intente coger.
						auctionedLot.setAuctioned(true);
						a.setAuctionedLotsCount(a.getAuctionedLotsCount()+1);
						break;
					}
				}	
				// Inicializar el precio actual al precio de salida.
				currentPrice = auctionedLot.getStartingPrice();
				
				// Crear un mensaje de tipo CFP para iniciar la subasta.
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION);
				cfp.setLanguage(a.getCodec().getName());
				cfp.setOntology(a.getOntology().getName());
				// El timeout para recibir la respuesta lo marca la ventana de oportunidad.
				cfp.setReplyByDate(new Date(System.currentTimeMillis() + a.getConfig().getOpportunityWindow()));
				for (AID buyer : a.getBuyers()) {
					cfp.addReceiver(buyer);
				}
				
				// Crear un objeto Auction para representar la acci�n.
				Auction auc = new Auction();
				auc.setLot(auctionedLot);
				auc.setPrice(currentPrice);
				Action actionOp = new Action(a.getAID(), auc);
				
				// A�adir el comportamiento para comenzar un protocolo de interacci�n FIPA-IteratedContractNet.
				try {
					a.getContentManager().fillContent(cfp, actionOp);
					a.getLogger().info("SUBASTA", "Cinta " + lane + ": Se abre subasta de lote de "+ auctionedLot.getKind() +" por "+ currentPrice);
					a.addBehaviour(new ContractNetInitiator(myAgent, cfp) {
						
						/**
						 * M�todo que maneja la recepci�n de mensajes.
						 * @param responses Mensajes recibidos.
						 * @param acceptances Mensajes ACCEPT/REJECT_PROPOSAL a ser enviados.
						 */
						@Override
						protected void handleAllResponses(Vector responses, Vector acceptances) {
							
							// Recorrer el vector de respuestas.
							Enumeration e = responses.elements();
							while (e.hasMoreElements()) {
								ACLMessage msg = (ACLMessage) e.nextElement();
								
								if (msg.getPerformative() == ACLMessage.PROPOSE) {
									ACLMessage reply = msg.createReply();
									// Si el comprador que respondi� un PROPOSE tiene saldo para pagar el lote y
									// fue el primero en hacerlo se lleva el lote.
									if ((winner == null) && (a.getBuyersBalance().get(msg.getSender()) >= currentPrice)) { 
										winner = msg.getSender();
										reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
										
										// Establecer el comprador, el precio final y el momento de la venta en el lote.
										auctionedLot.setBuyer(winner);
										auctionedLot.setFinalPrice(currentPrice);
										auctionedLot.setSaleTime(OntologyFactory.getSimTimeOntologyJSON(a.getSimTime()));
									}
									// En cualquier otro caso se rechaza la propuesta.
									else {
										reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
									}
									
									// A�adir el mensaje resultante al vector de aceptaciones.
									acceptances.addElement(reply);
								}
							}
							
							// En caso de que no se encuentre ning�n ganador se baja el precio y empieza otra ronda.
							if (winner == null) {
								currentPrice -= Math.round(currentPrice*a.getConfig().getReductionRate() * 100) / 100;
								// Si el precio resultante est� por encima o es igual que el precio de reserva la subasta contin�a.
								if (currentPrice >= auctionedLot.getReservePrice()) {
									// Crear un nuevo vector de mensajes CFP.
									Vector<ACLMessage> cfps = new Vector<ACLMessage>();
									
									// Crear el nuevo CFP.
									ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
									cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION);
									cfp.setLanguage(a.getCodec().getName());
									cfp.setOntology(a.getOntology().getName());
									cfp.setReplyByDate(new Date(System.currentTimeMillis() + a.getConfig().getOpportunityWindow()));
									for (AID buyer : a.getBuyers()) {
										cfp.addReceiver(buyer);
									}
									
									// Crear un objeto Auction para representar la acci�n.
									Auction auc = new Auction();
									auc.setLot(auctionedLot);
									auc.setPrice(currentPrice);
									Action actionOp = new Action(a.getAID(), auc);
									
									// Rellenar el CFP y a�adirlo al vector.
									try {
										a.getContentManager().fillContent(cfp, actionOp);
										cfps.addElement(cfp);
									} catch (CodecException | OntologyException e1) {
										e1.printStackTrace();
									}
									
									// Comenzar una nueva iteraci�n.
									a.getLogger().info("SUBASTA", "Cinta " + lane + ": Lote de "+ auctionedLot.getKind() + 
																  " en subasta por "+ currentPrice);
									newIteration(cfps);
								}
								// En caso de alcanzar el precio de reserva se cancela la subasta, se desecha el lote y se libera la cinta.
								else {		
									freeLanes[lane] = true;
									a.getLogger().info("SUBASTA", "Cinta " + lane + ": Subasta cerrada, se ha alcanzado el precio de reserva (" 
																  + auctionedLot.getReservePrice() + ")" + "\nLote de " + auctionedLot.getKind() + " desechado.");
									a.getLots().remove(auctionedLot);
									a.setAuctionedLotsCount(a.getAuctionedLotsCount()-1);
									timeMarkL = a.getConfig().getLatency() + System.currentTimeMillis();
								}
							}
						}
						
						/**
						 * M�todo que maneja la recepci�n de mensajes de tipo INFORM. Se llama cuando el ganador confirma el �xito de la subasta.
						 * @param inform Mensaje recibido.
						 */
						@Override
						protected void handleInform(ACLMessage inform) {
							// Liberar la cinta.
							freeLanes[lane] = true;
							
							// Decrementar el saldo del ganador.
							float balance = a.getBuyersBalance().get(winner);
							a.getBuyersBalance().replace(winner, balance-currentPrice);
								
							// Aumentar los ingresos de la lonja en funci�n de la comisi�n y aumentar los fondos del vendedor.
							float commission = Math.round(currentPrice*a.getConfig().getCommission() * 100) / 100;
							float prevFund = a.getSellersFund().get(auctionedLot.getSeller());
							a.setIncome(a.getIncome() + commission);
							a.getSellersFund().replace(auctionedLot.getSeller(), prevFund + (currentPrice - commission));
							
							// Resetear el ganador y actualizar la marca de tiempo de latencia.
							a.getLogger().info("SUBASTA", "Cinta " + lane + ": Lote de "+ auctionedLot.getKind() +
									  " vendido a " + winner.getLocalName() + " por "+ currentPrice);
							winner = null;
							timeMarkL = a.getConfig().getLatency() + System.currentTimeMillis();
						}
					});
				} catch (CodecException | OntologyException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * M�todo que indica si el comportamiento debe salir o no de la cola de comportamientos activos del agente.
	 * @return True si debe acabar, False si no.
	 */
	@Override
	public boolean done() {
		// Si la simulaci�n llega al estado END el comportamiento debe acabar.
		if (a.getSimTime().getSimState().equals(SimTimeOntology.END)) return true;
		return false;
	}
}

/**
 * Clase que define el comportamiento para el PROTOCOLO-COBRO.
 * @author alejandroramon.lopezr@um.es
 */
class CollectInitiatorBehaviour extends SimpleBehaviour {
	
	FishMarketAgent a;
	int nud;					// N�mero de ticks al d�a.
	int nd;						// N�mero de d�as de la simulaci�n.
	int timeMark;				// Marca de tiempo que indica cuando ha pasado medio d�a de simulaci�n.
	
	/**
	 * Constructor de la clase. Inicializa sus atributos.
	 * @param agent Agente que realiza el comportamiento.
	 * @param numUnitDay N�mero de ticks por d�a.
	 * @param numDays N�mero de d�as de simulaci�n.
	 */
	public CollectInitiatorBehaviour(FishMarketAgent agent, int numUnitDay, int numDays) {
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
		// Si ha pasado medio d�a desde la �ltima petici�n, se vuelve a realizar la petici�n.
		// Adem�s si nos encontramos en el �ltimo tick antes de finalizar la simulaci�n realizaremos la petici�n 
		// continuamente para conseguir que los vendedores puedan cobrar subastas de �ltima hora.
		if ((a.getSimTime().getTime() == timeMark) || ((a.getSimTime().getTime() == nud-1) && (a.getSimTime().getDay() == nd-1))) {
			if (timeMark == nud/2) timeMark = nud-1;
			else timeMark = nud/2;
			
			// Para cada vendedor que tenga fondos y que haya respondido ya a la petici�n anterior (hay que asegurarse de esto para que
			// los vendedores no tengan muchas solicitudes y acaben contestando a alguna antigua, de este modo solo se le manda una solicitud
			// si respondi� a la anterior) se manda la petici�n de cobro.
			for (AID seller : a.getSellers()) {
				if ((a.getSellersFund().get(seller) > 0) && (a.getConfirmations().get(seller))) {
					// Poner la confirmaci�n a False.
					a.getConfirmations().replace(seller, false);
					
					// Crear mensaje de tipo REQUEST.
					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
					request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					request.addReceiver(seller);
					request.setLanguage(a.getCodec().getName());
					request.setOntology(a.getOntology().getName());
					
					// Crear un objeto Collect para representar la acci�n.
					Collect col = new Collect();
					col.setFund(a.getSellersFund().get(seller));
					Action actionOp = new Action(a.getAID(), col);
					
					// A�adir el comportamiento para comenzar un protocolo de interacci�n FIPA-Request.
					try {
						a.getContentManager().fillContent(request, actionOp);
						a.addBehaviour(new AchieveREInitiator(a, request) {
							
							/**
							 * M�todo que maneja la recepci�n de mensajes ACL de tipo REFUSE.
							 * @param refuse Mensaje recibido.
							 */
							@Override
							protected void handleRefuse(ACLMessage refuse) {
								// Si el vendedor contest� con un REFUSE se confirma su respuesta.
								a.getConfirmations().replace(seller, true);
							}
							
							/**
							 * M�todo que maneja la recepci�n de mensajes ACL de tipo INFORM. Se llama cuando el vendedor acepta el cobro.
							 * @param inform Mensaje recibido.
							 */
							@Override
							protected void handleInform(ACLMessage inform) {
								// Retirar los fondos del vendedor de la lonja y confirmar su respuesta.
								a.getSellersFund().replace(seller, (float) 0);
								a.getConfirmations().replace(seller, true);
								a.getLogger().info("PROTOCOLO-COBRO", "Fondos de "+seller.getLocalName()+" retirados de la lonja.");
							}
						});
					}
					catch (CodecException | OntologyException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * M�todo que indica cuando debe salir el comportamiento de la cola de comportamientos activos del agente.
	 * @return True si debe acabar, False si no.
	 */
	@Override
	public boolean done() {
		// Si la simulaci�n llega al estado END el comportamiento debe acabar.
		if (a.getSimTime().getSimState().equals(SimTimeOntology.END)) return true;
		return false;
	}
}
