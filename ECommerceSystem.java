import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

/*
 * Models a simple ECommerce system. Keeps track of products for sale, registered customers, product orders and
 * orders that have been shipped to a customer
 */
public class ECommerceSystem
{
	// Creating treemap for products
	Map<String, Product> products = new TreeMap<String, Product>();
	ArrayList<Customer> customers = new ArrayList<Customer>();	

	ArrayList<ProductOrder> orders = new ArrayList<ProductOrder>();
	ArrayList<ProductOrder> shippedOrders = new ArrayList<ProductOrder>();

	ArrayList<CartItem> items = new ArrayList<CartItem>();

	// These variables are used to generate order numbers, customer id's, product id's 
	int orderNumber = 500;
	int customerId = 900;
	int productId = 700;
	int cartnumber = 400;

	// General variable used to store an error message when something is invalid (e.g. customer id does not exist)  
	String errMsg = null;

	// Random number generator
	Random random = new Random();

	public ECommerceSystem()
	{

		// File IO - Creating products
		
		File file = new File("products.txt");

		// Surrounding scanner method with try-catch
		try (Scanner in = new Scanner(file)) {
			String prodName = "";
			double prodPrice = 0.0;
			int prodStock = 0;
			String prodCategory = "";
			String prodId = "";

			// Variables specific to "BOOK" products
			String bookTitle = "";
			String author = "";
			int year = 0;
			int stockHardcover = 0;
			int stockPaperback = 0;
			String TitleAuthorYear = "";

			while (in.hasNextLine()) {

				prodCategory = in.nextLine();
				// Following if-block accounts for "BOOK" products
				if (prodCategory.equals("BOOKS")) {
					prodName = in.nextLine();
					prodPrice = Double.parseDouble(in.nextLine());
					// Splitting next line by space to account for paperback and hardcover stock
					String [] stockSplit = in.nextLine().split(" ");
					stockPaperback = Integer.parseInt(stockSplit[0]);
					stockHardcover = Integer.parseInt(stockSplit[1]);
					TitleAuthorYear = in.nextLine();
					// Splitting next line by ":" to accoutn for title, author and year
					String [] splitBook = TitleAuthorYear.split(":");
					bookTitle = splitBook[0];
					author = splitBook[1];
					year = Integer.parseInt(splitBook[2]);

					prodId = generateProductId();
					// Adding all variables into the treemap
					products.put(prodId, new Book(prodName,prodId, prodPrice, stockPaperback, stockHardcover, bookTitle, author, year));
				}
				
				// Following else-if block accounts for NON-BOOK products
				else if (!prodCategory.isEmpty()) {
					prodName = in.nextLine();
					prodPrice = in.nextDouble();
					prodStock = in.nextInt();
	
					prodId = generateProductId();
					// Adding all variables into the treemap
					products.put(prodId, new Product(prodName, prodId, prodPrice, prodStock, Product.Category.valueOf(prodCategory)));
				}
			}
		// Catching IOException	
		} catch (IOException e) {
			System.out.println(e);
			System.exit(1);
		}
		// Create some customers
		customers.add(new Customer(generateCustomerId(),"Inigo Montoya", "1 SwordMaker Lane, Florin"));
		customers.add(new Customer(generateCustomerId(),"Prince Humperdinck", "The Castle, Florin"));
		customers.add(new Customer(generateCustomerId(),"Andy Dufresne", "Shawshank Prison, Maine"));
		customers.add(new Customer(generateCustomerId(),"Ferris Bueller", "4160 Country Club Drive, Long Beach"));
	}

	private String genaratecarnumber(){
		return "" + cartnumber++;
	}

	private String generateOrderNumber()
	{
		return "" + orderNumber++;
	}

	private String generateCustomerId()
	{
		return "" + customerId++;
	}

	private String generateProductId()
	{
		return "" + productId++;
	}

	public String getErrorMessage()
	{
		return errMsg;
	}

	public void printAllProducts()
	{
		// Iterating through the treemap values and printing them
		for (Map.Entry<String, Product> entry : products.entrySet()) {
			entry.getValue().print();
	}	
	}

	public void printAllBooks()
	{
		// Iterating through the treemap values
		for (Map.Entry<String, Product> entry : products.entrySet()) {
			if (entry.getValue().getCategory() == Product.Category.BOOKS) {
				entry.getValue().print();
			}
		}
	}

