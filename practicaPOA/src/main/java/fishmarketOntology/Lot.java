package fishmarketOntology;

import jade.content.Concept;
import jade.core.AID;

/**
 * Clase que define la estructura del concepto Lot (Lote).
 * @author alejandroramon.lopezr@um.es
 */
public class Lot implements Concept {
	
	private int day, time;												// Momento de llegada del lote.
    private String id, kind;											// Identificador de lote y tipo de mercancía.
    private String registerTime, saleTime;								// Momento de registro y venta del lote.
    private AID buyer, seller;											// Comprador y vendedor del lote.
    private float kg;													// Peso del lote.
    private float reservePrice, startingPrice, finalPrice;				// Precio de reserva, salida y venta.
    private boolean auctioned = false;									// Flag activo si ha sido subastado.
        
    /**
     * Método que pasa la estructura del lote a cadena de caracteres.
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
	 * Método que devuelve el día en el que llega el lote.
	 * @return El día de llegada del lote.
	 */
	public int getDay() {
		return day;
	}
	
	/**
	 * Método que devuelve la unidad de tiempo en la que llega el lote.
	 * @return La unidad de tiempo de llegada del lote.
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Método que devuelve el identificador del lote.
	 * @return El identificador del lote.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Método que devuelve el tipo de mercancia del lote.
	 * @return El tipo de pescado del lote.
	 */
	public String getKind() {
		return kind;
	}
	
	/**
	 * Método que devuelve el comprador del lote.
	 * @return El comprador del lote.
	 */
	public AID getBuyer() {
		return buyer;
	}
	
	/**
	 * Método que devuelve el vendedor del lote.
	 * @return El vendedor del lote.
	 */
	public AID getSeller() {
		return seller;
	}
	
	/**
	 * Método que devuelve el peso del lote.
	 * @return El peso del lote en kilogramos.
	 */
	public float getKg() {
		return kg;
	}
	
	/**
	 * Método que devuelve el precio de reserva del lote.
	 * @return El precio de reserva del lote.
	 */
	public float getReservePrice() {
		return reservePrice;
	}
	
	/**
	 * Método que devuelve el precio inicial del lote.
	 * @return El precio de salida del lote.
	 */
	public float getStartingPrice() {
		return startingPrice;
	}
	
	/**
	 * Método que devuelve el precio final del lote.
	 * @return El precio de venta del lote.
	 */
	public float getFinalPrice() {
		return finalPrice;
	}
	
	/**
	 * Método que devuelve el momento en el que se registró el lote.
	 * @return El momento de registro del lote.
	 */
	public String getRegisterTime() {
		return registerTime;
	}
	
	/**
	 * Método que devuelve el instante en el que se vendió el lote.
	 * @return El momento de venta del lote.
	 */
	public String getSaleTime() {
		return saleTime;
	}
	
	/**
	 * Método que devuelve si el lote ha sido subastado.
	 * @return True si el lote fue subastado, False si no.
	 */
	// El método estándar para obtener un atributo booleano sería isAuctioned(), pero la clase Ontology.java 
	// solo permite métodos get/set.
	public boolean getAuctioned() {
		return auctioned;
	}
	
	/**
	 * Método que establece el día en el que llega el lote.
	 * @param day Día de llegada del lote.
	 */
	public void setDay(int day) {
		this.day = day;
	}
	
	/**
	 * Método que establece la unidad de tiempo en la que llega el lote.
	 * @param time Unidad de tiempo de llegada.
	 */
	public void setTime(int time) {
		this.time = time;
	}
	
	/**
	 * Método que establece el identificador del lote.
	 * @param id Identificador del lote.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Método que establece el tipo de mercancía del lote.
	 * @param kind Tipo de pescado del lote.
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	/**
	 * Método que establece el comprador del lote.
	 * @param buyer Comprador del lote.
	 */
	public void setBuyer(AID buyer) {
		this.buyer = buyer;
	}
	
	/**
	 * Método que establece el vendedor del lote.
	 * @param seller Vendedor del lote.
	 */
	public void setSeller(AID seller) {
		this.seller = seller;
	}
	
	/**
	 * Método que establece el peso del lote.
	 * @param kg Peso del lote en kilogramos.
	 */
	public void setKg(float kg) {
		this.kg = kg;
	}
	
	/**
	 * Método que establece el precio de reserva del lote.
	 * @param reservePrice Precio de reserva.
	 */
	public void setReservePrice(float reservePrice) {
		this.reservePrice = reservePrice;
	}
	
	/**
	 * Método que establece el precio de salida del lote.
	 * @param startingPrice Precio de salida.
	 */
	public void setStartingPrice(float startingPrice) {
		this.startingPrice = startingPrice;
	}
	
	/**
	 * Método que establece el precio final del lote.
	 * @param finalPrice Precio de venta final.
	 */
	public void setFinalPrice(float finalPrice) {
		this.finalPrice = finalPrice;
	}
	
	/**
	 * Método que establece el momento en el que se registró el lote.
	 * @param registerTime Momento de registro.
	 */
	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}
	
	/**
	 * Método que establece el momento en el que se vendió el lote.
	 * @param saleTime Momento de venta.
	 */
	public void setSaleTime(String saleTime) {
		this.saleTime = saleTime;
	}
	
	/**
	 * Método que establece si el lote fue subastado o no.
	 * @param auctioned True si el lote fue subastado, False si no.
	 */
	public void setAuctioned(boolean auctioned) {
		this.auctioned = auctioned;
	}
}
