package pkg;

import org.skynet.bgby.driverproxy.LayoutConfigManager;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.driverutils.Logger4PC;
import org.skynet.bgby.protocol.RestRequestCodec;
import org.skynet.bgby.restserver.RestManageCommandHandler;
import org.skynet.bgby.restserver.RestService;

import fi.iki.elonen.NanoHTTPD;

public class TestHttpRestService {

	public static void main(String[] args) throws Exception {
		DriverUtils.setLogger(new Logger4PC());
		// TODO Auto-generated method stub
		// layout related
		LayoutConfigManager layoutManager = new LayoutConfigManager();
		
		RestService service = new RestService();
		RestRequestCodec restProtocolHandler = new RestRequestCodec();
		service.setRestProtocolHandler(restProtocolHandler);
		RestManageCommandHandler handler1 = new RestManageCommandHandler();
		handler1.setLayoutManager(layoutManager);
		handler1.initHandlers();
		service.registerCommandHandler(handler1);
		service.registerCommandHandler(new FileDownloadHandler());
		service.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}

}
