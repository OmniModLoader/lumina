package org.omnimc.lumina.mmp.file;

import org.omnimc.lumina.mmp.file.data.ClassPatch;
import org.omnimc.lumina.mmp.file.data.FieldPatch;
import org.omnimc.lumina.mmp.file.data.MethodPatch;
import org.omnimc.lumina.mmp.file.data.ParameterPatch;
import org.omnimc.lumina.mmp.namespace.Namespace;
import org.omnimc.lumina.util.patch.Result;

import java.io.*;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public final class PatchParser {

    private static final Pattern CLASS_RENAME = Pattern.compile("^([\\w./$]+)\\s+(\\w+)$");
    private static final Pattern FIELD_RENAME = Pattern.compile("^([\\w./$]+)#(\\w+):([^\\s]+)\\s+(\\w+)$");
    private static final Pattern METHOD_RENAME = Pattern.compile("^([\\w./$]+)#(\\w+)\\(([^)]*)\\)(\\S*)\\s+(\\w+)$");
    private static final Pattern METHOD_PARAM_RENAME = Pattern.compile("^([\\w./$]+)#(\\w+)\\(([^)]*)\\)(\\S*)\\s+(\\d+)\\s+(\\w+)$");

    public static PatchData parse(InputStream inputStream) throws IOException {
        PatchData patchData = new PatchData();

        loopThroughInput(inputStream, line -> {
            String trimmedLine = line.trim();

            if (trimmedLine.isEmpty()) {
                return Result.FAILURE;
            }

            if (trimmedLine.contains("#")) {
                trimmedLine = trimmedLine.substring(0, trimmedLine.indexOf("#"));
                if (trimmedLine.isEmpty()) {
                    return Result.SKIP;
                }
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                String namespace = line.substring(1, line.length() - 1);
                patchData.setNamespace(new Namespace());
                return Result.SKIP;
            }

            Matcher methodParamMatcher = METHOD_PARAM_RENAME.matcher(trimmedLine);
            if (methodParamMatcher.matches()) {
                List<ParameterPatch> methodPatches = patchData.getParameterPatches();
                methodPatches.add(new ParameterPatch(methodParamMatcher.group(1),
                                                     methodParamMatcher.group(2),
                                                     "(" + methodParamMatcher.group(3) + ")" + methodParamMatcher.group(4),
                                                     Integer.parseInt(methodParamMatcher.group(5)),
                                                     methodParamMatcher.group(6)));
                return Result.SKIP;
            }

            Matcher methodMatcher = METHOD_RENAME.matcher(line);
            if (methodMatcher.matches()) {
                patchData.getMethodPatches().add(new MethodPatch(
                        methodMatcher.group(1),
                        methodMatcher.group(2),
                        "(" + methodMatcher.group(3) + ")" + methodMatcher.group(4),
                        methodMatcher.group(5)
                ));
                return Result.SKIP;
            }

            Matcher fieldMatcher = FIELD_RENAME.matcher(line);
            if (fieldMatcher.matches()) {
                patchData.getFieldPatches().add(new FieldPatch(
                        fieldMatcher.group(1),
                        fieldMatcher.group(2),
                        fieldMatcher.group(3),
                        fieldMatcher.group(4)
                ));
                return Result.SKIP;
            }

            Matcher classMatcher = CLASS_RENAME.matcher(line);
            if (classMatcher.matches()) {
                patchData.getClassPatches().add(new ClassPatch(
                        classMatcher.group(1),
                        classMatcher.group(2)
                ));
                return Result.SKIP;
            }

            return Result.FAILURE;
        });
        return patchData;
    }

    private static void loopThroughInput(InputStream inputStream, Function<String, Result> function) {
        try {
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                 LineNumberReader reader = new LineNumberReader(new InputStreamReader(bufferedInputStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    Result result = function.apply(line);
                    if (result == null) {
                        break;
                    }

                    if (result.equals(Result.FAILURE) || result.equals(Result.SUCCESS)) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read inputstream.", e);
        }
    }

}