# Sortable Coding Challenge

This project is a solution to the Sortable programming challenge located at:

[http://sortable.com/challenge/](http://sortable.com/challenge/)

The problem requires that a file full of product listings be associated with a target list of products.  Part of the challenge is dealing with the non-standard way that these listings are formatted, and avoiding false positives.

This solution is coded in Java and is designed to run on a Linux platform.  Windows is not supported.

You can run the solution in two steps:

1)  git clone https://github.com/RobertElderSoftware/SortableCodingChallenge.git

2)  cd SortableCodingChallenge && make run

results are stored in a file called results.txt

# Algorithm Description

##  Algorithm Synopsis

1)  Load products from products.txt.  Save the product id in a nested map as follows

ex:

map['manufacturer']['model']['family'] = 2345

Normalized manufacturer, model and family by converting to lower case and removing
dashes and spaces

2)

For each listing, take the title and remove common unnecessary information like focal
length or size.  Split the remaining string by spaces and dashes, then use this information
to remember the indices of where word boundaries start.  Recombine the string without the
spaces or dashes to model numbers can be normalized as contiguous strings:

"Sony f/4.4 DKC-34 99K"  -> "sonydkc3499k" with a word boundary at index 0 and at 4.

We can traverse down the map of all products by first looking at the keyset of the top level:

[sony, panasonic, ...]

For each key, we check if the normalized title "sonydkc3499k" contains the string "sony" or "panasonic"
starting at a word boundary.  Traversing down the map of Sony cameras gives up another map of model numbers:

[ds123, ds456, dkc3499k, ...]

Again we search for these keys in the string, until we get to a level where there is a unique product
associated with this position in the nested map.

##  More Algorithm Details

A classification problem typically involves arranging items in some multi-dimensional
state space.  Once items are positioned correctly, another algorithm can be used to
partition them.  In general, this can be done with statistical methods, but in this 
implementation we will use a simpler more discrete method using a sparse matrix
implemented with Java's hashmap.  In this case positioning items only involves
traversing down the correct map keys, and partitioning is already done since the
map keys are discrete.

For example, the top dimension (the most important) can be the manufacturer:

[Cannon, Sony, ...]

The second dimension is the model name.  Among Sony cameras there are many different
models:

[DSC-HX100v, TX10, ...]

In addition, we can have more dimensions for things like the camera family.  There is
no practical limit to how many dimensions this implementation can handle.  This
implementation should be able to work with products other than cameras with little or no
modification.

Each level in the sparse matrix can store a product id that comes from our products.txt
file.  Later, when we want to find which product a specific listing corresponds to
we can attempt to traverse down the map using whatever keys are available at the current
level.  

When loadings the product ids from products.txt, there can be a case where two different
products have what seems to be the same relevant information.  In this case, no distinction
can be made between these products, and a warning is issued.

# Asymptotic runtime analysis

There are several main variables that will control the overall run-time performance of this algorithm.  They are

1)  The number of products
2)  The number of listings
3)  The degree of fragmentation within product categories
4)  Regexes.


1)  A linear increase in the number of products will cause a linear increase in the number of hashmap lookups.  Hashtable lookups are typically considered to be O(1) operations, but whether this happens in practice depends on factors such as collision resolution method, average load factor and the data itself.  Java's implementation of HashMap claims to maintain a load factor of 0.75:

[Oracle Docs on HashMap](http://docs.oracle.com/javase/6/docs/api/java/util/HashMap.html)

Worst case performance of Java's HashMap insertion appears to be implementation dependent (I haven't been able to find an authoritative answer on this) since this implementation:

[An implementation of Java HashMap](http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b27/java/util/HashMap.java#HashMap.addEntry%28int%2Cjava.lang.Object%2Cjava.lang.Object%2Cint%29)

appears to use a linear time collision resolution.

Based on this information, it would appear that in the worst case, this algorithm scales in O(n^2) where n is the number of products, however since a large number of hash collisions seems to be rare, the average complexity is closer to O(n).

2)  Just like for products, a linear increase in the number of products should case a linear increase in the number of hashmap lookups, and other constant time operations.

 An empirical test shows that the run time scales linearly (almost directly proportional) to the number of listings.

 20196   listings ~2.2  seconds
 40392   listings ~3.7  seconds
 80784   listings ~6.4  seconds
161568   listings ~12.5 seconds
323136   listings ~23.5 seconds
646272   listings ~47   seconds
1292544  listings ~93   seconds
2585088  listings java.lang.OutOfMemoryError: Java heap space  listings is now 407MB

Reference-style: 
![Runtime][https://github.com/RobertElderSoftware/SortableCodingChallenge/runtime.png]

Note that at around 407MB of listings the JVM ran out of memory. The current implementation uses a linear amount of memory (which is not strictly necessary) but provides good run time performance.

3)  More fragmentation among products (having many different manufacturers with many families of products) is preferable for performance since this will decrease the number of possible keys that appear on each dimension level.  If all products are concentrated in one category on one dimension, then the each listing would be performing a linear lookup over all products.  Instead having products spread over many dimensions in an evenly distributed tree provides a more logarithmic traversal cost. 

4)  In order to remove unuseful information regexes were used to remove information like focal point, or zoom statistics that can get confused with product numbers.  These regexes use some inefficient operators like "\*" that can have exponential complexity in the worst case.  In our case they are applied to strings that are very simple so the cost is reasonable, however degenerate hand crafted inputs could be used to perform an algorithmic complexity attack.

[Algorithmic Complexity Attack](http://en.wikipedia.org/wiki/Algorithmic_complexity_attack)
