package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
import java.io.Serializable;
public class OrderReceipt implements Serializable{

	// fields
	private int orderId;
	private String seller;
	private int customerId;
	private String bookTitle;
	private int price;
	private int issuedTick;
	private int orderTick;
	private int processTick;

	public void setIssuedTick(int issuedTick) {
		this.issuedTick = issuedTick;
	}

	public void setOrderTick(int orderTick) {
		this.orderTick = orderTick;
	}

	public void setProcessTick(int processTick) {
		this.processTick = processTick;
	}

	public OrderReceipt(String seller, int customerId, String bookTitle, int price, int issuedTick, int orderTick, int processTick) {
		this.seller = seller;
		this.customerId = customerId;
		this.bookTitle = bookTitle;
		this.price = price;
		this.issuedTick = issuedTick;
		this.orderTick = orderTick;
		this.processTick = processTick;
	}

	/**
	 * Retrieves the orderId of this receipt.
	 */
	public int getOrderId() {

		return orderId;
	}
	/**
	 * Retrieves the name of the selling service which handled the order.
	 */
	public String getSeller() {

		return seller;
	}
	/**
	 * Retrieves the ID of the customer to which this receipt is issued to.
	 * <p>
	 * @return the ID of the customer
	 */
	public int getCustomerId() {

		return customerId;
	}
	/**
	 * Retrieves the name of the book which was bought.
	 */
	public String getBookTitle() {

		return bookTitle;
	}
	/**
	 * Retrieves the price the customer paid for the book.
	 */
	public int getPrice() {

		return price;
	}
	/**
	 * Retrieves the tick in which this receipt was issued.
	 */
	public int getIssuedTick() {
		return issuedTick;
	}
	/**
	 * Retrieves the tick in which the customer sent the purchase request.
	 */
	public int getOrderTick() {

		return orderTick;
	}
	/**
	 * Retrieves the tick in which the treating selling service started
	 * processing the order.
	 */
	public int getProcessTick() {

		return processTick;
	}

	public String toString()
	{
		return(" seller:"+seller+" ,customer id: " +customerId+" ,BOOKNAME: " +bookTitle+", price: " +price+" ,iss tick: " +issuedTick +" ,order tick: "+ orderTick + ", process tick: "+ processTick);
	}
}