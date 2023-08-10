import java.util.ArrayList;


public class Cart {

    private ArrayList<CartItem> items; //new ArrayList for Cartitem

    public Cart(){
        this.items = new ArrayList<CartItem>();
    }

    public ArrayList<CartItem> geCartList(){
        return items;
    
    }

    public void addCartItem(CartItem item){
    items.add(item);
    }

    public void printCart(){ //printcart when added
        int cartnumber = 1;
        for (CartItem i : items){
            System.out.println("Item #" +cartnumber+ " : " + i);
            cartnumber++;
        }
    }
    public void removeCartItem(CartItem item){ //to remove a product from a customer's cart
        items.remove(item);
    }
   
	
}