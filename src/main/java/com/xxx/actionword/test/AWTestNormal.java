package com.xxx.actionword.test;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.model.actionword.test.Student;

public class AWTestNormal extends ActionWord{
	private String param;
	
	private String actual;
	
	private String contextKeyName;
	
	private Student student;

	public String action(String str) {
		String result = str + "12345";
		return result;
	}

	@Override
	public boolean compareExpectAndActual() {
		boolean result = false;
//		String expectValue = action(param);
		String expectValue = action(student.getName());
		if (expectValue.equals(actual)) {
			result = true;
			logger.info("AWTestNormal compare expect and actual successfully.");
		} else {
			logger.error("Excute AWTestNormal failed. Expected value is {}. Actual value is {}.", expectValue, actual);
		}
		
		return result;
	}

	@Override
	public void assignContext() {
		logger.info("AWTestNormal assign Context.");
		String expectValue = action(student.getName());
		setContext(contextKeyName, expectValue);
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public String getContextKeyName() {
		return contextKeyName;
	}

	public void setContextKeyName(String contextKeyName) {
		this.contextKeyName = contextKeyName;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
}
