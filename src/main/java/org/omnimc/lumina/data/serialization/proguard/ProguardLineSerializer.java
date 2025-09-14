package org.omnimc.lumina.data.serialization.proguard;

import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.serialization.LineSerializer;
import org.omnimc.lumina.data.types.ClassData;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class ProguardLineSerializer implements LineSerializer {

    private String previousClass;

    private String obfuscatedName;
    private ClassData currentClassData;

    @Override
    public boolean serialize(String line, Mappings mappings) {
        String trimmedEntry = setupLine(line);

        if (trimmedEntry == null) {
            return false;
        }

        if (trimmedEntry.isEmpty()) {
            return true; // Skipping to next line.
        }

        /* Checking if it's a parentClass */
        if (trimmedEntry.endsWith(":")) {
            String currentClassName1 = processClassMapping(trimmedEntry, mappings);
            if (currentClassName1 == null) {
                return false;
            }

            obfuscatedName = currentClassName1;
            return true;
        }

        if (obfuscatedName != null) {
            parseClassMemberMapping(trimmedEntry, false, false);
        }
        return true;
    }

    @Override
    public boolean serializeFields(String line, ClassData classData) {
        String trimmedEntry = setupIndividualLine(line, classData);

        if (trimmedEntry == null) {
            return false;
        }

        if (trimmedEntry.isEmpty()) {
            return true;
        }

        return parseClassMemberMapping(trimmedEntry, false, true);
    }

    @Override
    public boolean serializeMethods(String line, ClassData classData) {
        String trimmedEntry = setupIndividualLine(line, classData);

        if (trimmedEntry == null) {
            return false;
        }

        if (trimmedEntry.isEmpty()) {
            return true;
        }

        return parseClassMemberMapping(trimmedEntry, true, false);
    }

    private boolean parseClassMemberMapping(String line, boolean ignoreField, boolean ignoreMethod) {
        int arrow = line.indexOf("->");
        int firstSpace = line.indexOf(" ");
        if (arrow < 0 || firstSpace < 0) {
            return false;
        }

        String type = line.substring(0, firstSpace).trim().replace(".", "/");
        String unObfuscatedName = line.substring(firstSpace + 1, arrow).trim();
        String obfuscatedName = line.substring(arrow + 2).trim();

        if (!ignoreField) {
            if (parseFieldMember(obfuscatedName, unObfuscatedName, type)) {
                return true;
            }
        }

        if (!ignoreMethod) {
            return parseMethodMember(obfuscatedName, unObfuscatedName, type);
        }
        return false;
    }

    private boolean parseFieldMember(String obfuscatedName, String unObfuscatedName, String type) {
        if (unObfuscatedName.isEmpty()) {
            return false;
        }

        int paramStart = unObfuscatedName.indexOf("(");
        if (paramStart > 0) {
            return false;
        }

        currentClassData.addField(obfuscatedName, unObfuscatedName, primitiveTypes(type));
        return true;
    }

    private boolean parseMethodMember(String obfuscatedName, String unObfuscatedName, String type) {
        if (unObfuscatedName.isEmpty()) {
            return false;
        }

        int paramStart = unObfuscatedName.indexOf("(");
        if (paramStart < 0) {
            return false;
        }

        String methodName = unObfuscatedName.substring(0, paramStart).trim();
        String paramList = unObfuscatedName.substring(paramStart + 1, unObfuscatedName.indexOf(")", paramStart)).trim();
        String args = toDescription(paramList);
        String ret = primitiveTypes(type);
        currentClassData.addMethod(obfuscatedName, methodName, (args + ret).replace(".", "/"));
        return true;
    }

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

        this.currentClassData = container.addClass(newClassName, className);

        return newClassName;
    }

    private String toDescription(String arguments) {
        if (arguments.isEmpty()) {
            return "(" + arguments + ")";
        }

        String[] split = arguments.split(",");

        StringBuilder desc = new StringBuilder("(");
        for (String s : split) {
            desc.append(primitiveTypes(s));
        }
        desc.append(")");
        return desc.toString();
    }

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

        return switch (input) {
            case "boolean" -> brackets + "Z";
            case "byte" -> brackets + "B";
            case "char" -> brackets + "C";
            case "double" -> brackets + "D";
            case "float" -> brackets + "F";
            case "int" -> brackets + "I";
            case "long" -> brackets + "J";
            case "void" -> "V";
            default -> brackets + "L" + input + ";";
        };
    }

    private String setupLine(String line) {
        if (line.isEmpty()) {
            return null;
        }

        String trimmedEntry = line.trim();

        if (trimmedEntry.contains("#")) {
            trimmedEntry = trimmedEntry.substring(0, trimmedEntry.indexOf("#"));
            if (trimmedEntry.isEmpty()) {
                return "";
            }
        }

        if (trimmedEntry.matches("^\\d+:\\d+:.*")) {
            trimmedEntry = trimmedEntry.replaceFirst("^\\d+:\\d+:", "").trim();
        }
        return trimmedEntry;
    }

    private String setupIndividualLine(String line, ClassData classData) {
        String trimmedEntry = setupLine(line);

        if (trimmedEntry == null) {
            return null;
        }

        if (trimmedEntry.isEmpty()) {
            return ""; // Skipping to next line.
        }

        if (trimmedEntry.endsWith(":")) {
            int space = trimmedEntry.indexOf(" ");
            if (space == -1) {
                return "";
            }

            previousClass = trimmedEntry.substring(0, space).trim().replace(".", "/");
            return "";
        }

        if (!previousClass.equals(classData.getClassName())) {
            return "";
        }

        currentClassData = classData;
        return trimmedEntry;
    }
}