import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import tester.Tester;


//represents a generic list
interface IList<T> {
  //filter this list using the given predicate
  IList<T> filter(Predicate<T> pred);

  //map a given function onto every member of this list and return a list of the results
  <U> IList<U> map(Function<T, U> converter);

  //combine the items in this list using the given function
  <U> U fold(BiFunction<T, U, U> converter, U initial);  
}

//represents a generic empty list
class MtList<T> implements IList<T> {
  
  MtList() {}
  
  /* Template: 
   * 
   * Methods:
   * ...this.filter(Predicate<T> pred)...                                 -- IList<T>
   * ...this.map(Function<T, U> converter)...                             -- <U>
   * ...this.fold(BiFunction<T, U, U> converter, U initial)...            -- <U>
   */
  
  //filter this empty list using the given predicate
  public IList<T> filter(Predicate<T> pred) {
    return new MtList<T>();
  }

  //map a given function onto every member of this list and return a list of the results
  public <U> IList<U> map(Function<T, U> converter) {
    return new MtList<U>();
  }

  //combine the items in this list using the given function
  public <U> U fold(BiFunction<T, U, U> converter, U initial) {
    return initial;
  }
}

//represents a generic non-empty list
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;
  
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /* Template: 
   * 
   * Fields:
   * ...this.first...                 -- T
   * ...this.rest...                  -- IList<T>
   * 
   * Methods:
   * ...this.filter(Predicate<T> pred)...                                 -- IList<T>
   * ...this.map(Function<T, U> converter)...                             -- <U>
   * ...this.fold(BiFunction<T, U, U> converter, U initial)...            -- <U>
   * 
   * Methods for fields:
   * ...this.rest.filter(Predicate<T> pred)...                                 -- IList<T>
   * ...this.rest.map(Function<T, U> converter)...                             -- <U>
   * ...this.rest.fold(BiFunction<T, U, U> converter, U initial)...            -- <U>
   */

  //filter this non-empty list using the given predicate
  public IList<T> filter(Predicate<T> pred) {
    if (pred.test(this.first)) {
      return new ConsList<T>(this.first, this.rest.filter(pred));
    }
    else {
      return this.rest.filter(pred);
    }
  }

  //map a given function onto every member of this list and return a list of the results
  public <U> IList<U> map(Function<T, U> converter) {
    return new ConsList<U>(converter.apply(this.first), this.rest.map(converter));
  }

  //combine the items in this list using the given function
  public <U> U fold(BiFunction<T, U, U> converter, U initial) {
    return converter.apply(this.first, this.rest.fold(converter,initial));
  }
}


class startsWithJ implements Predicate<String> {
  
  // tests whether given String begins with "J"
  public boolean test(String t) {
    return t.substring(0, 1).equals("J");
  }
} 

class endsWithEr implements BiFunction<String, Integer, Integer> {
  
  // adds 1 to count if given String ends in "er"
  public Integer apply(String t, Integer i) {
    if ((t.substring(t.length() - 2)).equals("er")) {
      return i + 1;
    }
    return i + 0;
  }
}

class firstThree implements Function<String, String> {
  
  // abbreviates given String by first three letters
  public String apply(String t) {
    return t.substring(0, 3);
  }
}

class ExamplesLists{

  ExamplesLists() {}
  
  Predicate<String> object1 = new startsWithJ();
  BiFunction<String, Integer, Integer> object2 = new endsWithEr();
  Function<String, String> object3 = new firstThree();
  
  IList<String> empty = new MtList<String>();
  IList<String> months1 = new ConsList<String>("January", new ConsList<String>("February", new ConsList<String>("June", new ConsList<String>("May", new ConsList<String>("July", this.empty)))));
  IList<String> months2 = new ConsList<String>("November", new ConsList<String>("June", new ConsList<String>("December", new ConsList<String>("October", this.empty))));
  IList<String> months3 = new ConsList<String>("August", new ConsList<String>("March", new ConsList<String>("June", new ConsList<String>("May", new ConsList<String>("November", this.empty)))));
  
  // test the method filter for months that begin with letter "J"
  boolean testFilter(Tester t) {
    return t.checkExpect(this.empty.filter(this.object1), this.empty)
        && t.checkExpect(this.months1.filter(this.object1), new ConsList<String>("January", new ConsList<String>("June", new ConsList<String>("July", this.empty))));
  }
  
  // test the method fold to count months that end in "er"
  boolean testFold(Tester t) {
    return t.checkExpect(this.empty.fold(this.object2, 0), 0)
        && t.checkExpect(this.months2.fold(this.object2, 0), 3);
  }
  
  // test the method map to create 3-letter abbreviations for given months
  boolean testMap(Tester t) {
    return t.checkExpect(this.empty.map(this.object3), this.empty)
        && t.checkExpect(this.months3.map(this.object3), new ConsList<String>("Aug", new ConsList<String>("Mar", new ConsList<String>("Jun", new ConsList<String>("May", new ConsList<String>("Nov", this.empty))))));
  }
  
  // test the method test in the class startsWithJ
  boolean testTestStartsWithJ(Tester t) {
    return t.checkExpect(this.object1.test("a"), false)
        && t.checkExpect(this.object1.test("J"), true);
  }
  
  //test the method apply in the class endsWithEr
  boolean testApplyEndsWithEr(Tester t) {
     return t.checkExpect(this.object2.apply("June", 0), 0)
         && t.checkExpect(this.object2.apply("November", 0), 1);
  }
 
  //test the method apply in the class firstThree
  boolean testApplyFirstThree(Tester t) {
     return t.checkExpect(this.object3.apply("June"), "Jun")
         && t.checkExpect(this.object3.apply("May"), "May");
  } 
}