package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// *<number-of-elements>\r\n<element-1>...<element-n>
// example: *2\r\n$5\r\nhello\r\n$5\r\nworld\r\n
public class Parser {
    private static final char ARRAY_PREFIX = '*';
    private static final char BULK_STRING_PREFIX = '$';
    private static final char INTEGER_PREFIX = ':';

    public static List<Object> parseCommand(BufferedReader input) {
        try {
            String firstLine = input.readLine();
            if (firstLine == null) {
                return null;
            }

            validateArrayPrefix(firstLine);
            int numberOfElements = Integer.parseInt(firstLine.substring(1));
            List<Object> command = new ArrayList<>(numberOfElements);

            // skipping length of the upcoming string which is the command
            input.readLine();

            // The command
            command.add(input.readLine());

            parseRemainingElements(input, command, numberOfElements - 1);

            return command;

        } catch (IOException e) {
            throw new RedisProtocolException("Failed to read from input", e);
        } catch (NumberFormatException e) {
            throw new RedisProtocolException("Invalid number format in protocol", e);
        }
    }

    private static void validateArrayPrefix(String line) throws RedisProtocolException {
        if (line.isEmpty() || line.charAt(0) != ARRAY_PREFIX) {
            throw new RedisProtocolException("Expected array prefix '*', got: " +
                    (line.isEmpty() ? "empty line" : line.charAt(0)));
        }
    }

    private static void parseBulkString(BufferedReader input, List<Object> command, String lengthLine)
            throws IOException, RedisProtocolException {
        command.add(lengthLine);  // Add the length line
        String value = input.readLine();
        if (value == null) {
            throw new RedisProtocolException("Unexpected end of input while reading bulk string");
        }
        command.add(value);
    }

    private static void parseInteger(List<Object> command, String line) throws RedisProtocolException {
        try {
            command.add(String.valueOf(INTEGER_PREFIX));
            command.add(Integer.parseInt(line.substring(1)));
        } catch (NumberFormatException e) {
            throw new RedisProtocolException("Invalid integer format: " + line.substring(1));
        }
    }

    private static void parseRemainingElements(BufferedReader input, List<Object> command, int remaining)
            throws IOException, RedisProtocolException {
        for (int i = 0; i < remaining; i++) {
            String typeLine = input.readLine();
            if (typeLine == null || typeLine.isEmpty()) {
                throw new RedisProtocolException("Unexpected end of input");
            }

            char type = typeLine.charAt(0);
            switch (type) {
                case BULK_STRING_PREFIX -> parseBulkString(input, command, typeLine);
                case INTEGER_PREFIX -> parseInteger(command, typeLine);
                default -> throw new RedisProtocolException("Unsupported type prefix: " + type);
            }
        }
    }
}

class RedisProtocolException extends RuntimeException {
    public RedisProtocolException(String message) {
        super(message);
    }

    public RedisProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
