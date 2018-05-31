package com.rpg.framework.handler.dispatcher;

public class TestCglib {
	public static void main(String[] args) {
        UserServiceCglib cglib = new UserServiceCglib();
        UserServiceImpl bookFacedImpl = (UserServiceImpl) cglib.getInstance(new UserServiceImpl());
//        bookFacedImpl.addUser();
    }
}
