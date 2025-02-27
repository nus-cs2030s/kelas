package cs2030s.grader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

public class KelasUtils {

  /**
   * Check if classes exist
   *
   * @param classes List<String> of class names to check for existence
   * @throws ClassNotFoundException Thrown if the class is not valid.
   */
  public static Check checkIfClassesExist(List<String> classes) {
    Boolean allClassesFound = true;
    String notFoundName = "";
    for (String name : classes) {
      try {
        new Kelas(name);
      } catch (ClassNotFoundException e) {
        allClassesFound = false;
        notFoundName = name;
      }
    }
    return new Check(allClassesFound, "Class not found: " + notFoundName);
  }

  /**
   * Check for common parent
   */
  public static Check checkCommonParent(String name1, String name2)
      throws ClassNotFoundException {
      Boolean pass = false;

      Kelas k1 = new Kelas(name1);
      Kelas k2 = new Kelas(name2);
      if (!k1.shareCommonSupertypeWith(k2)) {
        pass = true;
      }

      return new Check(pass, name1 + " & " + name2 + " do not have common parent.");
  }

  /**
   * Check for child
   */
  public static Check checkChildOf(String child, String parent) throws ClassNotFoundException {
    Kelas kChild = new Kelas(child);
    Kelas kParent = new Kelas(parent);
    boolean isChildOf = kChild.doesExtend(kParent);

    return new Check(isChildOf, child + " does not inherit from " + parent + ".");
  }

  public static Check mustNotHaveOnlyInterfaceAsParent(String name1, String name2)
      throws ClassNotFoundException {
      Kelas k1 = new Kelas(name1);
      Kelas k2 = new Kelas(name2);

      boolean foundClass = k1.getCommonInterfacesWith(k2).size() == k1.getCommonSupertypeWith(k2).size()
        ? false : true;

      return new Check(foundClass, name1 + " and " + name2 + " only have common interface.");
  }

  // public static void mustHaveProperAbstractClassAsParent(String name1, String name2)
  //         throws ClassNotFoundException {
  //     // Check if common parent is a proper abstract class
  //     Kelas k1 = new Kelas(name1);
  //     Kelas k2 = new Kelas(name2);
  //     List<Class<?>> list = k1.getCommonSupertypeWith(k2);
  //     boolean pass;
  //     for (Class<?> c : list) {
  //         Kelas k = new Kelas(c);
  //         if (k.isAbstract()) {
  //             if (!k.hasAbstractMethods()) {
  //                 //System.out.println(k + ": is abstract but does not have any abstract method");
  //             }
  //         }
  //     }
  // }

  // public static void mustDeclareComputeFareOrConfigureParent(String name)
  //         throws ClassNotFoundException {
  //     Kelas k = new Kelas(name);
  //     if (k.hasPublicMethod("computeFare", Class.forName("Request"))) {
  //         return;
  //     } else {
  //         //System.out.println(name + ": no computeFare(). Parent's constructors:");
  //         Constructor<?>[] cs = Class.forName(name).getSuperclass().getDeclaredConstructors();
  //         for (Constructor<?> c : cs) {
  //             //System.out.println(" - " + c);
  //         }
  //     }
  // }

  // public static Boolean mustImplementOneOfGenericInterfaces(String name, String... intfNames)
  //         throws ClassNotFoundException {
  //     Kelas k = new Kelas(name);
  //     boolean found = false;
  //     for (String intfName : intfNames) {
  //         if (k.doesImplementGenericInterface(intfName)) {
  //             found = true;
  //             break;
  //         }
  //     }
  //     if (!found) {
  //         //System.out.println("The class " + name + " does not implement one of:");
  //         for (String intfName : intfNames) {
  //             //System.out.println(" - " + intfName);
  //         }
  //     }
  //     return found;
  // }
  //
  public static List<Class<?>> getClasses()
      throws ClassNotFoundException, java.io.IOException {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      assert classLoader != null;
      java.net.URL resource = classLoader.getResource(".");
        return findClasses(new java.io.File(resource.getFile()), ".");
  }

  private static List<Class<?>> findClasses(java.io.File directory, String packageName) throws ClassNotFoundException {
    List<Class<?>> classes = new java.util.ArrayList<>();
    if (!directory.exists()) {
      return classes;
    }
    java.io.File[] files = directory.listFiles();
    for (java.io.File file : files) {
      if (file.getName().endsWith(".class")) {
        String className = file.getName().substring(0, file.getName().length() - 6);
        classes.add(Class.forName(className));
      }
    }
    return classes;
  }
}
