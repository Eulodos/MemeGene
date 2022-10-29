package com.aw.assignment.memegene;

import com.aw.assignment.memegene.meme.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;

@RestController
class MemeController {

    private final MemeTemplateRepository memeTemplateRepository;
    private final MemeRepository memeRepository;
    private final MemeImageProcessor memeProcessor;

    private final SecureRandom secureRandom = new SecureRandom();

    MemeController(final MemeTemplateRepository memeTemplateRepository, final MemeRepository memeRepository, final MemeImageProcessor memeProcessor) {
        this.memeTemplateRepository = memeTemplateRepository;
        this.memeRepository = memeRepository;
        this.memeProcessor = memeProcessor;
    }

    //, produces = MediaType.IMAGE_JPEG_VALUE
    @GetMapping(value = "/template/{id}")
    ResponseEntity<byte[]> getTemplateById(@PathVariable final Long id) {
        return ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.IMAGE_JPEG)
                .body(memeTemplateRepository.findById(id)
                        .orElseThrow(() -> new MemeNotFoundException("Meme with id: " + id + " was not found"))
                        .getImage());
    }

    @GetMapping(value = "/templates", produces = MediaType.APPLICATION_JSON_VALUE)
    List<TestRecord> getMemeTemplates() {
        return memeTemplateRepository.findAllProjectedBy();
    }

    @GetMapping(value = "/meme/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getMemeById(@PathVariable final Integer id) {
        return this.memeRepository.findById(id)
                .orElseThrow(() -> new MemeNotFoundException(String.format("Meme with ID %s not found", id)))
                .getMeme();
    }

    @PostMapping(value = "/meme", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] createMeme(@RequestBody final CreateMemeInput input, final HttpServletRequest request) {
        final String remoteAddr = request.getRemoteAddr();
        try {
            final MemeEntity meme = this.memeProcessor.createMeme(input, remoteAddr);
            return meme.getMeme();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/meme/random", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] randomMeme() {
        final Integer firstByOrderByIdDesc = this.memeRepository.findGreatestId();
        final int i = secureRandom.nextInt(firstByOrderByIdDesc) + 1;
        return memeRepository.findById(i)
                .orElseThrow(() -> new MemeNotFoundException("Shouldn't really happen"))
                .getMeme();
    }

    @ExceptionHandler(MemeNotFoundException.class)
    ResponseEntity<ErrorResponse> handleMemeNotFoundException(final MemeNotFoundException ex, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), Instant.now(), ex.getMessage(), request.getRequestURI()));
    }
}
