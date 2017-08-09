package com.xxx.model.actionword.basic;

import java.util.List;

public class ManualTestcase {
	private String id;
	
	private String title;
	
	private boolean active = true;
	
	private int priority;//优先级
	
	private List<String> presetConditons;
	
	private List<String> oprationSteps;
	
	private List<String> expectedResult;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public List<String> getPresetConditons() {
		return presetConditons;
	}

	public void setPresetConditons(List<String> presetConditons) {
		this.presetConditons = presetConditons;
	}

	public List<String> getOprationSteps() {
		return oprationSteps;
	}

	public void setOprationSteps(List<String> oprationSteps) {
		this.oprationSteps = oprationSteps;
	}

	public List<String> getExpectedResult() {
		return expectedResult;
	}

	public void setExpectedResult(List<String> expectedResult) {
		this.expectedResult = expectedResult;
	}
}
