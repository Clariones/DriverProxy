package pkg;

import java.io.File;
import java.io.FileReader;

import org.skynet.bgby.deviceconfig.DeviceConfigManagerPCImpl;
import org.skynet.bgby.devicedriver.DeviceDriverManagerPCImpl;
import org.skynet.bgby.deviceprofile.DeviceProfileManagerPCImpl;
import org.skynet.bgby.devicestatus.DeviceStatusManagerPCImpl;
import org.skynet.bgby.driverproxy.DriverProxyConfiguration;
import org.skynet.bgby.driverproxy.DriverProxyService;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.driverutils.Logger4PC;
import org.skynet.bgby.layout.LayoutManagerPCImpl;

import com.google.gson.Gson;

public class TestDriverProxyService {

	public static void main(String[] args) throws Exception {
		DriverUtils.setLogger(new Logger4PC());

		File cfgFile = new File("testInput/testCfg.json");
		FileReader reader = new FileReader(cfgFile);
		DriverProxyConfiguration config = new Gson().fromJson(reader, DriverProxyConfiguration.class);

		DriverProxyService service = new DriverProxyService();
		service.setConfig(config);

		// TODO
		DeviceProfileManagerPCImpl profileManager = new DeviceProfileManagerPCImpl();
		profileManager.setBaseFolder(new File("testInput/deviceProfile"));
		service.setDeviceProfileManager(profileManager);
		// TODO
		DeviceConfigManagerPCImpl configManager = new DeviceConfigManagerPCImpl();
		configManager.setBaseFolder(new File("testInput/deviceInfo"));
		service.setDeviceConfigManager(configManager);
		// TODO
		LayoutManagerPCImpl layoutManager = new LayoutManagerPCImpl();
		layoutManager.setBaseFolder(new File("testInput/controllerLayout"));
		service.setLayoutManager(layoutManager);
		// TODO
		DeviceDriverManagerPCImpl driverManager = new DeviceDriverManagerPCImpl();
		driverManager.setBaseFolder(new File("testInput/driverInfo"));
		service.setDriverManager(driverManager);
		// TODO
		DeviceStatusManagerPCImpl statusManager = new DeviceStatusManagerPCImpl();
		statusManager.setBaseFolder(new File("testOuput/devieStatus"));
		service.setDeviceStatusManager(statusManager);

		TestReporter reporter = new TestReporter();
		service.setReporter(reporter);
		service.start();
	}

}
