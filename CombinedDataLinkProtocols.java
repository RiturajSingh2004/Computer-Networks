import java.util.Random;
import java.util.Scanner;

public class CombinedDataLinkProtocols {

    private static final int POLY = 0x1021;
    private static final int INITIAL_VALUE = 0xFFFF;

    // --- CRC-CCITT calculation (for completeness, if you want to integrate error detection later) ---
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

    // --- Simple Protocol ---
    public static class SimpleProtocol {
        public void sendFrame(String frame) throws InterruptedException {
            System.out.println("[SimpleProtocol] Sender: Sending frame -> " + frame);
            Thread.sleep(1000); // Simulate transmission delay
            System.out.println("[SimpleProtocol] Receiver: ACK received for frame");
            System.out.println("[SimpleProtocol] Sender: ACK received, sending next frame...\n");
        }
    }

    // --- Stop and Wait Protocol ---
    public static class StopAndWaitProtocol {
        private static final Random random = new Random();
        private static final double LOSS_PROBABILITY = 0.3;

        private int seqNum = 0;

        public void sendData(String data) throws InterruptedException {
            boolean ackReceived = false;
            int attempts = 0;

            while (!ackReceived) {
                attempts++;
                System.out.println("[StopAndWait] Sender: Attempt " + attempts + " to send frame #" + seqNum);
                boolean frameSent = sendFrame(seqNum, data);

                if (!frameSent) {
                    System.out.println("[StopAndWait] Sender: Frame lost, retransmitting...");
                    Thread.sleep(1000);
                    continue;
                }

                // Wait for ACK (simulate delay)
                Thread.sleep(500);

                boolean ackDelivered = sendAck(seqNum);

                if (ackDelivered) {
                    System.out.println("[StopAndWait] Sender: ACK received for frame #" + seqNum + "\n");
                    ackReceived = true;
                    seqNum = 1 - seqNum;
                } else {
                    System.out.println("[StopAndWait] Sender: ACK lost, retransmitting frame #" + seqNum);
                    Thread.sleep(1000);
                }
            }
        }

        private boolean sendFrame(int seqNum, String data) {
            System.out.println("[StopAndWait] Sender: Sending frame #" + seqNum + " with data: " + data);
            boolean lost = random.nextDouble() < LOSS_PROBABILITY;
            if (lost) {
                System.out.println("[StopAndWait] Channel: Frame #" + seqNum + " LOST in transit");
                return false;
            }
            System.out.println("[StopAndWait] Receiver: Frame #" + seqNum + " received correctly");
            return true;
        }

        private boolean sendAck(int seqNum) {
            boolean lost = random.nextDouble() < LOSS_PROBABILITY;
            if (lost) {
                System.out.println("[StopAndWait] Channel: ACK #" + seqNum + " LOST in transit");
                return false;
            }
            System.out.println("[StopAndWait] Receiver: ACK #" + seqNum + " sent");
            return true;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose protocol to run:");
        System.out.println("1 - Simple Protocol");
        System.out.println("2 - Stop-and-Wait Protocol");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String[] messages = {
                "Frame 1: Hello",
                "Frame 2: World",
                "Frame 3: Stop-and-Wait",
                "Frame 4: Protocol"
        };

        switch (choice) {
            case 1:
                SimpleProtocol simple = new SimpleProtocol();
                for (String msg : messages) {
                    simple.sendFrame(msg);
                }
                break;

            case 2:
                StopAndWaitProtocol stopAndWait = new StopAndWaitProtocol();
                for (String msg : messages) {
                    stopAndWait.sendData(msg);
                }
                break;

            default:
                System.out.println("Invalid choice.");
        }

        scanner.close();
    }
}
