import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;

import DataModel.Product;
import DataModel.Listing;
import DataModel.ProductDimension;
import DataModel.Result;

class SortableChallenge {

  private static final String productsFile = "products.txt";
  private static final String listingsFile = "listings.txt";
  private static final String resultsFile = "results.txt";
  /*  Regexes of things to remove that get confused with model numbers */
  private static final Pattern [] junkPatterns = {
    Pattern.compile("(3d)"),
    Pattern.compile("(1080p)"),
    Pattern.compile("([0-9]+x)"), /*  Zoom */
    Pattern.compile("(([0-9]+)*-*[0-9]+\\s*mm)"),
    Pattern.compile("(([0-9]+)*-*[0-9]+\\s*inch)"),
    Pattern.compile("(\\sf/?\\s*[0-9]+[,\\.]?[0-9]+\\s*-\\s*[0-9]+[,\\.]?[0-9]+)"), /*  Focal length */
    Pattern.compile("(\\sf/?\\s*[0-9]+)"), /*  Focal length */
    Pattern.compile("(([0-9]+)*[,\\.]?[0-9]+-?\\s?(inch|mpix|gb|mb|mp|megapixel))")
  };

  public static String removeSpacesAndDashes(String s, List<Integer> wordBoundaries){
    /*  This function removes the '-' and ' ' characters, and also keeps track of */
    /*  the indicies of where words start in the original string. */
    String [] parts = s.split(" ");
    StringBuilder result = new StringBuilder();
    int currentPosition = 0;
    for(int i = 0; i < parts.length; i++){
      String [] subParts = parts[i].split("-");
      for(int j = 0; j < subParts.length; j++){
        result.append(subParts[j]);
        wordBoundaries.add(currentPosition);
        currentPosition += subParts[j].length();
      }
    }
    return result.toString();
  }

  public static String removeJunk(String s) {
    /*  Remove information that can get confused with model numbers */
    s = s.toLowerCase();
    boolean moreMatches;
    do {
      moreMatches = false;
      for(Pattern p : junkPatterns){
        Matcher m = p.matcher(s);
        if(m.find()){
          moreMatches = true;
          s = m.replaceAll(""); /*  Remove */
        }
      }
    }while(moreMatches);
    return s;
  }

  public static void main(String[] args) {
    StringBuilder resultsOutput = new StringBuilder();
    List<Product> products = new ArrayList<Product>();
    List<Listing> listings = new ArrayList<Listing>();
    JsonParser parser = new JsonParser();
    ProductDimension topDimension = new ProductDimension();

    /*  Read in products */
    try {
      InputStream in = new FileInputStream(productsFile);
      Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
      BufferedReader br = new BufferedReader(reader);
      String line;
      while ((line = br.readLine()) != null) {
         products.add(new Product(parser, line));
      }
    }catch (Exception e) {
      System.out.println("Failed to read products file.");
      e.printStackTrace();
      System.exit(-1);
    }

    /*  Read in listings */
    try {
      InputStream in = new FileInputStream(listingsFile);
      Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
      BufferedReader br = new BufferedReader(reader);
      String line;
      while ((line = br.readLine()) != null) {
         Listing l = new Listing(parser, line);
         listings.add(l);
      }
    }catch (Exception e) {
      System.out.println("Failed to read listings file.");
      e.printStackTrace();
      System.exit(-1);
    }

    /*  Populate our sparse matrix with the product ids */
    for(int i = 0; i < products.size(); i++){
      List<String> dimensions = new ArrayList<String>();
      dimensions.add(products.get(i).getManufacturer().toLowerCase().replace(" ", "").replace("-",""));
      dimensions.add(products.get(i).getModel().toLowerCase().replace(" ", "").replace("-",""));
      if(products.get(i).getFamily() != null){
        dimensions.add(products.get(i).getFamily().toLowerCase().replace(" ", "").replace("-",""));
      }
      topDimension.setSubDimensions(products.get(i), dimensions.toArray(new String[dimensions.size()]));
    }

    int recalled = 0;
    int notRecalled = 0;
    for(Listing l : listings){
      /*  A list of word boundaries used to keep track of where words start as in the original string */
      List<Integer> wordBoundaries = new ArrayList<Integer>();
      String normalizedListing = removeSpacesAndDashes(removeJunk(l.getTitle()), wordBoundaries);
      ProductDimension currentDimension = topDimension;

      while(true){
        Integer keyIndex = null;
        List<String> dimensionKeys = new ArrayList<String>(currentDimension.getKVP().keySet());
        /*  Determine if there is a unique dimension key that is a substring of the normalized version of the listing */
        int i = 0;
        for(i = 0; i < dimensionKeys.size(); i++){
          Integer searchIndex = normalizedListing.indexOf(dimensionKeys.get(i));
          /*  Check if the current key was found at a place where a word boundary started in the original string */
          if(searchIndex != -1 && wordBoundaries.contains(searchIndex)){
            if(keyIndex == null){
              keyIndex = i; /*  This might be the key to traverse down with */
            }else{ /*  Otherwise, there are multiple possibilities and we don't know which one is the correct one  */
              if(dimensionKeys.get(i).indexOf(dimensionKeys.get(keyIndex)) != -1){
                /*  Current is a better match because previous one is a substring of current */
                keyIndex = i;
              }else if(dimensionKeys.get(keyIndex).indexOf(dimensionKeys.get(i)) != -1){
                /*  No need for any change; current one is a substring of previous  */
              }else{
                /*  There are multiple keys that might be correct and we don't know which one to use */
                keyIndex = null;
                break;
              }
            }
          }
        }

        if(keyIndex == null){
          break; /*  Can't traverse anywhere in the sparse matrix */
        }else{
          /*  Go down a dimension so we can keep going */
          currentDimension = currentDimension.getKVP().get(dimensionKeys.get(keyIndex));
        }
      }

      if(currentDimension.getUniqueProduct() != null){ /*  The producted was listed in this dimension */
        currentDimension.getUniqueProduct().addListing(l);
        recalled++;
      }else if(currentDimension.findUniqueProduct() != null){ /*  There was only one product in a lower dimension */
        currentDimension.findUniqueProduct().addListing(l);
        recalled++;
      }else{
        notRecalled++;
      }
    }

    /*  Build our list of result objects */
    for(Product p : products){
       Result r = new Result();
       r.setProduct(p);
       for(Listing l : p.getListings()){
         r.addListing(l);
       }
       resultsOutput.append(r.getJsonElement().toString() + "\n");
    }

    try {
      PrintWriter out = new PrintWriter(resultsFile);
      out.println(resultsOutput);
      out.close();
    }catch (Exception e) {
      System.out.print("Failed to output listings file.");
      e.printStackTrace();
      System.exit(-1);
    }
    System.out.println("Recalled " + recalled + ".  " + notRecalled + " inconclusive.\n");
    System.out.println("Results can be found in 'results.txt'.");

    System.exit(0);
  }
}
