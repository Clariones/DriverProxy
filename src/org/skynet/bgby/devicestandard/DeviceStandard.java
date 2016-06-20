package org.skynet.bgby.devicestandard;

import java.util.Set;

public interface DeviceStandard {

	boolean isSupportCommand(String command);

	boolean isValidTerm(String term);

	String getId();

	Set<String> getCommands();

	Set<String> getTerms();

}
