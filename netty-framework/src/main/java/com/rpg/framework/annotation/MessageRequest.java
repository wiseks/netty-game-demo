package com.rpg.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageRequest {


	/**
	 * 是否管理员协议
	 * 
	 * @return
	 */
	boolean admin() default false;

	/**
	 * 不需要任何安全校验
	 * 
	 * @return
	 */
	boolean nocheck() default false;

	/**
	 * 是否需要登錄操作
	 * 
	 * @return
	 */
	boolean checkLogin() default true;
}
