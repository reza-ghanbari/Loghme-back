package structures;

import exceptions.InvalidToJsonException;
import exceptions.UnregisteredOrderException;

public class User {
    private Cart _cart;
    private Location _location;

    public User(Location location) {
        this._cart = new Cart();
        this._location = location;
    }

    public void addToCart(String foodName, String restaurantName) throws UnregisteredOrderException {

    }

    public Cart getCart() {
        return _cart;
    }

    public Location getLocation(){
        return _location;
    }

    public String finalizeOrder() throws InvalidToJsonException {
        return null;
    }

}
