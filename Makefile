run: SortableChallenge.class DataModel/Product.class DataModel/Listing.class DataModel/ProductDimension.class DataModel/Result.class
	@java -cp .:google-gson-2.2.4/gson-2.2.4.jar SortableChallenge DataModel/Product DataModel/Listing DataModel/ProductDimension DataModel/Result

SortableChallenge.class: SortableChallenge.java
	@javac -Werror -Xlint:all -cp .:google-gson-2.2.4/gson-2.2.4.jar SortableChallenge.java

DataModel/Product.class: DataModel/Product.java
	@javac -Werror -Xlint:all -cp .:google-gson-2.2.4/gson-2.2.4.jar DataModel/Product.java

DataModel/Listing.class: DataModel/Listing.java
	@javac -Werror -Xlint:all -cp .:google-gson-2.2.4/gson-2.2.4.jar DataModel/Listing.java

DataModel/ProductDimension.class: DataModel/ProductDimension.java
	@javac -Werror -Xlint:all -cp .:google-gson-2.2.4/gson-2.2.4.jar DataModel/ProductDimension.java

DataModel/Result.class: DataModel/Result.java
	@javac -Werror -Xlint:all -cp .:google-gson-2.2.4/gson-2.2.4.jar DataModel/Result.java

findbugs: SortableChallenge.class
	@findbugs -textui SortableChallenge.class
	@findbugs -textui DataModel/Product.class
	@findbugs -textui DataModel/Listing.class
	@findbugs -textui DataModel/ProductDimension.class
	@findbugs -textui DataModel/Result.class

clean:
	rm SortableChallenge.class DataModel/Product.class DataModel/Listing.class DataModel/ProductDimension.class DataModel/Result.class
