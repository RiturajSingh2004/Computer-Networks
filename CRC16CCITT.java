import java.util.Scanner;

public class CRC16CCITT {
    private static final int POLY = 0x1021;
    private static final int INITIAL_VALUE = 0xFFFF;

    public static int calculateCRC(byte[] data) {
        int crc = INITIAL_VALUE;

        for (byte b : data) {
            crc ^= (b << 8) & 0xFFFF;

            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = ((crc << 1) ^ POLY) & 0xFFFF;
                } else {
                    crc = (crc << 1) & 0xFFFF;
                }
            }
        }
        return crc & 0xFFFF;
    }

    public static byte[] binaryStringToBytes(String binaryStr) {
        int len = binaryStr.length();
        int byteLen = (len + 7) / 8;
        byte[] bytes = new byte[byteLen];

        String padded = String.format("%" + (byteLen * 8) + "s", binaryStr).replace(' ', '0');

        for (int i = 0; i < byteLen; i++) {
            String byteStr = padded.substring(i * 8, (i + 1) * 8);
            bytes[i] = (byte) Integer.parseInt(byteStr, 2);
        }
        return bytes;
    }

    // Append CRC bytes (2 bytes, high byte first) to original data bytes
    public static byte[] appendCRC(byte[] data, int crc) {
        byte[] result = new byte[data.length + 2];
        System.arraycopy(data, 0, result, 0, data.length);
        // CRC high byte
        result[data.length] = (byte) ((crc >> 8) & 0xFF);
        // CRC low byte
        result[data.length + 1] = (byte) (crc & 0xFF);
        return result;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select mode:");
        System.out.println("1 - Calculate CRC-CCITT (16-bit)");
        System.out.println("2 - Verify data with given CRC");
        System.out.print("Enter choice (1 or 2): ");
        int mode = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (mode == 1) {
            // Calculate CRC
            System.out.println("Select input type:");
            System.out.println("1 - Text String");
            System.out.println("2 - Binary String (e.g. 11001010)");
            System.out.print("Enter choice (1 or 2): ");
            int inputType = scanner.nextInt();
            scanner.nextLine();

            byte[] dataBytes;

            switch (inputType) {
                case 1 -> {
                    System.out.print("Enter text string: ");
                    String textInput = scanner.nextLine();
                    dataBytes = textInput.getBytes();
                }
                case 2 -> {
                    System.out.print("Enter binary string (only 0s and 1s): ");
                    String binaryInput = scanner.nextLine();
                    if (!binaryInput.matches("[01]+")) {
                        System.out.println("Invalid binary string. Exiting.");
                        scanner.close();
                        return;
                    }
                    dataBytes = binaryStringToBytes(binaryInput);
                }
                default -> {
                    System.out.println("Invalid choice. Exiting.");
                    scanner.close();
                    return;
                }
            }

            int crc = calculateCRC(dataBytes);
            System.out.printf("CRC-CCITT (16-bit) = 0x%04X\n", crc);

        } else if (mode == 2) {
            // Verify mode
            System.out.println("Select input type:");
            System.out.println("1 - Text String");
            System.out.println("2 - Binary String (e.g. 11001010)");
            System.out.print("Enter choice (1 or 2): ");
            int inputType = scanner.nextInt();
            scanner.nextLine();

            byte[] dataBytes;

            switch (inputType) {
                case 1 -> {
                    System.out.print("Enter text string: ");
                    String textInput = scanner.nextLine();
                    dataBytes = textInput.getBytes();
                }
                case 2 -> {
                    System.out.print("Enter binary string (only 0s and 1s): ");
                    String binaryInput = scanner.nextLine();
                    if (!binaryInput.matches("[01]+")) {
                        System.out.println("Invalid binary string. Exiting.");
                        scanner.close();
                        return;
                    }
                    dataBytes = binaryStringToBytes(binaryInput);
                }
                default -> {
                    System.out.println("Invalid choice. Exiting.");
                    scanner.close();
                    return;
                }
            }

            System.out.print("Enter CRC-CCITT (16-bit) code in hex (e.g. 29B1): ");
            String crcHex = scanner.nextLine();

            int givenCRC;
            try {
                givenCRC = Integer.parseInt(crcHex, 16);
            } catch (NumberFormatException e) {
                System.out.println("Invalid CRC format. Exiting.");
                scanner.close();
                return;
            }

            // Append the given CRC bytes to data bytes
            byte[] dataWithCRC = appendCRC(dataBytes, givenCRC);

            // Calculate CRC of combined data+CRC
            int check = calculateCRC(dataWithCRC);

            if (check == 0) {
                System.out.println("CRC verification successful! Data is correct.");
            } else {
                System.out.println("CRC verification failed! Data is corrupted.");
                System.out.printf("Check CRC result = 0x%04X\n", check);
            }

        } else {
            System.out.println("Invalid mode selected. Exiting.");
        }

        scanner.close();
    }
}
