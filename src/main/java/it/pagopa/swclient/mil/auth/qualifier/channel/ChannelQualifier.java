/*
 * ChannelQualifier.java
 *
 * 3 apr 2024
 */
package it.pagopa.swclient.mil.auth.qualifier.channel;

import java.util.Map;

import it.pagopa.swclient.mil.auth.bean.Channel;
import jakarta.enterprise.util.AnnotationLiteral;

/**
 * 
 * @author Aantonio Tarricone
 */
public class ChannelQualifier {
	/*
	 *
	 */
	@SuppressWarnings("serial")
	private static final Map<Channel, AnnotationLiteral<?>> QUALIFIERS = Map.of(
		Channel.MIL, new AnnotationLiteral<MilService>() { },
		Channel.SERVER, new AnnotationLiteral<Server>() { },
		Channel.ATM, new AnnotationLiteral<Atm>() { });
	
	/**
	 * 
	 */
	private ChannelQualifier() {
	}
	
	/**
	 * 
	 * @param channel
	 * @return 
	 */
	public static AnnotationLiteral<?> get(Channel channel) {
		return QUALIFIERS.get(channel);
	}
}