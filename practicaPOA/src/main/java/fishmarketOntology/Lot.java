package fishmarketOntology;

import jade.content.Concept;
import jade.core.AID;

/**
 * Clase que define la estructura del concepto Lot (Lote).
 * @author alejandroramon.lopezr@um.es
 */
public class Lot implements Concept {
	
	private int day, time;												// Momento de llegada del lote.
    private String id, kind;											// Identificador de lote y tipo de mercanc�a.
    private String registerTime, saleTime;								// Momento de registro y venta del lote.
    private AID buyer, seller;											// Comprador y vendedor del lote.
    private float kg;													// Peso del lote.
    private float reservePrice, startingPrice, finalPrice;				// Precio de reserva, salida y venta.
    private boolean auctioned = false;									// Flag activo si ha sido subastado.
        
    /**
     * M�todo que pasa la estructura del lote a cadena de caracteres.
     * @return La cadena equivalente.
     */
	@Override
	public String toString() {
		String str = "Lot-" + id + " [day= " + day + ", time=" + time + ", kind=" + kind + ", kg=" + kg +
								", reservePrice=" + reservePrice + ", startingPrice=" + startingPrice +
								", finalPrice=" + finalPrice + ", registerTime=" + registerTime +
								", saleTime=" + saleTime +", seller=";
		if (seller != null) str += seller.getLocalName() + ", buyer=";
		else str += seller + ", buyer=";
		if (buyer != null) str += buyer.getLocalName() + ", auctioned=" + auctioned + "]";
		else str += buyer + ", auctioned=" + auctioned + "]";
		return str;
	}

	/**
	 * M�todo que devuelve el d�a en el que llega el lote.
	 * @return El d�a de llegada del lote.
	 */
	public int getDay() {
		return day;
	}
	
	/**
	 * M�todo que devuelve la unidad de tiempo en la que llega el lote.
	 * @return La unidad de tiempo de llegada del lote.
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * M�todo que devuelve el identificador del lote.
	 * @return El identificador del lote.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * M�todo que devuelve el tipo de mercancia del lote.
	 * @return El tipo de pescado del lote.
	 */
	public String getKind() {
		return kind;
	}
	
	/**
	 * M�todo que devuelve el comprador del lote.
	 * @return El comprador del lote.
	 */
	public AID getBuyer() {
		return buyer;
	}
	
	/**
	 * M�todo que devuelve el vendedor del lote.
	 * @return El vendedor del lote.
	 */
	public AID getSeller() {
		return seller;
	}
	
	/**
	 * M�todo que devuelve el peso del lote.
	 * @return El peso del lote en kilogramos.
	 */
	public float getKg() {
		return kg;
	}
	
	/**
	 * M�todo que devuelve el precio de reserva del lote.
	 * @return El precio de reserva del lote.
	 */
	public float getReservePrice() {
		return reservePrice;
	}
	
	/**
	 * M�todo que devuelve el precio inicial del lote.
	 * @return El precio de salida del lote.
	 */
	public float getStartingPrice() {
		return startingPrice;
	}
	
	/**
	 * M�todo que devuelve el precio final del lote.
	 * @return El precio de venta del lote.
	 */
	public float getFinalPrice() {
		return finalPrice;
	}
	
	/**
	 * M�todo que devuelve el momento en el que se registr� el lote.
	 * @return El momento de registro del lote.
	 */
	public String getRegisterTime() {
		return registerTime;
	}
	
	/**
	 * M�todo que devuelve el instante en el que se vendi� el lote.
	 * @return El momento de venta del lote.
	 */
	public String getSaleTime() {
		return saleTime;
	}
	
	/**
	 * M�todo que devuelve si el lote ha sido subastado.
	 * @return True si el lote fue subastado, False si no.
	 */
	// El m�todo est�ndar para obtener un atributo booleano ser�a isAuctioned(), pero la clase Ontology.java 
	// solo permite m�todos get/set.
	public boolean getAuctioned() {
		return auctioned;
	}
	
	/**
	 * M�todo que establece el d�a en el que llega el lote.
	 * @param day D�a de llegada del lote.
	 */
	public void setDay(int day) {
		this.day = day;
	}
	
	/**
	 * M�todo que establece la unidad de tiempo en la que llega el lote.
	 * @param time Unidad de tiempo de llegada.
	 */
	public void setTime(int time) {
		this.time = time;
	}
	
	/**
	 * M�todo que establece el identificador del lote.
	 * @param id Identificador del lote.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * M�todo que establece el tipo de mercanc�a del lote.
	 * @param kind Tipo de pescado del lote.
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	/**
	 * M�todo que establece el comprador del lote.
	 * @param buyer Comprador del lote.
	 */
	public void setBuyer(AID buyer) {
		this.buyer = buyer;
	}
	
	/**
	 * M�todo que establece el vendedor del lote.
	 * @param seller Vendedor del lote.
	 */
	public void setSeller(AID seller) {
		this.seller = seller;
	}
	
	/**
	 * M�todo que establece el peso del lote.
	 * @param kg Peso del lote en kilogramos.
	 */
	public void setKg(float kg) {
		this.kg = kg;
	}
	
	/**
	 * M�todo que establece el precio de reserva del lote.
	 * @param reservePrice Precio de reserva.
	 */
	public void setReservePrice(float reservePrice) {
		this.reservePrice = reservePrice;
	}
	
	/**
	 * M�todo que establece el precio de salida del lote.
	 * @param startingPrice Precio de salida.
	 */
	public void setStartingPrice(float startingPrice) {
		this.startingPrice = startingPrice;
	}
	
	/**
	 * M�todo que establece el precio final del lote.
	 * @param finalPrice Precio de venta final.
	 */
	public void setFinalPrice(float finalPrice) {
		this.finalPrice = finalPrice;
	}
	
	/**
	 * M�todo que establece el momento en el que se registr� el lote.
	 * @param registerTime Momento de registro.
	 */
	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}
	
	/**
	 * M�todo que establece el momento en el que se vendi� el lote.
	 * @param saleTime Momento de venta.
	 */
	public void setSaleTime(String saleTime) {
		this.saleTime = saleTime;
	}
	
	/**
	 * M�todo que establece si el lote fue subastado o no.
	 * @param auctioned True si el lote fue subastado, False si no.
	 */
	public void setAuctioned(boolean auctioned) {
		this.auctioned = auctioned;
	}
}
