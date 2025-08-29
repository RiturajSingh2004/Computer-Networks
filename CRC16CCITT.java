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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the input string to calculate CRC-CCITT: ");
        String input = scanner.nextLine();

        byte[] bytes = input.getBytes();
        int crc = calculateCRC(bytes);

        System.out.printf("CRC-CCITT (16-bit) of \"%s\" = 0x%04X\n", input, crc);

        scanner.close();
    }
}
