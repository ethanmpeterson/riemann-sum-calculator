class Interval { // class that parses closed interval text and converts upper and lower bounds to integer and or floats
  Textfield inputField;
  String input;
  
  float upper;
  float lower;
  
  Interval(Textfield i) { // constructor taking a text field object to convert to string and parse
    inputField = i;
  }
  
  boolean parse() { // returns true if the interval is valid and the values have been converted to float and stored in upper and lower variables
    input = inputField.getText();
    if (input.trim().length() > 0) { // if something has been entered continue parsing
      if (input.charAt(0) == '[' && input.charAt(input.length() - 1) == ']') { // check for the square brackets
        for (int i = 1; i < input.length(); i++) {
          if (input.charAt(i) == ',') {
            try {
              lower = Float.parseFloat(input.substring(1, i)); // needs to be put in try catch to ensure conversion works
              upper = Float.parseFloat(input.substring(i + 1, input.length() - 1));
            } catch (NumberFormatException e) {
              return false;
            }
            lower = Float.parseFloat(input.substring(1, i)); // needs to be put in try catch to ensure conversion works
            upper = Float.parseFloat(input.substring(i + 1, input.length() - 1));
            println(lower);
            println(upper);
            break;
          }
        }
      } else {
      /* Throw error for invalid brackets */
      return false;
      }
    } else {
  /* Throw error for invalid input */
      return false;
    }
    return true;
  }
  
}