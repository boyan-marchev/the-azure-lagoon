package softuni.springadvanced.web.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ModelAndView handleAnyException(Throwable exception){

        ModelAndView modelAndView = new ModelAndView("error");
        Throwable throwable = exception;

        while (throwable.getCause() != null){
            throwable = throwable.getCause();

        }

        modelAndView.addObject("message", throwable.getMessage());
        return modelAndView;
    }
}
