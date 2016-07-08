package org.skynet.bgby.devicestandard;

public class SimpleSensor extends DeviceStandardBaseImpl {
	public static final String ID = "Sensor.Simple";
	
	public static final String TERM_MEASURE_VALUE = "measureValue";
	public static final String TERM_MEASURE_LEVEL = "measureLevel";
	public static final String TERM_MEASURE_UNIT = "unit";
	public static final String TERM_MEASURE_NAME = "measureName";
	public static final String TERM_MEASURE_LEVEL_GOOD = "good";
	public static final String TERM_MEASURE_LEVEL_NORMAL = "normal";
	public static final String TERM_MEASURE_LEVEL_BAD = "bad";
		
	

	public SimpleSensor() {
		super();
		id = ID;
		
		TERM(TERM_MEASURE_VALUE);
		TERM(TERM_MEASURE_LEVEL);
		TERM(TERM_MEASURE_NAME);
		TERM(TERM_MEASURE_UNIT);
		TERM(TERM_MEASURE_LEVEL_GOOD);
		TERM(TERM_MEASURE_LEVEL_NORMAL);
		TERM(TERM_MEASURE_LEVEL_BAD);

	}

}
