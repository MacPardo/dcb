package dcb.core.runner;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AddressVerifier {
    /*
    https://stackoverflow.com/questions/2406341/how-to-check-if-an-ip-address-is-the-local-host-on-a-multi-homed-system
     */
    public static boolean isAddressLocal(String address) {
        try {
            InetAddress inetAddress = InetAddress.getByName(address);
            if (inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress()) {
                return true;
            }
            return NetworkInterface.getByInetAddress(inetAddress) != null;
        } catch (UnknownHostException | SocketException e) {
            return false;
        }

    }
}