	public ArrayList<Book> booksByAuthor(String author) throws UnknownAuthorException
	{
		// Stores books by corresponding authors
		ArrayList<Book> books = new ArrayList<Book>();

		// Iterating through the treemap values
		for (Map.Entry<String, Product> entry : products.entrySet())
		{
			if (entry.getValue().getCategory() == Product.Category.BOOKS)
			{
				// Adding the corresponding author value to the arraylist "books"
				Book book = (Book) entry.getValue();
				if (book.getAuthor().equals(author))
					books.add(book);
			}
		}
		return books;
	}

	public void printAllOrders()
	{
		for (ProductOrder o : orders)
			o.print();
	}

	public void printAllShippedOrders()
	{
		for (ProductOrder o : shippedOrders)
			o.print();
	}

	public void printCustomers()
	{
		for (Customer c : customers)
			c.print();
	}
	/*
	 * Given a customer id, print all the current orders and shipped orders for them (if any)
	 */
	public boolean printOrderHistory(String customerId) throws UnknownCustomerException
	{
		// Make sure customer exists
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Unknown Customer Id");
		}	
		System.out.println("Current Orders of Customer " + customerId);
		for (ProductOrder order: orders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
		System.out.println("\nShipped Orders of Customer " + customerId);
		for (ProductOrder order: shippedOrders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
		return true;
	}


	public String orderProduct(String productId, String customerId, String productOptions) throws UnknownCustomerException, UnknwonProductException, InvalidProductOptionException, ProductOutOfStockException
	{
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Unknown Customer Id");
		}
		Customer customer = customers.get(index);

		// Get product by converting treemap values to a temp arraylist
		ArrayList<Product> temp = new ArrayList<Product>();
		for (Map.Entry<String, Product> entry : products.entrySet())
		{
			// Adding treemap values (product obj) to the "temp" arraylist
			Product p = (Product) entry.getValue();
			temp.add(p);
		} 

		index = temp.indexOf(new Product(productId));
		if (index == -1)
		{
			throw new UnknwonProductException("Unknown Product Id");
		}
		Product product = temp.get(index);

		// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
		if (!product.validOptions(productOptions))
		{
			// errMsg = "Product " + product.getName() + " ProductId " + productId + " Invalid Options: " + productOptions;
			// return null;
			throw new InvalidProductOptionException("Invalid Options: " + productOptions);
		}
		// Is it in stock?
		if (product.getStockCount(productOptions) == 0)
		{
			// errMsg = "Product " + product.getName() + " ProductId " + productId + " Out of Stock";
			// return null;
			throw new ProductOutOfStockException("Out of Stock");
		}
		// Create a ProductOrder
		ProductOrder order = new ProductOrder(generateOrderNumber(), product, customer, productOptions);
		product.reduceStockCount(productOptions);

		// Add to orders and return
		orders.add(order);

		return order.getOrderNumber();
	}


	/*
	 * Create a new Customer object and add it to the list of customers
	 */

	public boolean createCustomer(String name, String address) throws InvalidCustomerNameException, InvalidCustomerAddressException
	{
		// Check to ensure name is valid
		if (name == null || name.equals(""))
		{
			throw new InvalidCustomerNameException("Invalid Customer Name");
		}
		// Check to ensure address is valid
		if (address == null || address.equals(""))
		{
			throw new InvalidCustomerAddressException("Invalid Customer Address");
		}
		// Adding customers to the arraylist
		Customer customer = new Customer(generateCustomerId(), name, address);
		customers.add(customer);
		return true;
	}

