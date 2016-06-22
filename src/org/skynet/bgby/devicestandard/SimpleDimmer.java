package org.skynet.bgby.devicestandard;

public class SimpleDimmer extends SimpleLight {
	public static final String ID = "Light.Dimmer";
	
	public static final String TERM_DIMMER_LEVEL = "dimmer";
	
	public static final int ERR_DIMMER_OUT_OF_RANGE = ERR_LIGHT_START_CODE + 101;

	public SimpleDimmer() {
		super();
		id = ID;
		
		TERM(TERM_DIMMER_LEVEL);
		
	}

}
