package pkg;

import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.driverutils.Logger4PC;
import org.skynet.bgby.listeningserver.ListeningServerException;

public class UdpTest {

	public static void main(String[] args) {
		DriverUtils.setLogger(new Logger4PC());
		
		UdpApp service = new UdpApp();
		service.setListeningAddress("224.0.0.4");
		service.setListeningPort(4006);
		service.setDamon(false);
		
		try {
			service.start();
		} catch (ListeningServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
