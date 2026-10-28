package com.vichumeats.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ErrorAdvice {
  @ExceptionHandler({
    IllegalArgumentException.class,
    java.util.NoSuchElementException.class,
    org.springframework.dao.OptimisticLockingFailureException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  String invalid(Exception e, Model m) {
    m.addAttribute(
        "detail",
        e instanceof IllegalArgumentException
            ? e.getMessage()
            : "This record changed or is unavailable. Reload the page and try again.");
    return "problem";
  }

  @ExceptionHandler({
    java.io.IOException.class,
    org.springframework.web.multipart.MaxUploadSizeExceededException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  String upload(Model m) {
    m.addAttribute("detail", "Image upload failed. Use a JPG or PNG under 3 MB.");
    return "problem";
  }
}
