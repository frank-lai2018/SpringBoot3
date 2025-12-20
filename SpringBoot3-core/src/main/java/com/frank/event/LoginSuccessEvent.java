package com.frank.event;

import org.springframework.context.ApplicationEvent;

import com.frank.dto.UserDTO;

public class LoginSuccessEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4839582409194171366L;

	public LoginSuccessEvent(UserDTO user) {
		super(user);
	}


}
