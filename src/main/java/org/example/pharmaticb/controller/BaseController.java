package org.example.pharmaticb.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(value=RUNTIME)
@RequestMapping(value = "/api")
public @interface BaseController {
}
