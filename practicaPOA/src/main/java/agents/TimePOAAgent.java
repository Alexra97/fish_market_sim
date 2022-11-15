package agents;

import agents.clock.SimTimeOntology;
import fishmarketOntology.FishmarketOntology;
import utils.OntologyFactory;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

/**
 * Clase que hereda de POAAgent y que realiza la suscripción del agente al agente reloj.
 * @author pablo
 */
public class TimePOAAgent  extends POAAgent {
	
	private SimTimeOntology simTime;
	private Codec codec = new SLCodec();
	private Ontology ontology = FishmarketOntology.getInstance();
	
	/**
	 * Método que inicializa el agente añadiéndole un comportamiento de suscriptor.
	 */
	public void setup() {
		super.setup(); 
		
		// Registrar el lenguaje y la instancia Singleton de la ontologia para que todos los agentes
		// que heredan esta clase se puedan entender entre sí.
		simTime = new SimTimeOntology(0,0);
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// Buscar al agente reloj en el DF.
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("simulated-time");
		template.addServices(sd);
		
		// Añadir el comportamiento de suscriptor.
		Behaviour clockDFSub =  new SubscriptionInitiator(
				this, DFService.createSubscriptionMessage(this, getDefaultDF(), template, null)) {
			protected void handleInform(ACLMessage inform) {
				try {
					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
					if(dfds.length > 0) {
						AID clockAgent = dfds[0].getName();
						ACLMessage request = new ACLMessage(ACLMessage.SUBSCRIBE);
						request.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);
						request.setReplyWith(""+System.currentTimeMillis());
						request.addReceiver(clockAgent);
						
						myAgent.addBehaviour(new TimerUpdaterBehaviour(getAgent(), request));
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		};
		
		addBehaviour(clockDFSub);
	}
	
	/**
	 * Clase que implementa el comportamiento de actualización de estado ante un tick.
	 * @author pablo
	 */
	private class TimerUpdaterBehaviour extends SubscriptionInitiator {
		
		/**
		 * Constructor de la clase, invoca al constructor del padre (SuscriptionInitiator).
		 * @param a Agente que realiza el comportamiento.
		 * @param msg Mensaje a enviar al servicio al que se desea suscribir el agente.
		 */
		public TimerUpdaterBehaviour(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		/**
		 * Método que maneja la recepción de un mensaje ACL de tipo INFORM.
		 */
		@Override
		protected void handleInform(ACLMessage inform) {
			String content = inform.getContent();
			SimTimeOntology sto = OntologyFactory.getSimTimeOntologyObject(content);
			
			simTime = sto;
			
			// Si el estado de la simulación ha llegado a END se envía un mensaje al reloj indicando que nos 
			// elimine de la lista de suscripciones. Tras esto el agente acaba.
			if(simTime.getSimState().equals(SimTimeOntology.END)) {
				ACLMessage cancel = inform.createReply();
				cancel.setPerformative(ACLMessage.CANCEL);
				getAgent().send(cancel);
				
				doDelete();
			}
		}
	}
	
	/**
	 * Método que retorna el estado de la simulación.
	 * @return Un objeto con el estado de simulación actual.
	 */
	public SimTimeOntology getSimTime() {
		return simTime;
	}
	
	/**
	 * Método que devuelve la instancia única de la ontología.
	 * @return La ontología.
	 */
	public Ontology getOntology() {
		return ontology;
	}
	
	/**
	 * Método que devuelve el codec del lenguaje SL.
	 * @return El codec SL.
	 */
	public Codec getCodec() {
		return codec;
	}
}
