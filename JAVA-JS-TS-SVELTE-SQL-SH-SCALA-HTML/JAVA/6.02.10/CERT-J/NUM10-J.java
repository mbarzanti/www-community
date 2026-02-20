/*
Noncompliant Code Example
This noncompliant code example passes a double value to the BigDecimal constructor. Because the decimal literal 0.1 cannot be precisely represented by a double, precision of the BigDecimal is affected.
*/


// Prints 0.1000000000000000055511151231257827021181583404541015625
// when run in FP-strict mode 
System.out.println(new BigDecimal(0.1)); 
