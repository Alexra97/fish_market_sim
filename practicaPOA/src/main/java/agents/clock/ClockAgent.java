package agents.clock;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

import agents.POAAgent;
import utils.OntologyFactory;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import jade.proto.SubscriptionResponder.Subscription;
import jade.proto.SubscriptionResponder.SubscriptionManager;

/**
 * Clase que define la funcionalidad del reloj.
 * @author pablo
 */
public class ClockAgent extends POAAgent {
	
	private FileHandler globalHandler;
	
	/**
	 * M�todo que inicializa el agente.
	 */
	public void setup() {
		super.setup();
		
		Object[] args = getArguments();
		if (args != null && args.length == 4) {
			
			// Inicializar los atributos.
			int unitTimeMillis = (Integer) args[0];
			int numUnitDay = (Integer) args[1];
			int numSimDays = (Integer) args[2];
			
			// A�adir el handler de escritura com�n a todos los agentes.
			globalHandler = (FileHandler) args[3];
			getLogger().addFileHandler(globalHandler);
			getLogger().info("setup()", "setup (unitTimeMillis="+unitTimeMillis+", "+
												"numUnitDay="+numUnitDay+", "+
												"numSimDays="+numSimDays+")");
			
			// A�adir el comportamiento del reloj.
			ClockTickerBehaviour clockBehaviour = new ClockTickerBehaviour(this, unitTimeMillis, numUnitDay, numSimDays);
			addBehaviour(clockBehaviour);
			
			// A�adir el comportamiento de respuesta ante peticiones de suscripci�n.
			MessageTemplate mt = SubscriptionResponder.createMessageTemplate(ACLMessage.SUBSCRIBE);
			addBehaviour(new SubscriptionResponder(this, mt, clockBehaviour));

			// Registrar al reloj en el DF.
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("simulated-time");
			sd.setName("Lonja-Simulated-Time");
			dfd.addServices(sd);
			try {
				DFService.register(this, dfd);
			} catch(FIPAException fe) {
				fe.printStackTrace();
			}
			
		} else {
			System.out.println("Son necesarios 3 argumentos (<unitTimeMillis>,<numUnitDay>,<numSimDays>)");
			doDelete();
		}
	}
	
	/**
	 * M�todo que mata al agente.
	 */
	@Override
	public void takeDown() {
		try {
			DFService.deregister(this);
			getLogger().info("takeDown", getLocalName() + " finalizando...");
			// Cerrar handler global cuando todos los agentes han terminado de escribir en �l.
			getLogger().close(globalHandler);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		super.takeDown();
	}
	
	/**
	 * Comportamiento que simula el paso del tiempo y notifica a los agentes que se hayan suscrito.
	 * @author pablo
	 */
	private class ClockTickerBehaviour extends TickerBehaviour implements SubscriptionManager {
		private int unitTimeMillis; // N�mero de milisegundos que forman una unidad de tiempo
		private int numUnitDay; // N�mero de unidades que forman un d�a
		private int numSimDays; // N�mero de d�as que durar� la simulaci�n
		
		private int time = 0; // Contador de unidades de tiempo
		private int day = 0; // Contador de d�as
		
		private ClockAgent a;
		
		List<Subscription> subs = new ArrayList<Subscription>();
		
		/**
		 * Constructor de la clase. Inicializa sus atributos.
		 * @param agent Agente reloj que realiza el comportamiento.
		 * @param unitTimeMillis Milisegundos que dura cada tick de reloj.
		 * @param numUnitDay N�mero de ticks en un d�a.
		 * @param numSimDays N�mero de d�as de simulaci�n.
		 */
		public ClockTickerBehaviour(ClockAgent agent, int unitTimeMillis, int numUnitDay, int numSimDays) {
			super(agent, unitTimeMillis);
			a = agent;
			this.unitTimeMillis = unitTimeMillis;
			this.numUnitDay = numUnitDay;
			this.numSimDays = numSimDays;
		}
		
		/**
		 * M�todo que implementa la funcionalidad del reloj en cada tick.
		 */
		@Override
		protected void onTick() {
			
			// Incrementar el n�mero de ticks.
			time += 1;
			if (time >= numUnitDay) {
				// Nuevo d�a.
				time = 0;
				day += 1;
			}
			
			if(isSimEnd()) {
				// Notificar fin de simulaci�n.
				a.getLogger().info("ClockTickerBehaviour", "Fin de la simulacion.");
				notifySubscriptors(true);
			} else {
				// Notificar a los agentes suscritos del d�a y la unidad de tiempo.
				a.getLogger().info("ClockTickerBehaviour","day="+day+", time="+time);
				notifySubscriptors(false);
			}
		}
		
		/**
		 * M�todo que devuelve si es el fin de la simulaci�n o no.
		 * @return True si se ha acabado el �ltimo dia, False si no.
		 */
		public boolean isSimEnd() {
			return day >= numSimDays;
		}
		
		/**
		 * M�todo que notifica a los suscriptores del reloj del estado y tiempo de la simulaci�n.
		 * @param end Booleano que indica si la simulaci�n ha acabado.
		 */
		private void notifySubscriptors(boolean end) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			SimTimeOntology obj = new SimTimeOntology(day, time);
			if(end) {
				obj.setSimState(SimTimeOntology.END);
			}
			String content = OntologyFactory.getSimTimeOntologyJSON(obj);
			msg.setContent(content);
			for(Subscription subscription: subs) {
				subscription.notify(msg);
			}
		}

		/**
		 * M�todo que elimina una suscripci�n de la lista de suscripciones.
		 * @param subscription La suscripci�n a eliminar.
		 * @return Si la operaci�n tuvo �xito.
		 */
		@Override
		public boolean deregister(Subscription subscription) throws FailureException {
			synchronized (subs) {
				a.getLogger().info("ClockTickerBehaviour","deregister("+subscription.getMessage().getSender().getLocalName()+")");
				subs.remove(subscription);
				
				// Al final de la simulaci�n el reloj debe esperar a que todos los agentes de desuscriban para poder
				// finalizar, de lo contrario el reloj podr�a acabar sin que muchos agentes supiesen que lleg� el final de la simulaci�n.
				if(subs.isEmpty() && isSimEnd()) {
					a.doDelete();
				}
			}
			return true;
		}

		/**
		 * M�todo que a�ade una suscripci�n a la lista de suscripciones.
		 * @param subscription La suscripci�n a a�adir.
		 * @return Si la operaci�n tuvo �xito.
		 */
		@Override
		public boolean register(Subscription subscription) throws RefuseException, NotUnderstoodException {
			synchronized (subs) {
				getLogger().info("ClockTickerBehaviour","register("+subscription.getMessage().getSender().getLocalName()+")");
				subs.add(subscription);
			}
			return true;
		}
	}
}
