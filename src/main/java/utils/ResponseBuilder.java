package utils;

import java.io.IOException;

public class ResponseBuilder {
    public static String buildBulkStringArray(String... strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(strings.length).append("\r\n");

        for (String string : strings) {
            sb.append("$")
                    .append(string.length())
                    .append("\r\n")
                    .append(string)
                    .append("\r\n");
        }

        return sb.toString();
    }

    public static String buildBulkString(String string)  {
        int length = string.length();
        return "$" + length + "\r\n" + string + "\r\n";
    }

    public static String buildNullBulkString() {
        return "$-1\r\n";
    }
}
