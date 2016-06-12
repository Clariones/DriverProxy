package pkg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.restserver.IRestRequestHandler;

public class FileDownloadHandler implements IRestRequestHandler{
	public static final String CMD = "download";
	public static final String TARGET = "file";
	@Override
	public boolean handleCommand(IRestRequest restRequest, IHttpResponse restResponse) {
		if (!restRequest.getCommand().equals(CMD) || !restRequest.getTarget().equals(TARGET)){
			return false;
		}
		String fileName = "C:/Users/Clariones/Desktop/巴国布衣项目/01.例子-普通空调规格.txt";
		FileInputStream fIn;
		try {
			fIn = new FileInputStream(fileName);
			restResponse.setInputStream(fIn);
//			fIn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
