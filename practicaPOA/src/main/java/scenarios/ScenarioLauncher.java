package scenarios;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.FileHandler;

import org.yaml.snakeyaml.Yaml;

import agents.clock.ClockAgentConfig;
import utils.AgentLoggingHTMLFormatter;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;

/**
 * Clase que define el método main que lanza el SMA.
 * @author pablo y alejandroramon.lopezr@um.es
 */
public class ScenarioLauncher {
		
	public static void main(String[] args) throws SecurityException, IOException {
		if(args.length == 1) {
			// Cargar el escenario mediante el archivo YAML.
			String config_file = args[0];
			Yaml yaml = new Yaml();
			InputStream inputStream = new FileInputStream(config_file);
			ScenarioConfig scenario = yaml.load(inputStream);
			
			// Crear el handler de escritura común a todos los agentes.
			FileHandler myFileHandler = new FileHandler("logs/"+scenario.getName()+".html");
			myFileHandler.setFormatter(new AgentLoggingHTMLFormatter(scenario.getName()));
						
			System.out.println(scenario);
			try {

				// Obtener una instancia del entorno runtime de Jade.
				Runtime rt = Runtime.instance();
				// Terminar la máquina virtual si no hubiera ningún contenedor de agentes activo.
				rt.setCloseVM(true);
				// Lanzar una plataforma en el puerto 8888.
				// Y crear un profile de la plataforma a partir de la cual podemos
				// crear contenedores.
				Profile pMain = new ProfileImpl(null, 8888, null);
				System.out.println("Lanzamos una plataforma desde clase principal..."+pMain);
				
				// Crear el contenedor.
				AgentContainer mc = rt.createMainContainer(pMain);
				
				// Crear un RMA (la GUI de JADE).
				System.out.println("Lanzando el agente RMA en el contenedor main ...");
				AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
				rma.start();

				// INICIALIZACIÓN DE LOS AGENTES //
				
				// Reloj.
				ClockAgentConfig cc = scenario.getClock();
				Object[] arguments =  {cc.getUnitTimeMillis(), cc.getNumUnitDay(), cc.getNumSimDays(), myFileHandler};
				
				AgentController clock = mc.createNewAgent("clock", agents.clock.ClockAgent.class.getName(), arguments);
				clock.start();
				
				// Lonja.
				AgentRefConfig marketConfig = scenario.getFishMarket();
				System.out.println(marketConfig);
				Object[] marketConfigArg = {marketConfig.getConfig(), cc.getNumUnitDay(), cc.getNumSimDays(), myFileHandler};
				AgentController market = mc.createNewAgent(
						marketConfig.getName(), 
						agents.fishmarket.FishMarketAgent.class.getName(), 
						marketConfigArg);
				market.start();
				
				// Compradores.
				List<AgentRefConfig> buyers = scenario.getBuyers();
				for(AgentRefConfig buyer: buyers) {
					System.out.println(buyer);
					Object[] buyerConfigArg = {buyer.getConfig(), cc.getNumUnitDay(), cc.getNumSimDays(), myFileHandler};
					AgentController b = mc.createNewAgent(
							buyer.getName(), 
							agents.buyer.BuyerAgent.class.getName(), 
							buyerConfigArg);
					b.start();
				}
				
				// Vendedores.
				List<AgentRefConfig> sellers = scenario.getSellers();
				for(AgentRefConfig seller: sellers) {
					System.out.println(seller);
					Object[] sellerConfigArg = {seller.getConfig(), myFileHandler};
					AgentController s = mc.createNewAgent(
							seller.getName(), 
							agents.seller.SellerAgent.class.getName(), 
							sellerConfigArg);
					s.start();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
