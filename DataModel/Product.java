package DataModel;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import DataModel.Listing;

/*
    A model for capturing products in the products.txt file.
*/

public class Product {

  private String productName;
  private String manufacturer;
  private String model;
  private String family;
  private String announcedDate; /*  Store as String for now, since we don't use the date */
  private List<Listing> listings = new ArrayList<Listing>();

  public String toString(){
    return getProductName() + " " + getManufacturer() + " " + getModel() + " " + getFamily();
  }

  public String getProductName(){
    return productName;
  }

  public String getManufacturer(){
    return manufacturer;
  }

  public String getModel(){
    return model;
  }

  public String getFamily(){
    return family;
  }

  public void addListing(Listing l){
    listings.add(l);
  }

  public List<Listing> getListings(){
    return listings;
  }

  public Product(JsonParser jsonParser, String json){
    JsonObject product = (JsonObject)jsonParser.parse(json);
    for(Map.Entry<String,JsonElement> entry: product.entrySet()){
      if(entry.getKey().equals("product_name")){
        if(entry.getValue().isJsonNull()){
          assert(false);
        }else{
          productName = entry.getValue().getAsJsonPrimitive().getAsString();
        }
      }else if(entry.getKey().equals("manufacturer")){
        if(entry.getValue().isJsonNull()){
          assert(false);
        }else{
          manufacturer = entry.getValue().getAsJsonPrimitive().getAsString();
        }
      }else if(entry.getKey().equals("model")){
        if(entry.getValue().isJsonNull()){
          assert(false);
        }else{
          model = entry.getValue().getAsJsonPrimitive().getAsString();
        }
      }else if(entry.getKey().equals("family")){
        if(entry.getValue().isJsonNull()){
          family = null; /*  Family is optional */
        }else{
          family = entry.getValue().getAsJsonPrimitive().getAsString();
        }
      }else if(entry.getKey().equals("announced-date")){
        if(entry.getValue().isJsonNull()){
          assert(false);
        }else{
          announcedDate = entry.getValue().getAsJsonPrimitive().getAsString();
        }
      }else{
        assert(false);
      }
    }
  }
}

