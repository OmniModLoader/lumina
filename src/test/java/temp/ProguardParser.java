package temp;

import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.consumer.FailedState;
import org.omnimc.lumina.serialization.LineSerializer;

import java.util.function.Consumer;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class ProguardParser implements LineSerializer {

    private String parentClass;

    /**
     * Processes a class member mapping and updates the {@linkplain Mappings}.
     *
     * @param parentClass the parent class name.
     * @param line        the line of class member mapping data.
     * @param container   the {@linkplain Mappings} to be updated with the parsed data.
     */
    private void processClassMemberMapping(String parentClass, String line, Mappings container) {
        int colonIndex1 = line.indexOf(':');
        int colonIndex2 = colonIndex1 < 0 ? -1 : line.indexOf(':', colonIndex1 + 1);
        int spaceIndex = line.indexOf(' ', colonIndex2 + 2);
        int argumentIndex1 = line.indexOf('(', spaceIndex + 1);
        int argumentIndex2 = argumentIndex1 < 0 ? -1 : line.indexOf(')', argumentIndex1 + 1);
        int colonIndex3 = argumentIndex2 < 0 ? -1 : line.indexOf(':', argumentIndex2 + 1);
        int colonIndex4 = colonIndex3 < 0 ? -1 : line.indexOf(':', colonIndex3 + 1);
        int arrowIndex = line.indexOf("->", (colonIndex4 >= 0 ? colonIndex4 : colonIndex3 >= 0 ? colonIndex3 : argumentIndex2 >= 0 ? argumentIndex2 : spaceIndex) + 1);

        if (spaceIndex < 0 || arrowIndex < 0) {
            return;
        }

        // Extract the elements.
        String type = line.substring(colonIndex2 + 1, spaceIndex).trim().replace(".", "/");
        String name = line.substring(spaceIndex + 1, argumentIndex1 >= 0 ? argumentIndex1 : arrowIndex).trim();
        String newName = line.substring(arrowIndex + 2).trim();

        // Does the method name contain an explicit original class name?
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex >= 0) {
            parentClass = name.substring(0, dotIndex);

            name = name.substring(dotIndex + 1);
        }

        // Process this class member mapping.
        if (!type.isEmpty() && !name.isEmpty() && !newName.isEmpty()) {
            // Is it a field or a method?
            if (argumentIndex2 < 0) {
                // Field found

                String formatedTypes = this.primitiveTypes(type);

                container.addField(parentClass, newName + formatedTypes, name);
            } else {
                String replace = line.substring(argumentIndex1 + 1, argumentIndex2).trim().replace(".", "/");
                String arguments = this.methodFormat(replace);
                String formatedType = this.primitiveTypes(type);

                container.addMethod(parentClass, newName + arguments + formatedType, name);
            }
        }
    }

    /**
     * Processes a class mapping and updates the {@linkplain Mappings}.
     *
     * @param line      the line of class mapping data.
     * @param container the {@linkplain Mappings} to be updated with the parsed data.
     * @return the new class name if mapping was successful, otherwise {@code null}.
     */
    private String processClassMapping(String line, Mappings container) {
        int arrowIndex = line.indexOf("->");
        if (arrowIndex < 0) {
            return null;
        }

        int colonIndex = line.indexOf(':', arrowIndex + 2);
        if (colonIndex < 0) {
            return null;
        }

        // Extract the elements.
        String className = line.substring(0, arrowIndex).trim().replace(".", "/");
        String newClassName = line.substring(arrowIndex + 2, colonIndex).trim().replace(".", "/");

        container.addClass(newClassName, className);

        return newClassName;
    }

    /**
     * Formats the method arguments.
     *
     * @param arguments the arguments to be formatted.
     * @return the formatted arguments as a string.
     */
    private String methodFormat(String arguments) {
        return "(" + method(arguments) + ")";
    }

    /**
     * Converts the method arguments into their primitive type representations.
     *
     * @param arguments the arguments to be converted.
     * @return the converted arguments as a string.
     */
    private String method(String arguments) {
        if (arguments.isEmpty()) {
            return arguments;
        }

        String[] split = arguments.split(",");

        StringBuilder returnBuilder = new StringBuilder();
        for (String s : split) {
            returnBuilder.append(primitiveTypes(s));
        }

        return returnBuilder.toString();
    }

    /**
     * Converts primitive types to their respective representations.
     *
     * @param input the input type to be converted.
     * @return the converted type as a string.
     */
    private String primitiveTypes(String input) {
        StringBuilder brackets = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c == '[') {
                brackets.append("[");
                continue;
            }

            if (c == ']') {
                input = input.replaceFirst("\\[]", "");
            }
        }

        switch (input) { // I am so sorry JitPack wouldn't let me do it the clean way for some reason...
            case "boolean" -> {
                return brackets + "Z";
            }
            case "byte" -> {
                return brackets + "B";
            }
            case "char" -> {
                return brackets + "C";
            }
            case "double" -> {
                return brackets + "D";
            }
            case "float" -> {
                return brackets + "F";
            }
            case "int" -> {
                return brackets + "I";
            }
            case "long" -> {
                return brackets + "J";
            }
            case "void" -> {
                return "V";
            }
            default -> {
                return brackets + "L" + input + ";";
            }
        }
    }

    @Override
    public boolean serializeLine(String line, Mappings mappings, Consumer<FailedState> consumer) {
        if (line.isEmpty()) {
            return false;
        }

        String trimmedEntry = line.trim();

        if (trimmedEntry.contains("#")) {
            trimmedEntry = trimmedEntry.substring(0, trimmedEntry.indexOf("#"));
            if (trimmedEntry.isEmpty()) {
                return false;
            }
        }

        /* Checking if it's a parentClass */
        if (trimmedEntry.charAt(trimmedEntry.length() - 1) == ':') {
            String currentClassName1 = processClassMapping(trimmedEntry, mappings);
            if (currentClassName1 == null) {
                return false;
            }

            parentClass = currentClassName1;
            return true;
        }

        if (parentClass != null) {
            processClassMemberMapping(parentClass, trimmedEntry, mappings);
        }
        return true;
    }
}