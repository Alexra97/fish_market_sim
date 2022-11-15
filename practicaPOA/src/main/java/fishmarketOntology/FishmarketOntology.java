package fishmarketOntology;

import jade.content.onto.*;
import jade.content.schema.*;

/**
 * Clase que define la ontología de la lonja. Los agentes usarán esta ontología para expresar sus intenciones a los demás.
 * @author alejandroramon.lopezr@um.es
 */
public class FishmarketOntology extends Ontology {
	
	public static final String ONTOLOGY_NAME = "fishmarket-ontology";

	// VOCABULARIO:
	//   Concepto Lote.
	public static final String LOT = "Lot";
	public static final String LOT_DAY = "day";
	public static final String LOT_TIME = "time";
	public static final String LOT_ID = "id";
	public static final String LOT_KIND = "kind";
	public static final String LOT_BUYER = "buyer";
	public static final String LOT_SELLER = "seller";
	public static final String LOT_KG = "kg";
	public static final String LOT_RESERVE_PRICE = "reservePrice";
	public static final String LOT_STARTING_PRICE = "startingPrice";
	public static final String LOT_FINAL_PRICE = "finalPrice";
	public static final String LOT_REGISTER_TIME = "registerTime";
	public static final String LOT_SALE_TIME = "saleTime";
	public static final String LOT_AUCTIONED = "auctioned";
	
	//  Acción registrar agente.
	public static final String REGISTER = "Register";
	public static final String REGISTER_TYPE = "type";
	
	//  Acción depositar lotes.
	public static final String DEPOSIT = "Deposit";
	public static final String DEPOSIT_LOTS = "lots";
	
	//  Acción abrir crédito.
	public static final String OPEN = "Open";
	public static final String OPEN_BALANCE = "balance";
	
	//  Acción retirar compra.
	public static final String WITHDRAW = "Withdraw";
	public static final String WITHDRAW_LOTS = "lots";
	
	//  Acción cobrar ventas.
	public static final String COLLECT = "Collect";
	public static final String COLLECT_FUND = "fund";
	 
	//  Acción subastar lote.
	public static final String AUCTION = "Auction";
	public static final String AUCTION_LOT = "lot";
	public static final String AUCTION_PRICE = "price";
	 
	// Única instancia (Singleton).
	private static Ontology singleInstance = new FishmarketOntology();
	
	/**
	 * Método que devuelve la única instancia de la ontología.
	 * @return La instancia única de la ontología.
	 */
	public static Ontology getInstance() {
		return singleInstance;
	}
		
	/**
	 * Constructor de la clase. Llama al constructor de la clase Ontology y añade los atributos a sus
	 * correspondientes acciones y conceptos de la ontología.
	 */
	private FishmarketOntology() {
		super(ONTOLOGY_NAME, BasicOntology.getInstance(), new CFReflectiveIntrospector());
		
		try {
			// Registrar conceptos y acciones de la ontología.
			add(new ConceptSchema(LOT), Lot.class);
			add(new AgentActionSchema(REGISTER), Register.class); 
			add(new AgentActionSchema(DEPOSIT), Deposit.class);
			add(new AgentActionSchema(OPEN), Open.class);
			add(new AgentActionSchema(WITHDRAW), Withdraw.class);
			add(new AgentActionSchema(COLLECT), Collect.class);
			add(new AgentActionSchema(AUCTION), Auction.class);
			
			// Estructura para el concepto Lot. Nombre del atributo/Tipo/Opcionalidad.
			ConceptSchema cs = (ConceptSchema) getSchema(LOT);
			cs.add(LOT_DAY, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
			cs.add(LOT_TIME, (PrimitiveSchema) getSchema(BasicOntology.INTEGER)); 
			cs.add(LOT_ID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
			cs.add(LOT_KIND, (PrimitiveSchema) getSchema(BasicOntology.STRING));
			cs.add(LOT_BUYER, (ConceptSchema) getSchema(BasicOntology.AID), ObjectSchema.OPTIONAL);
			cs.add(LOT_SELLER, (ConceptSchema) getSchema(BasicOntology.AID), ObjectSchema.OPTIONAL);
			cs.add(LOT_KG, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
			cs.add(LOT_RESERVE_PRICE, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
			cs.add(LOT_STARTING_PRICE, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
			cs.add(LOT_FINAL_PRICE, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.OPTIONAL);
			cs.add(LOT_REGISTER_TIME, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
			cs.add(LOT_SALE_TIME, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
			cs.add(LOT_AUCTIONED, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN));
			
			// Estructura para la acción Register. Nombre del atributo/Tipo/Cardinalidad mínima/Cardinalidad máxima.
			AgentActionSchema as = (AgentActionSchema) getSchema(REGISTER);
			as.add(REGISTER_TYPE, (PrimitiveSchema) getSchema(BasicOntology.STRING));

			// Estructura para la acción Deposit.
			as = (AgentActionSchema) getSchema(DEPOSIT);
			as.add(DEPOSIT_LOTS, (ConceptSchema) getSchema(LOT), 1, ObjectSchema.UNLIMITED);
			
			// Estructura para la acción Open.
			as = (AgentActionSchema) getSchema(OPEN);
			as.add(OPEN_BALANCE, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
			
			// Estructura para la acción Withdraw.
			as = (AgentActionSchema) getSchema(WITHDRAW);
			as.add(WITHDRAW_LOTS, (ConceptSchema) getSchema(LOT), 0, ObjectSchema.UNLIMITED);
			
			// Estructura para la acción Collect.
			as = (AgentActionSchema) getSchema(COLLECT);
			as.add(COLLECT_FUND, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
			
			// Estructura para la acción Auction.
			as = (AgentActionSchema) getSchema(AUCTION);
			as.add(AUCTION_LOT, (ConceptSchema) getSchema(LOT));
			as.add(AUCTION_PRICE, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));	
		}
		catch (OntologyException oe) {
			oe.printStackTrace();
		}
	}
} 