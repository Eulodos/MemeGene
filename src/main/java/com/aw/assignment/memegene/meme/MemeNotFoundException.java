package com.aw.assignment.memegene.meme;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemeNotFoundException extends RuntimeException {

    public MemeNotFoundException(final String message) {
        super(message);
    }
}
