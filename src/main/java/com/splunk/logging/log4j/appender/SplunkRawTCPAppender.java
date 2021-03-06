package com.splunk.logging.log4j.appender;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import com.splunk.logging.SplunkRawTCPInput;

/**
 * Log4j Appender for sending events to Splunk via Raw TCP
 * 
 * @author Damien Dallimore damien@dtdsoftware.com
 * 
 */
public class SplunkRawTCPAppender extends AppenderSkeleton {

	// connection settings
	private String host = "";
	private int port = 5150;

	//queuing settings
	private String maxQueueSize; 
	private boolean dropEventsOnQueueFull;
	
	private SplunkRawTCPInput sri;

	/**
	 * Constructor
	 */
	public SplunkRawTCPAppender() {
	}

	/**
	 * Constructor
	 * 
	 * @param layout
	 *            the layout to apply to the log event
	 */
	public SplunkRawTCPAppender(Layout layout) {

		this.layout = layout;
	}

	/**
	 * Log the message
	 */
	@Override
	protected void append(LoggingEvent event) {

		try {
			if (sri == null) {
				sri = new SplunkRawTCPInput(host, port);
				sri.setMaxQueueSize(maxQueueSize);
				sri.setDropEventsOnQueueFull(dropEventsOnQueueFull);
			}
		} catch (Exception e) {
			errorHandler
					.error("Couldn't establish Raw TCP connection for SplunkRawTCPAppender named \""
							+ this.name + "\".");
			return;
		}

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(layout.format(event));

        if(layout.ignoresThrowable()) {
            String[] s = event.getThrowableStrRep();
            if (s != null) {
                int len = s.length;
                for(int i = 0; i < len; i++) {
                    stringBuilder.append(s[i]);
                    stringBuilder.append(Layout.LINE_SEP);
                }
            }
        }

		sri.streamEvent(stringBuilder.toString());
	}

	/**
	 * Clean up resources
	 */
	synchronized public void close() {

		closed = true;
		if (sri != null) {
			try {
				sri.closeStream();
				sri = null;
			} catch (Exception e) {
				Thread.currentThread().interrupt();
				sri = null;
			}
		}

	}

	public boolean requiresLayout() {
		return true;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	public String getMaxQueueSize() {
		return maxQueueSize;
	}

	public void setMaxQueueSize(String maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}

	public boolean isDropEventsOnQueueFull() {
		return dropEventsOnQueueFull;
	}

	public void setDropEventsOnQueueFull(boolean dropEventsOnQueueFull) {
		this.dropEventsOnQueueFull = dropEventsOnQueueFull;
	}


}
