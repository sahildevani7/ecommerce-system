public class CartItem  {
    private Product product;
    private String productOptions;
    private Customer customer;
    private String cartnumber;

    public CartItem(String cartnumber, Product product, Customer customer, String productOptions) {
        this.product = product;
        this.productOptions = productOptions;
        this.customer = customer;
        this.cartnumber = cartnumber;
    }

    public String getcartnumber()//return cartnumber 
    { 
        return cartnumber;
    }

    public void setcartnumber(String cartnumber)
    {
        this.cartnumber = cartnumber;
    }

    public Product getProd() //return product
    {
        return product;
    }

    public void setProd(Product product)
    {
        this.product = product;
    }

    public Customer getCustomer()// return customer
	{
		return customer;
	}
	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}

    public String getProductOptions() //return productoptions
    {
        return productOptions;
    }

    public void setProductOptions(String productOptions) {
        this.productOptions = productOptions;
    }


	public void print() //print the cart details for customer
	{
		System.out.printf("\nCart # %3s Customer Id: %3s Product Id: %3s Product Name: %12s Options: %8s", cartnumber, customer.getId(), product.getId(), product.getName(), 
				productOptions);
	}

    
  
}