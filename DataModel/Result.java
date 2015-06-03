package DataModel;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import DataModel.Product;
import DataModel.Listing;


/*
    A model for capturing results in the results.txt file.
*/

public class Result {
  private Product product;
  private List<Listing> listings = new ArrayList<Listing>();

  public void addListing(Listing l){
    listings.add(l);
  }

  public void setProduct(Product p){
    product = p;
  }

  public JsonElement getJsonElement(){
    final JsonObject jsonObject = new JsonObject();
    final JsonArray jsonListingsArray = new JsonArray();
    jsonObject.addProperty("product_name", product.getProductName());
    for (final Listing l: listings) {
        jsonListingsArray.add(l.getJsonElement());
    }
    jsonObject.add("listings", jsonListingsArray);
    return jsonObject;
  }
}
