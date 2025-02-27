package cs2030s.grader;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.function.Function;

/**
 * Kelas is a wrapper around Java's reflection Class class.  It provides
 * additional methods to help to check for good programming practice and
 * to verify if classes follow a certain specification.
 * <p>
 * Created to help with grading of CS2030 homework.
 */

public class Kelas {
    // c is the Java Class object we wrap around.
    private Class<?> c;

    /**
     * Construct a Kelas object for the class with the given name.
     *
     * @param className The name of the class.
     * @throws ClassNotFoundException Thrown if the className is not valid.
     */
    public Kelas(String className) throws ClassNotFoundException {
      this.c = Class.forName(className);
    }

    /**
     * Construct a Kelas object for the given Class object.
     * (e.g., new Kelas(String.class))
     *
     * @param c The Class object
     */
    public Kelas(Class<?> c) {
        this.c = c;
    }

    /**
     * Checks if this class is an abstract class.
     *
     * @return true if it is an abstract class; false otherwise.
     */
    public boolean isAbstract() {
        return Modifier.isAbstract(c.getModifiers());
    }

    /**
     * Checks if this class is an interface.
     *
     * @return true if it is an interface; false otherwise.
     */
    public boolean isInterface() {
        return c.isInterface();
    }

    /**
     * Get all fields
     * @return KelasFields
     */
    public KelasFields getFields() {
        return new KelasFields(Stream.of(this.c.getDeclaredFields()));
    }

    /**
     * Get all methods
     * @return KelasMethods
     */
    public KelasMethods getMethods() {
        return new KelasMethods(Stream.of(this.c.getDeclaredMethods()));
    }

    /**
     * Get all constructors
     * @return KelasConstructors
     */
    public KelasConstructors getConstructors() {
      return new KelasConstructors(Stream.of(this.c.getDeclaredConstructors()));
    }

    public boolean doesExtend(Kelas parent) {
        // Check if class has no inheritance
        if (this.c.getSuperclass().equals(Object.class)) {
            return false;
        }

        if (this.c.getSuperclass().equals(parent.c)) {
            return true;
        }
        return false;
    }

    public boolean doesImplement(Kelas parentInterface) throws ClassNotFoundException {
                // Check if class has no inheritance
        if (this.c.getSuperclass().equals(Object.class)) {
            return false;
        }

        for (Class<?> ifs1 : this.c.getInterfaces()) {
            if (ifs1.equals(parentInterface.c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return all (immediate) parent supertype with another class.
     * The common parent could be a class or an interface.  Object
     * does not count as common parent.
     *
     * @return A list of Class objects.
     */
    public List<Class<?>> getCommonSupertypeWith(Kelas that) {

        List<Class<?>> list = new ArrayList<>();

        if (this.c.getSuperclass().equals(that.c.getSuperclass()) &&
                !this.c.getSuperclass().equals(Object.class)) {
            list.add(this.c.getSuperclass());
        }

        list.addAll(this.getCommonInterfacesWith(that));

        return list;
    }

    public boolean commonParent(Kelas that) {
        return this.c.getSuperclass().equals(that.c.getSuperclass());
    }

    public List<Class<?>> getCommonInterfacesWith(Kelas that) {
        List<Class<?>> list = new ArrayList<>();
        for (Class<?> ifs1 : this.c.getInterfaces()) {
            for (Class<?> ifs2 : that.c.getInterfaces()) {
                if (ifs1.equals(ifs2)) {
                    list.add(ifs1);
                }
            }
        }
        return list;
    }

    public List<Class<?>> getSubclasses() throws ClassNotFoundException, java.io.IOException {
        return KelasUtils.getClasses().stream()
          .filter(c -> this.c.equals(c.getSuperclass()))
          .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Checks if this class share at least one common (immediate) parent with
     * another class.  The common parent could be a class or an interface.
     * Note: Object does not count as common parent.
     *
     * @return true if a shared parent is found; false otherwise.
     */
    public boolean shareCommonSupertypeWith(Kelas that) {
        List<Class<?>> commonSupertypes = this.getCommonSupertypeWith(that);
        return commonSupertypes.size() > 0;
    }

    public String toString() {
        return c.toString();
    }

    public Class<?> getC() {
        return c;
    }

    public boolean equals(Kelas k2) {
        return c.equals(k2.c);
    }

    public <T> T apply(Function<Class<?>, T> func) {
      return func.apply(c);
    }
}
