package com.frank.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher implements ApplicationEventPublisherAware {

	/**
	 * 底層傳送事件用的元件，SpringBoot會透過ApplicationEventPublisherAware介面自動注入給我們
	 * 事件是廣播出去的。所有監聽這個事件的監聽器都可以收到
	 */
	private ApplicationEventPublisher applicationEventPublisher;

	/**
	 * 會被自動調用，把真正發事件的底層群組元件給我們注入進來
	 * @param applicationEventPublisher event publisher to be used by this object
	 */
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	/**
	 * 所有事件都可以發
	 * @param event
	 */
	public void publishEvent(Object event) {
		applicationEventPublisher.publishEvent(event);
	}

}
