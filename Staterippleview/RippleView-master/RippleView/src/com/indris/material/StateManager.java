package com.indris.material;

import com.indris.material.RippleView.State;

public class StateManager {
	private State currentState;
	private State preState = State.IDIE;
	public StateManager(State currentState) {
		this.currentState = currentState;
	}
	public State getCurrentState() {
		return currentState;
	}
	public void setCurrentState(State currentState) {
			this.preState = this.currentState; 
			this.currentState = currentState;
			statechange();
	}
	public State getPreState() {
		return preState;
	}
	public void setPreState(State preState) {
		this.preState = preState;
	}

	public void statechange(){
		
	}
}
