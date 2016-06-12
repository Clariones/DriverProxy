package pkg;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.RestRequestImpl;
import org.skynet.bgby.protocol.restmanagecommand.CmdHowAreYou;
import org.skynet.bgby.restserver.IRestClientCallback;
import org.skynet.bgby.restserver.RestClientBaseImpl;

public class TestHttpClient {

	public static void main(String[] args) {
		MyHttpClient client = new MyHttpClient();
		InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 8981);
		String contentRoot = null;
		IRestRequest request = new RestRequestImpl();
//		request.setCommand(CmdHowAreYou.CMD);
//		request.setTarget("Baby");
		request.setCommand("download");
		request.setTarget("file");
		try {
			IHttpResponse response = client.synchRequest(serverAddress, contentRoot, request);
			System.out.println(response.getStatus().getDescription());
			System.out.println(response.getMimeType());
			System.out.println(response.getAsString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

class MyHttpClient extends RestClientBaseImpl{

	@Override
	public void asynchRequest(InetSocketAddress serverAddress, String contentRoot, IRestRequest request,
			IRestClientCallback callback) {
		// TODO Auto-generated method stub
		
	}
	
}