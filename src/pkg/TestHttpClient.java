package pkg;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.skynet.bgby.command.management.CmdGetLayout;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.RestRequestImpl;
import org.skynet.bgby.restserver.RestClientBaseImpl;

import com.google.gson.Gson;

public class TestHttpClient {

	public static void main(String[] args) {
		MyHttpClient client = new MyHttpClient();
		InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 8981);
		String contentRoot = null;
		IRestRequest request = new RestRequestImpl();
		
//		request.setCommand(CmdGetProfileByDevice.CMD);
//		request.setTarget("ctrl02");
		
		request.setCommand(CmdGetLayout.CMD);
		request.setTarget("ctrl02");
		
//		request.setCommand("download");
//		request.setTarget("file");
		
		try {
			IHttpResponse response = client.synchRequest(serverAddress, contentRoot, request);
			System.out.println(response.getStatus().getDescription());
			System.out.println(response.getMimeType());
			String str = response.getAsString();
			System.out.println(response.getAsString());
			System.out.println("the str is " + str);
			CommonData data = new Gson().fromJson(str, CommonData.class);
			System.out.println(data.getData().getClass());
//			Map<String, DeviceProfile> profiles = new HashMap<String, DeviceProfile>();
//			profiles = new Gson().fromJson(data.getData().get("profiles"), profiles.getClass());
//			System.out.println(profiles);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

class MyHttpClient extends RestClientBaseImpl{


	
}