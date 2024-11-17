package store;

import model.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalStore {
    private static final GlobalStore INSTANCE = new GlobalStore();

    private static final ConcurrentHashMap<String, Data> dataStore = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> config = new ConcurrentHashMap<>();

    final static int AUX = 0xFA;
    final static int RESIZEDB = 0xFB;
    final static int EXPIRETIMEMS = 0xFC;
    final static int EXPIRETIME = 0xFD;
    final static int SELECTDB = 0xFE;
    final static int EOF = 0xFF;

    final static int MASK_2_BITS = 0b11000000; // Mask to extract the first 2 bits
    final static int FORMAT_00 = 0b00000000;
    final static int FORMAT_01 = 0b01000000;
    final static int FORMAT_10 = 0b10000000;
    final static int FORMAT_11 = 0b11000000;

    private GlobalStore() {}

    public static GlobalStore getInstance() {
        return INSTANCE;
    }

    public ConcurrentHashMap<String, Data> getDataStore() {
        return dataStore;
    }

    public ConcurrentHashMap<String, String> getConfig() {
        return config;
    }

    public static void buildDataStore() {
        String dir = config.get("dir");
        String dbFileName = config.get("dbFileName");

        if (dir == null || dbFileName == null) {
            return;
        }

        try (InputStream inputStream = new FileInputStream(new File(dir, dbFileName))) {
            showRedisVersion(inputStream);
            processMetadata(inputStream);
            processKeyValues(inputStream);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void processKeyValues(InputStream inputStream) throws IOException {
        // Read the control byte
        int controlByte = inputStream.read();
        if (controlByte == -1) {
            throw new IOException("Unexpected end of input stream.");
        }

        // Read the length byte
        int lengthByte = inputStream.read();
        if (lengthByte == -1) {
            throw new IOException("Unexpected end of input stream while reading length byte.");
        }

        int strLength = lengthEncoding(inputStream, lengthByte);
        if (strLength == 0) {
            int nextByte = inputStream.read();
            if (nextByte == -1) {
                throw new IOException("Unexpected end of input stream while reading fallback length.");
            }
            strLength = nextByte; // Use fallback byte for length
        }

        // Read the key string
        byte[] keyBytes = inputStream.readNBytes(strLength);
        if (keyBytes.length < strLength) {
            throw new IOException("Incomplete key read: Expected " + strLength + " bytes but got " + keyBytes.length);
        }
        String key = new String(keyBytes);

        int valueLength = lengthEncoding(inputStream, lengthByte);
        if (valueLength == 0) {
            int nextByte = inputStream.read();
            if (nextByte == -1) {
                throw new IOException("Unexpected end of input stream while reading fallback length.");
            }
            valueLength = nextByte; // Use fallback byte for length
        }
        // Read the value string
        byte[] valueBytes = inputStream.readNBytes(valueLength);
        if (valueBytes.length < valueLength) {
            throw new IOException("Incomplete key read: Expected " + valueLength + " bytes but got " + keyBytes.length);
        }
        String value = new String(valueBytes);

        System.out.println("key read in is: " + key);
        System.out.println("value read in is: " + value);
        Data newData = new Data(value);
        dataStore.put(key, newData);
    }

    private static void processMetadata(InputStream inputStream) throws IOException {
        int bytes;

        while ((bytes = inputStream.read()) != -1) {
            if (bytes == RESIZEDB) {
                System.out.println("RESIZEDB");
                bytes = inputStream.read();
                inputStream.readNBytes(lengthEncoding(inputStream, bytes));
                inputStream.readNBytes(lengthEncoding(inputStream, bytes));
                break;
            }

            switch (bytes) {
                case AUX:
                    System.out.println("AUX");
                    break;
                case EXPIRETIMEMS:
                    System.out.println("EXPIRETIMEMS");
                    break;
                case EXPIRETIME:
                    System.out.println("EXPIRETIME");
                    break;
                case SELECTDB:
                    System.out.println("SELECTDB");
                    break;
                case EOF:
                    System.out.println("EOF");
                    break;
            }
        }
    }

    private static void showRedisVersion(InputStream inputStream) throws IOException {
        byte[] redis = new byte[5];
        byte[] version = new byte[4];
        inputStream.read(redis);
        inputStream.read(version);

        // Should probably stop processing if the magic string is wrong
        System.out.printf(
                "Magic string + version number: %s%s\n",
                new String(redis, StandardCharsets.UTF_8),
                new String(version, StandardCharsets.UTF_8)
        );
    }

    private static int lengthEncoding(InputStream inputStream, int bytes) throws IOException {
        int first2bits = bytes & MASK_2_BITS;

        switch (first2bits) {
            case FORMAT_00:
                System.out.println("00");
                return 0;

            case FORMAT_01:
                System.out.println("01");
                return 2;

            case FORMAT_10:
                System.out.println("10");
                byte[] lengthBytes = inputStream.readNBytes(Integer.BYTES);
                if (lengthBytes.length < Integer.BYTES) {
                    throw new IOException("Unexpected end of stream when reading length.");
                }
                ByteBuffer buffer = ByteBuffer.wrap(lengthBytes);
                return 1 + buffer.getInt();

            case FORMAT_11:
                System.out.println("11");
                return 1; // Special format

            default:
                throw new IllegalArgumentException("Invalid encoding format: " + first2bits);
        }
    }

}