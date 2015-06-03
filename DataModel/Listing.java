package DataModel;

import java.util.Map;

import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

/*
    A model for capturing listings in the listings.txt file.
*/

public class Listing {

  private String title;
  private String manufacturer;
  private String currency;
  private String price;

  public String toString(){
    return getTitle() + " " + getManufacturer() + " " + getCurrency() + " " + getPrice();
  }

  public String getTitle(){
    return title;
  }

  public String getManufacturer(){
    return manufacturer;
  }

  public String getCurrency(){
    return currency;
  }

  public String getPrice(){
    return price;
  }

  public Listing(JsonParser jsonParser, String json){
    JsonObject product = (JsonObject)jsonParser.parse(json);
    for(Map.Entry<String,JsonElement> entry: product.entrySet()){
      if(entry.getKey().equals("title")){
        if(entry.getValue().isJsonNull()){
          assert(false);
        }else{
          title = entry.getValue().getAsJsonPrimitive().getAsString();
        }
      }else if(entry.getKey().equals("manufacturer")){
        if(entry.getValue().isJsonNull()){
          assert(false);
        }else{
          manufacturer = entry.getValue().getAsJsonPrimitive().getAsString();
        }
      }else if(entry.getKey().equals("currency")){
        if(entry.getValue().isJsonNull()){
          assert(false);
        }else{
          currency = entry.getValue().getAsJsonPrimitive().getAsString();
        }
      }else if(entry.getKey().equals("price")){
        if(entry.getValue().isJsonNull()){
          assert(false);
        }else{
          price = entry.getValue().getAsJsonPrimitive().getAsString();
        }
      }else{
        assert(false);
      }
    }
  }

  /*  For testing output format, character encodings etc. */
  /*  Inputting then outputting unmodified listing should result in identical file  */
  public JsonElement getJsonElement(){
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("title", getTitle());
    jsonObject.addProperty("manufacturer", getManufacturer());
    jsonObject.addProperty("currency", getCurrency());
    jsonObject.addProperty("price", getPrice());
    return jsonObject;
  }
}
