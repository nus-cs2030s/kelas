import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Kelas is a wrapper around Java's reflection Class class.  It provides
 * additional methods to help to check for good programming practice and
 * to verify if classes follow a certain specification.
 * <p>
 * Created to help with grading of CS2030 homework.
 */

class KelasOld {
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
     * Get all fields
     * @return Get all fields
     */
    public List<Field> getAllFields() {
        return Stream.of(this.c.getDeclaredFields())
                .collect(Collectors.toList());
    }

    /**
     * Return a list of public fields defined in this class
     *
     * @return A list of public fields.
     */
    public List<Field> getPublicFields() {
        return Stream.of(this.c.getDeclaredFields())
                .filter(f -> Modifier.isPublic(f.getModifiers()))
                .collect(Collectors.toList());
    }

    public boolean containsPublicFields() {
        return !this.getPublicFields().isEmpty();
    }

    public List<Field> getDefaultFields() {
        return Stream.of(this.c.getDeclaredFields())
                .filter(f -> !Modifier.isPublic(f.getModifiers()))
                .filter(f -> !Modifier.isPrivate(f.getModifiers()))
                .filter(f -> !Modifier.isProtected(f.getModifiers()))
                .collect(Collectors.toList());
    }

    public boolean containsDefaultFields() {
        return !this.getDefaultFields().isEmpty();
    }

    /**
     * Return a list of private fields defined in this class, excluding
     * constants defined as private static final.
     *
     * @return A list of public fields.
     */
    public List<Field> getPrivateFields() {
        return Stream.of(this.c.getDeclaredFields())
                .filter(f -> Modifier.isPrivate(f.getModifiers()))
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> !Modifier.isFinal(f.getModifiers()))
                .collect(Collectors.toList());
    }

    public boolean containsPrivateFields() {
        return !this.getPrivateFields().isEmpty();
    }

    /**
     * Return a list of public static final fields defined in this class.
     *
     * @return A list of public static final fields.
     */
    public List<Field> getPublicConstants() {
        return Stream.of(this.c.getDeclaredFields())
                .filter(f -> Modifier.isPublic(f.getModifiers()) &&
                        Modifier.isStatic(f.getModifiers()) &&
                        Modifier.isFinal(f.getModifiers()))
                .collect(Collectors.toList());
    }

    /**
     * Return a list of private static final fields defined in this class.
     *
     * @return A list of private static final fields.
     */
    public List<Field> getPrivateConstants() {
        return Stream.of(this.c.getDeclaredFields())
                .filter(f -> Modifier.isPrivate(f.getModifiers()) &&
                        Modifier.isStatic(f.getModifiers()) &&
                        Modifier.isFinal(f.getModifiers()))

                .collect(Collectors.toList());
    }

    public boolean containsConstants() {
        return !this.getPublicConstants().isEmpty();
    }

    /**
     * Return true if a field is found with given type and value
     *
     * @return A list of fields.
     */
    public <T> boolean hasFieldWithTypeValue(Class<T> type, T value) {
        try {
            List<Field> fields = getAllFields();
            for (Field f : fields) {
                if (f.getType() == type && type.cast(f.get(null)).equals(value)) {
                    return true;
                }
            }
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    /**
     * Return true if a constant field is found with given type and value
     *
     * @return A list of public static final fields.
     */
    public <T> boolean hasPublicConstantFieldWithTypeValue(Class<T> type, T value) {
        try {
            List<Field> consts = getPublicConstants();
            for (Field f : consts) {
                if (f.getType() == type && type.cast(f.get(null)).equals(value)) {
                    return true;
                }
            }
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    public <T> boolean hasPrivateConstantFieldWithTypeValue(Class<T> type, T value) {
        try {
            List<Field> consts = getPrivateConstants();
            for (Field f : consts) {
                f.setAccessible(true);
                if (type.equals(f.get(null).getClass()) && type.cast(f.get(null)).equals(value)) {
                    return true;
                }
            }
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    /**
     * Checks if class has a certain public method
     *
     * @return true if such a method is found.  false otherwise.
     */
    public boolean hasPublicMethod(String name, Class<?>... paramTypes) {
        try {
            Method m = this.c.getDeclaredMethod(name, paramTypes);
            if (!Modifier.isPublic(m.getModifiers())) {
                return false;
            }
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    /**
     * Get all methods
     *
     * @return list of methods.
     */
    public List<Method> getMethods() {
        return List.of(this.c.getDeclaredMethods());
    }

    /**
     * Checks if this class share at least one common (immediate) parent with
     * another class.  The common parent could be a class or an interface.
     * Note: Object does not count as common parent.
     *
     * @return true if a shared parent is found; false otherwise.
     */
    public boolean shareCommonSupertypeWith(Kelas ac) {
        if (this.c.getSuperclass().equals(Object.class)) {
            return false;
        }
        if (this.c.getSuperclass().equals(ac.c.getSuperclass())) {
            return true;
        }

        for (Class<?> ifs1 : this.c.getInterfaces()) {
            for (Class<?> ifs2 : ac.c.getInterfaces()) {
                if (ifs1.equals(ifs2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isChildOf(String Parent) throws ClassNotFoundException {
        if (this.c.getSuperclass().equals(Object.class)) {
            return false;
        }
        Class<?> parentClass = Class.forName(Parent);
        if (this.c.getSuperclass().equals(parentClass)) {
            return true;
        }

        for (Class<?> ifs1 : this.c.getInterfaces()) {
            if (ifs1.equals(parentClass)) {
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

        for (Class<?> ifs1 : this.c.getInterfaces()) {
            for (Class<?> ifs2 : that.c.getInterfaces()) {
                if (ifs1.equals(ifs2)) {
                    list.add(ifs1);
                }
            }
        }
        return list;
    }

    /**
     * Checks if this class implements a given Generic interface.
     *
     * @param name         The fully qualified name of the interface
     * @param typeArgument The fully qualified name of the typeArgument
     * @return true if the class imlpements the interface; false otherwise.
     */
    public boolean doesImplementGenericInterface(String name) {
        Type[] parents = this.c.getGenericInterfaces();
        for (Type ifs : parents) {
            if (ifs.getTypeName().equals(name)) {
                return true;
            }
        }
        return false;
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
     * Checks if this class has an abstract method.
     *
     * @return true if it has at least one abstract method; false otherwise.
     */
    public boolean hasAbstractMethods() {
        Method[] methods = c.getDeclaredMethods();
        for (Method m : methods) {
            if (Modifier.isAbstract(m.getModifiers())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this interface has an interface method
     * @return
     */
    public boolean hasMethods() {
        Method[] methods = c.getDeclaredMethods();
        if (methods.length > 0) {
            return true;
        }
        return false;
    }

    public String toString() {
        return c.toString();
    }

    public boolean equals(Kelas k2) {
        return c.equals(k2.c);
    }
}
