package restAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import exceptions.FoodDoesntExistException;
import exceptions.InvalidToJsonException;
import models.Restaurant;
import models.SpecialFood;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restAPI.DTO.Restaurant.SpecialFoodDTO;
import systemHandlers.DataHandler;
import systemHandlers.Services.RestaurantManager;
import systemHandlers.SystemManager;

import java.util.ArrayList;
import java.util.Date;

@RestController
public class FoodPartyController {

    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    private ObjectNode generateError(JsonNodeFactory factory, int status, String description) {
        ObjectNode errorNode = factory.objectNode();
        errorNode.put("status", status);
        errorNode.put("description", description);
        return errorNode;
    }

    @RequestMapping(value = "/foodParty",method = RequestMethod.GET)
    public ResponseEntity<Object> getAllFoods() {
        ArrayList<SpecialFoodDTO> foods = RestaurantManager.getInstance().getAllSpecialFoods();
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    @RequestMapping(value = "/foodParty/time", method = RequestMethod.GET)
    public ResponseEntity<Object> getFoodPartyTime() {
        ObjectNode node = factory.objectNode();
        Date start = RestaurantManager.getInstance().getFoodPartyStartTime();
        Date current = new Date();
        long diff = current.getTime() - start.getTime();
        diff = 180000 - diff;
        node.put("minutes", (int) (diff / (60 * 1000)));
        node.put("seconds", (int) ((diff / (1000)) % 60));
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @RequestMapping(value = "/foodParty/{fid}", method = RequestMethod.GET)
    public ResponseEntity<Object> getSpecialFood(
            @PathVariable(value = "fid") String foodId,
            @RequestBody(required = true) JsonNode restaurantId
    ) {
        if (restaurantId == null)
            return new ResponseEntity<>(generateError(factory, 400, "Food doesn't exist"), HttpStatus.NOT_FOUND);
        try {
            SpecialFoodDTO food = RestaurantManager.getInstance().getSpecialFoodById(restaurantId.asText(), foodId);
            return new ResponseEntity<>(food, HttpStatus.OK);
        } catch (FoodDoesntExistException e) {
            return new ResponseEntity<>(generateError(factory, 404, "Food doesn't exist"), HttpStatus.NOT_FOUND);
        }
    }
}