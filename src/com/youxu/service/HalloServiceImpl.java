package com.youxu.service;

public class HalloServiceImpl implements HalloService {

	@Override
	public String hallo(String name) {
		return "hallo:" + name;
	}

}
