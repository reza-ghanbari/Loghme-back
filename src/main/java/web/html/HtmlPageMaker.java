package web.html;

import structures.OrderItem;
import structures.Restaurant;
import structures.User;

import java.util.ArrayList;
import java.util.List;

public class HtmlPageMaker {

    public String makeAllRestaurantsPage(ArrayList<Restaurant> restaurants){
        return null;
    }

    public String makeRestaurantPage(Restaurant restaurant){
        return null;
    }

    public String makeProfilePage(User user,boolean negCredit,boolean successFullAddCredit){
        return null;
    }

    public String makeCartPage(User user){
        return null;
    }

    public String makeRestaurantNotFoundPage(String restaurantId){
        return null;
    }

    public String makeInvalidRestaurantAccessPage(String restaurantId){
        return null;
    }

    public String makeNotEnoughCreditPage(User user){
        return null;
    }

    public String makeCartEmptyErrorPage(){
        return null;
    }

    public String makeMultipleRestaurantAddToCartErrorPage(User user){
        return null;
    }

    public String makeInvalidRequestPage(String address){return null;}

    public String makeOrderFinalizedPage(ArrayList<OrderItem> orderItems,User user){return null;}

    public String makeFoodNotFoundPage(String foodName,String restaurantName,String restaurantId){return null;}

}
