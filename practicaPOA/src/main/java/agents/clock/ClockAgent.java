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
	 * Método que inicializa el agente.
	 */
	public void setup() {
		super.setup();
		
		Object[] args = getArguments();
		if (args != null && args.length == 4) {
			
			// Inicializar los atributos.
			int unitTimeMillis = (Integer) args[0];
			int numUnitDay = (Integer) args[1];
			int numSimDays = (Integer) args[2];
			
			// Añadir el handler de escritura común a todos los agentes.
			globalHandler = (FileHandler) args[3];
			getLogger().addFileHandler(globalHandler);
			getLogger().info("setup()", "setup (unitTimeMillis="+unitTimeMillis+", "+
												"numUnitDay="+numUnitDay+", "+
												"numSimDays="+numSimDays+")");
			
			// Añadir el comportamiento del reloj.
			ClockTickerBehaviour clockBehaviour = new ClockTickerBehaviour(this, unitTimeMillis, numUnitDay, numSimDays);
			addBehaviour(clockBehaviour);
			
			// Añadir el comportamiento de respuesta ante peticiones de suscripción.
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
	 * Método que mata al agente.
	 */
	@Override
	public void takeDown() {
		try {
			DFService.deregister(this);
			getLogger().info("takeDown", getLocalName() + " finalizando...");
			// Cerrar handler global cuando todos los agentes han terminado de escribir en él.
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
		private int unitTimeMillis; // Número de milisegundos que forman una unidad de tiempo
		private int numUnitDay; // Número de unidades que forman un día
		private int numSimDays; // Número de días que durará la simulación
		
		private int time = 0; // Contador de unidades de tiempo
		private int day = 0; // Contador de días
		
		private ClockAgent a;
		
		List<Subscription> subs = new ArrayList<Subscription>();
		
		/**
		 * Constructor de la clase. Inicializa sus atributos.
		 * @param agent Agente reloj que realiza el comportamiento.
		 * @param unitTimeMillis Milisegundos que dura cada tick de reloj.
		 * @param numUnitDay Número de ticks en un día.
		 * @param numSimDays Número de días de simulación.
		 */
		public ClockTickerBehaviour(ClockAgent agent, int unitTimeMillis, int numUnitDay, int numSimDays) {
			super(agent, unitTimeMillis);
			a = agent;
			this.unitTimeMillis = unitTimeMillis;
			this.numUnitDay = numUnitDay;
			this.numSimDays = numSimDays;
		}
		
		/**
		 * Método que implementa la funcionalidad del reloj en cada tick.
		 */
		@Override
		protected void onTick() {
			
			// Incrementar el número de ticks.
			time += 1;
			if (time >= numUnitDay) {
				// Nuevo día.
				time = 0;
				day += 1;
			}
			
			if(isSimEnd()) {
				// Notificar fin de simulación.
				a.getLogger().info("ClockTickerBehaviour", "Fin de la simulacion.");
				notifySubscriptors(true);
			} else {
				// Notificar a los agentes suscritos del día y la unidad de tiempo.
				a.getLogger().info("ClockTickerBehaviour","day="+day+", time="+time);
				notifySubscriptors(false);
			}
		}
		
		/**
		 * Método que devuelve si es el fin de la simulación o no.
		 * @return True si se ha acabado el último dia, False si no.
		 */
		public boolean isSimEnd() {
			return day >= numSimDays;
		}
		
		/**
		 * Método que notifica a los suscriptores del reloj del estado y tiempo de la simulación.
		 * @param end Booleano que indica si la simulación ha acabado.
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
		 * Método que elimina una suscripción de la lista de suscripciones.
		 * @param subscription La suscripción a eliminar.
		 * @return Si la operación tuvo éxito.
		 */
		@Override
		public boolean deregister(Subscription subscription) throws FailureException {
			synchronized (subs) {
				a.getLogger().info("ClockTickerBehaviour","deregister("+subscription.getMessage().getSender().getLocalName()+")");
				subs.remove(subscription);
				
				// Al final de la simulación el reloj debe esperar a que todos los agentes de desuscriban para poder
				// finalizar, de lo contrario el reloj podría acabar sin que muchos agentes supiesen que llegó el final de la simulación.
				if(subs.isEmpty() && isSimEnd()) {
					a.doDelete();
				}
			}
			return true;
		}

		/**
		 * Método que añade una suscripción a la lista de suscripciones.
		 * @param subscription La suscripción a añadir.
		 * @return Si la operación tuvo éxito.
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
