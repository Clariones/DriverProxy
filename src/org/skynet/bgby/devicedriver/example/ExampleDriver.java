package org.skynet.bgby.devicedriver.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.devicedriver.DeviceDriverBaseImpl;
import org.skynet.bgby.devicedriver.DeviceDriverException;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestandard.SimpleSensor;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;

public class ExampleDriver extends DeviceDriverBaseImpl {
	public static final String PROFILE = "SomeCompany.SomeType.SomeSensor";
	private ExampleDriverConfiguration configuration;

	@Override
	public void initStatus(DeviceProfile profile, DeviceConfigData config, DeviceStatus device)
			throws DeviceDriverException {
		super.initStatus(profile, config, device);
	}

	@Override
	protected boolean isNeedPollingDevice(DeviceProfile profile) {
		return true;
	}

	@Override
	public void setConfig(Object cfgObject) {
		this.configuration = (ExampleDriverConfiguration) cfgObject;
	}

	@Override
	public IRestResponse onCommand(ExecutionContext ctx) throws DeviceDriverException {
		DeviceConfigData cfg = ctx.getConfig();
		double value = doQuery(DriverUtils.getAsInt(cfg.getIdentity().get("id"), -1), (String) cfg.getIdentity().get("ipAddress"));
		RestResponseImpl response = new RestResponseImpl();
		if (value < 0){
			response.setErrorCode(12345);
			response.setResult("Cannot got value after 3 times");
		}else{
			response.setData(getResult(value));
		}
		return response;
	}

	private Map<String, Object> getResult(double value) {
		Map<String, Object> result = new HashMap<>();
		result.put(SimpleSensor.TERM_MEASURE_VALUE, value);
		if (value < 30){
			result.put(SimpleSensor.TERM_MEASURE_LEVEL, SimpleSensor.TERM_MEASURE_LEVEL_GOOD);
		}else if (value < 125){
			result.put(SimpleSensor.TERM_MEASURE_LEVEL, SimpleSensor.TERM_MEASURE_LEVEL_NORMAL);
		}else{
			if (value > 1000){
				result.put(SimpleSensor.TERM_MEASURE_VALUE, value/1000);
				result.put(SimpleSensor.TERM_MEASURE_UNIT, "kug/m3");
			}
			result.put(SimpleSensor.TERM_MEASURE_LEVEL, SimpleSensor.TERM_MEASURE_LEVEL_BAD);
		}
		return result;
	}

	private double doQuery(int id, String ipaddress) throws DeviceDriverException {
		if (id <= 199){
			throw new DeviceDriverException("Example ID must >= 200");
		}
		try {
			DatagramSocket socket = new DatagramSocket();
			byte[] buf = ("$example query " + id).getBytes();
			DatagramPacket datapkg = new DatagramPacket(buf, buf.length, 
					new InetSocketAddress(ipaddress, configuration.getPort()));
			socket.setSoTimeout(5000); // 5S timeout
			for(int i=0; i<3; i++){
				socket.send(datapkg);
				
				DatagramPacket recvPkg = new DatagramPacket(new byte[1000], 1000);
				try{
					socket.receive(recvPkg);
					String rstStr = new String(recvPkg.getData(),0, recvPkg.getLength());
					// should be "$example result xxxx". For simple, no verify
					return Double.parseDouble(rstStr.split(",")[3]);
				}catch (SocketTimeoutException e){
					DriverUtils.log(Level.FINE, "Example", "Timeout, try again " + i);
				}
			}
			return -1;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DeviceDriverException("Exception when create soket", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new DeviceDriverException("Exception when send request", e);
		}
	}

	@Override
	protected long getDevicePollingPeriod() {
		return 5000;
	}

	@Override
	protected DeviceStatus pollingDevice(DeviceConfigData device) {
		try {
			double value = doQuery(DriverUtils.getAsInt(device.getIdentity().get("id"), -1), (String) device.getIdentity().get("ipAddress"));
			if (value < 0){
				return null;
			}
			DeviceStatus status = new DeviceStatus();
			status.setID(device.getID());
			status.setProfile(device.getProfile());
			Map<String, Object> rst = getResult(value);
			status.setStatus(rst);
			return status;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean canDriverDevice(String deviceID, DeviceStatus deviceStatus, DeviceProfile profile,
			DeviceConfigData devCfg) {
		if (profile.getID().equals(PROFILE)){
			return true;
		}
		return false;
	}

}
