package DataModel;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import DataModel.Product;

/*
    A classification problem typically involves arranging items in some multi-dimensional
    state space.  Once items are positioned correctly, another algorithm can be used to
    partition them.  In general, this can be done with statistical methods, but in this 
    implementation we will use a simpler more descrete method using a sparse matrix
    implemented with Java's hashmap.  In this case positioning items only involves
    traversing down the correct map keys, and partitioning is already done since the
    map keys are descrete.
    
    For example, the top dimension (the most important) can be the manufacturer:

    [Cannon, Sony, ...]

    The second dimension is the model name.  Among Sony cameras there are many different
    models:

    [DSC-HX100v, TX10, ...]

    In addition, we can have more dimensions for things like the camera family.  There is
    no practical limit to how many dimensions this implementation can handle.  This
    implemention should be able to work with products other than cameras with little or no
    modification.

    Each level in the sparse matrix can store a product id that comes from our products.txt
    file.  Later, when we want to find which product a specific listing corresponds to
    we can attempt to traverse down the map using whatever keys are available at the current
    level.  

    When loadings the product ids from products.txt, there can be a case where two different
    products have what seems to be the same relevant information.  In this case, no distinction
    can be made between these products, and a warning is issued.

*/

public class ProductDimension {

  private Map<String, ProductDimension> kvp = new HashMap<String, ProductDimension>();
  private Product uniqueProduct = null;

  public ProductDimension(){
  }

  public Map<String, ProductDimension> getKVP(){
    return kvp;
  }

  public Product getUniqueProduct(){
    return uniqueProduct;
  }

  public Product findUniqueProduct(){
    if(kvp.keySet().size() == 1){ /*  In this case we have map that only contains one entry, so there might only be one product */
      return kvp.get(kvp.keySet().toArray()[0]).findUniqueProduct();
    }else{
      return uniqueProduct;  /*  Returns null if there was no product found */
    }
  }

  public void setSubDimensions(Product p, String ... dimensionKeys){
    List<String> vaArgs = new ArrayList<String>(Arrays.asList(dimensionKeys));
    /*  Have we traversed down all the dimensions ofthe product? */
    if(vaArgs.size() == 0){
      if(uniqueProduct == null){
        uniqueProduct = p;  /*  Yes store the product that has these criteria */
      }else{
        System.out.println("Ignoring product.txt entry:\n" + p);
        System.out.println("Due to inability to distinguish from a similar product:\n" + uniqueProduct);
        System.out.println("These are likely to be the exact same product.\n");
      }
    }else{
      /*  Still more dimensions to traverse down */
      String nextDimension = vaArgs.remove(0).toLowerCase();
      if(!kvp.containsKey(nextDimension)){  /*  This dimension has never been encountered before */
        kvp.put(nextDimension, new ProductDimension());
      }
      /*  Traverse down the remaining dimensions */
      kvp.get(nextDimension).setSubDimensions(p, vaArgs.toArray(new String[vaArgs.size()]));
    }
  }
}