	public ProductOrder shipOrder(String orderNumber) throws InvalidOrderNumberException
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			throw new InvalidOrderNumberException("Invalid Order Number");
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
		shippedOrders.add(order);
		return order;
	}

	/*
	 * Cancel a specific order based on order number
	 */
	public boolean cancelOrder(String orderNumber) throws InvalidOrderNumberException
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			throw new InvalidOrderNumberException("Invalid order number");
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
		return true;
	}

	// CART METHODS

	public String addtocart(String productId, String customerId, String productOptions) throws UnknownCustomerException, UnknwonProductException, InvalidProductOptionException, ProductOutOfStockException
	{
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Unknown Customer Id");
		}
		Customer customer = customers.get(index);

		// Get product by converting treemap values to a temp arraylist
		ArrayList<Product> temp = new ArrayList<Product>();
		for (Map.Entry<String, Product> entry : products.entrySet())
		{
			// Adding treemap values (product obj) to the "temp" arraylist
			Product p = (Product) entry.getValue();
			temp.add(p);
		} 
		// Checking if product exists 
		index = temp.indexOf(new Product(productId));
		if (index == -1)
		{
			throw new UnknwonProductException("Unknown Product Id");
		}
		Product product = temp.get(index);

		// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
		if (productOptions.equals("Book")){
			if (!product.validOptions(productOptions)){
		
			throw new InvalidProductOptionException("Invalid options: " + productOptions);
			}
		}
		
		// Is it in stock?
		if (product.getStockCount(productOptions) == 0)
		{
			throw new ProductOutOfStockException("Out of Stock");
		}
		// Create a Cartitem
		CartItem item = new CartItem(genaratecarnumber(), product, customer, productOptions);
		product.reduceStockCount(productOptions);

		// Add to orders and return
		items.add(item);

		return item.getcartnumber();
	}

	// Print cart
	public boolean printcart(String customerId) throws UnknownCustomerException
	{
		// Make sure customer exists
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Unknown Customer Id");
		}	
	
		for (CartItem item: items)
		{
			// Printing items with accordance to customerIds
			if (item.getCustomer().getId().equals(customerId))
				item.print();
		}
	
		return true;
	}

	// Order cart
	public boolean orderItems(String customerId) throws UnknownCustomerException
	{
		// Make sure customer exists
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Unknown Customer Id");
		}	
		
		CartItem item = items.get(index);
		items.remove(index);
	
		return true;
	}

	// Remove cart item
	public CartItem removItem(String productId, String customerId) throws UnknownCustomerException, UnknwonProductException
	{

		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Unknown Customer Id");
		}
		Customer customer = customers.get(index);

		// Get product by converting treemap values to a temp arraylist
		ArrayList<Product> temp = new ArrayList<Product>();
		for (Map.Entry<String,Product> entry : products.entrySet())
		{
			// Adding treemap values (product obj) to "temp" arraylist
			Product p = (Product) entry.getValue();
			temp.add(p);

		} 

		// Removing items with accordance to productId
		Product product = temp.get(index);
		CartItem item = items.get(index);
		items.remove(index);
		return item;
	}

	// Sort products by increasing price
	public void printByPrice()
	{
		ArrayList<Product> temp = new ArrayList<Product>();

		// Iterating through treemap values
		for (Map.Entry<String, Product> entry : products.entrySet())
		{
			// Adding treemap values to temp arraylist 
			Product p = (Product) entry.getValue();;
			temp.add(p);
		} 

		// Sorting with accordance to comparebyPrice method in Product class
		Collections.sort(temp, Product :: comparebyPrice);
		System.out.println(temp);
	}

	// Sort products alphabetically by product name
	public void printByName()
	{
		ArrayList<Product> temp = new ArrayList<Product>();

		// Iterating through treemap values
		for (Map.Entry<String, Product> entry : products.entrySet())
		{
			// Adding treemap values to temp arraylist
			Product p = (Product) entry.getValue();
			temp.add(p);
		} 

		// Sorting with accordance to compareTo method in Product class
		Collections.sort(temp, Product :: compareTo);
		System.out.println(temp);
	}


	// Sort products alphabetically by product name
	public void sortCustomersByName()
	{
		Collections.sort(customers);
		// System.out.println(customers);
	}
}

// Creating custom exception classes by extending RuntimeException 

class UnknownCustomerException extends RuntimeException 
{
	UnknownCustomerException(String message) {
		super(message);
	}
}

class UnknwonProductException extends RuntimeException
{
	UnknwonProductException(String message) {
		super(message);
	}
}

class InvalidProductOptionException extends RuntimeException
{
	InvalidProductOptionException(String message) {
		super(message);
	}
}

class ProductOutOfStockException extends RuntimeException
{
	ProductOutOfStockException(String message) {
		super(message);
	}
}

class InvalidCustomerNameException extends RuntimeException
{
	InvalidCustomerNameException(String message) {
		super(message);
	}
}

class InvalidCustomerAddressException extends RuntimeException
{
	InvalidCustomerAddressException(String message) {
		super(message);
	}
}

class InvalidOrderNumberException extends RuntimeException
{
	InvalidOrderNumberException(String message) {
		super(message);
	}
}

class UnknownAuthorException extends RuntimeException
{
	UnknownAuthorException(String message) {
		super(message);
	}
}