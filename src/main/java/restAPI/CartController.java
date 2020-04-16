package restAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import exceptions.*;
import models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restAPI.DTO.Cart.CartDTO;
import restAPI.DTO.Error.ErrorDTO;
import restAPI.DTO.HandShakes.CartSuccessFulFinalize;
import restAPI.DTO.HandShakes.ChangeInCartSuccess;
import restAPI.DTO.Order.OrderDetailDTO;
import systemHandlers.DataHandler;
import systemHandlers.Services.UserServices;
import systemHandlers.SystemManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/users/{id}")
public class CartController {

    private JsonNodeFactory factory = JsonNodeFactory.instance;
    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value="/cart",method = RequestMethod.PUT)
    public ResponseEntity<Object> addToCart(
            @PathVariable(value = "id",required = true) String userId,
            @RequestBody (required = true) JsonNode payload)
    {
        JsonNode foodNameJson = payload.get("food");
        JsonNode restaurantIdJson = payload.get("restaurant");
        JsonNode specialFoodJson = payload.get("special");
        JsonNode numberOfOrders = payload.get("food_count");
        ObjectNode answerJson = factory.objectNode();
        if (restaurantIdJson == null || foodNameJson == null || specialFoodJson == null) {
            return new ResponseEntity<>(new ErrorDTO("bad request",40002), HttpStatus.BAD_REQUEST);
        } else {
            int count = 1;
            if(numberOfOrders!=null){
                count = numberOfOrders.asInt();
            }
            String foodName = foodNameJson.asText();
            String restaurantId = restaurantIdJson.asText();
            Boolean specialFood = specialFoodJson.asBoolean();
            try {
                Restaurant restaurant = SystemManager.getInstance().getRestaurantById(restaurantId);
                if (specialFood) {
                    SpecialFood food = restaurant.getSpecialFoodByName(foodName);
                    for(int i=0;i<count;i++){
                        SystemManager.getInstance().addToCart(food, SystemManager.getInstance().getUser());
                    }
                    food.setCount(food.getCount() - count);
                    return new ResponseEntity<>(new ChangeInCartSuccess(200,foodName,food.getCount()), HttpStatus.OK);
                } else {
                    NormalFood food = restaurant.getNormalFoodByName(foodName);
                    if (restaurant.getLocation().getDistance(SystemManager.getInstance().getUser().getLocation()) <= 170) {
                        for(int i=0;i<count;i++){
                            SystemManager.getInstance().addToCart(food, SystemManager.getInstance().getUser());
                        }
                        return new ResponseEntity<>(new ChangeInCartSuccess(200,foodName,Integer.MAX_VALUE), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new ErrorDTO("restaurant not in range",403), HttpStatus.FORBIDDEN);
                    }
                }
            } catch (FoodDoesntExistException e) {
                return new ResponseEntity<>(new ErrorDTO("food does not exist",40401), HttpStatus.NOT_FOUND);
            } catch (RestaurantDoesntExistException e) {
                return new ResponseEntity<>(new ErrorDTO("restaurant does not exist",40402), HttpStatus.NOT_FOUND);
            } catch (UnregisteredOrderException e) {
                return new ResponseEntity<>(new ErrorDTO("unregistered order",40004), HttpStatus.BAD_REQUEST);
            } catch (FoodCountIsNegativeException e) {
                return new ResponseEntity<>(new ErrorDTO("food count negative",40001), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @RequestMapping(value="/cart",method = RequestMethod.DELETE)
    public ResponseEntity<Object> removeFromCart(
            @PathVariable(value = "id",required = true) String userId,
            @RequestBody (required = true) JsonNode payload)
    {
        JsonNode foodNameJson = payload.get("food");
        JsonNode restaurantIdJson = payload.get("restaurant");
        JsonNode specialFoodJson = payload.get("special");
        ObjectNode answerJson = factory.objectNode();
        if (restaurantIdJson == null || foodNameJson == null || specialFoodJson == null) {
            return new ResponseEntity<>(new ErrorDTO("bad request",40002), HttpStatus.BAD_REQUEST);
        } else {
            try {
                String foodName = foodNameJson.asText();
                String restaurantId = restaurantIdJson.asText();
                boolean specialFood = specialFoodJson.asBoolean();
                Restaurant restaurant = SystemManager.getInstance().getRestaurantById(restaurantId);
                User user = SystemManager.getInstance().getUser();
                int count;
                if (specialFood) {
                    try {
                        SpecialFood food = restaurant.getSpecialFoodByName(foodName);
                        user.getCart().removeOrder(food);
                        food.setCount(food.getCount() + 1);
                        count = food.getCount();
                    } catch (FoodDoesntExistException e) {
                        NormalFood food = restaurant.getNormalFoodByName(foodName);
                        SpecialFood cartFood = (SpecialFood) user.getCart().removeOrder((food.changeToSpecialFood()));
                        count = cartFood.getCount();
                    }
                    return new ResponseEntity<>(new ChangeInCartSuccess(200, foodName, count), HttpStatus.OK);
                } else {
                    NormalFood food = restaurant.getNormalFoodByName(foodName);
                    if (restaurant.getLocation().getDistance(SystemManager.getInstance().getUser().getLocation()) <= 170) {
                        user.getCart().removeOrder(food);
                        return new ResponseEntity<>(new ChangeInCartSuccess(200,foodName,Integer.MAX_VALUE), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new ErrorDTO("restaurant not in range",403), HttpStatus.FORBIDDEN);
                    }
                }
            } catch (FoodDoesntExistException e) {
                return new ResponseEntity<>(new ErrorDTO("food does not exist",40401), HttpStatus.NOT_FOUND);
            } catch (RestaurantDoesntExistException e) {
                return new ResponseEntity<>(new ErrorDTO("restaurant does not exist",40402), HttpStatus.NOT_FOUND);
            } catch (FoodCountIsNegativeException e) {
                return new ResponseEntity<>(new ErrorDTO("food count negative",40001), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @RequestMapping(value="/cart",method = RequestMethod.POST)
    public ResponseEntity<Object> finalize(
            @PathVariable(value = "id",required = true) String userId)
    {
        try {
            OrderDetailDTO order = UserServices.getInstance().finalizeCart(userId);
            return new ResponseEntity<>(new CartSuccessFulFinalize(200,order),HttpStatus.OK);
        }
        catch (CartIsEmptyException e){
            return new ResponseEntity<>(new ErrorDTO("cart is empty",40001),HttpStatus.BAD_REQUEST);
        }
        catch (CreditIsNotEnoughException e){
            return new ResponseEntity<>(new ErrorDTO("credit not enough",40002),HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/cart",method = RequestMethod.GET)
    public ResponseEntity<Object> getCart(
            @PathVariable(value = "id",required = true) String userId)
    {
        CartDTO cart = UserServices.getInstance().getUserCart(userId);
        return new ResponseEntity<>(cart,HttpStatus.OK);
    }

}
