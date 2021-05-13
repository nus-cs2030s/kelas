package cs2030s.grader;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream wrapper that expresses the fields of a Kelas.
 * Methods can be chained, except for terminal operations listed below.
 */
public class KelasConstructors {
    private Stream<Constructor<?>> stream;

    public KelasConstructors(Stream<Constructor<?>> stream) {
        this.stream = stream;
    }

    /*
     * Basic field checks
     */
    public KelasConstructors filterPublic(boolean allowed) {
        Predicate<Constructor<?>> pred = allowed 
            ? f -> Modifier.isPublic(f.getModifiers()) 
            : f -> !Modifier.isPublic(f.getModifiers());
        this.stream = this.stream.filter(pred);
        return this;
    }

    public KelasConstructors filterPrivate(boolean allowed) {
        Predicate<Constructor<?>> pred = allowed 
            ? f -> Modifier.isPrivate(f.getModifiers()) 
            : f -> !Modifier.isPrivate(f.getModifiers());
        this.stream = this.stream.filter(pred);
        return this;
    }

    public boolean hasOnlyPrivate() {
        return this.stream.allMatch(f -> Modifier.isPrivate(f.getModifiers()));
    }

    public KelasConstructors excludeDefault() {
        this.stream = this.stream
          .filter(c -> c.getParameterCount() != 0 || Modifier.isPublic(c.getModifiers()));
        return this;
    }

    public KelasConstructors areProtected(boolean allowed) {
        Predicate<Constructor<?>> pred = allowed 
            ? f -> Modifier.isProtected(f.getModifiers()) 
            : f -> !Modifier.isProtected(f.getModifiers());
        this.stream = this.stream.filter(pred);
        return this;
    }

    public KelasConstructors areStatic(boolean allowed) {
        Predicate<Constructor<?>> pred = allowed 
            ? f -> Modifier.isStatic(f.getModifiers()) 
            : f -> !Modifier.isStatic(f.getModifiers());
        this.stream = this.stream.filter(pred);
        return this;
    }

    public KelasConstructors areFinal(boolean allowed) {
        Predicate<Constructor<?>> pred = allowed 
            ? f -> Modifier.isFinal(f.getModifiers()) 
            : f -> !Modifier.isFinal(f.getModifiers());
        this.stream = this.stream.filter(pred);
        return this;
    }

    // For edge cases that require OR operations
    public KelasConstructors filter(Predicate<Constructor<?>> pred) {
        this.stream = this.stream.filter(pred);
        return this;
    }

    /*
    * Have methods
    */
    /**
     * Filter fields with name
     * @param name the name to check
     * @return KelasConstructors object to chain
     */
    public KelasConstructors haveName(String name) {
        this.stream = this.stream.filter(f -> f.getName().equals(name));
        return this;
    }

    /*
     * Terminal operations
     */
    /**
     * Terminal operation. 
     * Collects the fields into a List.
     * @return List of fields
     */
    public List<Constructor<?>> toList() {
        return this.stream.collect(Collectors.toList());
    }

    /**
     * Terminal operation. 
     * Count the number of fields.
     * @return Number of fields
     */
    public int count() {
        return (int)this.stream.count();
    }

    /**
     * Terminal operation. 
     * Count if number of fields equals number
     * @return Number of fields
     */
    public boolean countEquals(int number) {
        return count() == number;
    }


    /**
     * Terminal operation. 
     * Returns true if fields are absent. Returns false otherwise.
     */
    public boolean areAbsent() {
        return count() == 0;
    }

    /**
     * Terminal operation. 
     * Returns true if fields are present. Returns false otherwise.
     */
    public boolean arePresent() {
        return !areAbsent();
    }

    @Override
    public String toString() {
        return this.stream.reduce("", (str, field) -> str + field.toString() + "\n", (str, field) -> str);
    }

}
