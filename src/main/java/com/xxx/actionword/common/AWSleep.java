package com.xxx.actionword.common;

import java.util.concurrent.TimeUnit;

import com.xxx.actionword.basic.ActionWord;

public class AWSleep extends ActionWord{
	private String seconds;

	@Override
	public boolean compareExpectAndActual() {
		try {
			logger.warn("Thread will sleep {} seconds.", seconds);
			TimeUnit.SECONDS.sleep(Integer.parseInt(seconds));
		} catch (NumberFormatException e) {
			logger.error("Please input correct seconds {}.", seconds);
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Eccute AWSleep failed.");
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void assignContext() {
		
	}

	public String getSeconds() {
		return seconds;
	}

	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}
}
