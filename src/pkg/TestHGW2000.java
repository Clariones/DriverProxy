package pkg;

import java.io.IOException;

import org.skynet.bgby.devicedriver.honeywell.Configuration;
import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestHGW2000 {
	
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public static void main(String[] args) {
		HGW2000Controller driver = new HGW2000Controller();
		
		Configuration configuration = new Configuration();
		configuration.setHostIPAddress("192.168.0.40");
		configuration.setPassword("123456");
		configuration.setPort(10099);
		configuration.setUsername("admin");
		driver.setConfiguration(configuration);
		
		try {
//			ExecutionResult result = driver.controlLight(1, 6, 0, 100);
			ExecutionResult result = driver.controlHBusLight(1, 2, 4, 0, 100);
			System.out.println(gson.toJson(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
