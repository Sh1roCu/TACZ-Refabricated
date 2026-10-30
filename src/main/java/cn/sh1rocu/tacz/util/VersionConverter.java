package cn.sh1rocu.tacz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NeoForge(Maven)版本范围 -> Fabric(SemVer) 版本范围，用于转换枪包meta文件的依赖版本
 */
public final class VersionConverter {

    private VersionConverter() {
    }

    private static final Pattern SPLIT = Pattern.compile("(?<=[\\])]),\\s*(?=[\\[(])");
    private static final Pattern INTERVAL = Pattern.compile(
            "([\\[(])\\s*([^,]*?)\\s*(?:(,)\\s*([^,]*?))?\\s*([\\])])");

    public static List<String> toList(String range) {
        List<String> out = new ArrayList<>();
        for (String part : SPLIT.split(range.trim())) {
            part = part.trim();
            Matcher m = INTERVAL.matcher(part);
            if (!m.matches()) {
                out.add(">=" + part);
                continue;
            }
            String lo = m.group(2), hi = m.group(4);
            if (m.group(3) == null) {
                if (m.group(1).equals("[") && m.group(5).equals("]")) {
                    out.add(">=" + lo + " <=" + lo);
                } else {
                    out.add(lo);
                }
                continue;
            }
            List<String> conds = new ArrayList<>();
            if (!lo.isEmpty()) conds.add((m.group(1).equals("[") ? ">=" : ">") + lo);
            if (!hi.isEmpty()) conds.add((m.group(5).equals("]") ? "<=" : "<") + hi);
            out.add(conds.isEmpty() ? "*" : String.join(" ", conds));
        }
        return out;
    }

    public static String toString(String range) {
        return String.join(" ", toList(range));
    }

}