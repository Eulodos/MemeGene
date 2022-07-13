package com.aw.assignment.memegene;

import com.aw.assignment.memegene.meme.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

@RestController
class MemeController {

    private final MemeTemplateRepository memeTemplateRepository;
    private final MemeRepository memeRepository;

    MemeController(final MemeTemplateRepository memeTemplateRepository, final MemeRepository memeRepository) {
        this.memeTemplateRepository = memeTemplateRepository;
        this.memeRepository = memeRepository;
    }

    @GetMapping(value = "/template/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getTemplateById(@PathVariable final Long id) {
        return memeTemplateRepository.findById(id).orElseThrow(() -> new MemeNotFoundException("Meme with id: " + id + " was not found")).getImage();
    }

    @GetMapping(value = "/templates", produces = MediaType.APPLICATION_JSON_VALUE)
    List<TestRecord> getMemeTemplates() {
        return memeTemplateRepository.findAllProjectedBy();
    }

    @GetMapping(value = "/meme/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getMemeById(@PathVariable final Long id) {
        //TODO: FIX THIS
        return this.memeRepository.findById(id).get().getMeme();
    }

    @PostMapping(value = "/meme", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] createMeme(final CreateMemeInput input, final HttpServletRequest request) {
        //TODO: Return response informing about newly created resource, also in headers
        final String remoteAddr = request.getRemoteAddr();
        return null;
    }

    @GetMapping(value = "/meme/random", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] randomMeme() {
        return null;
    }

    @ExceptionHandler(MemeNotFoundException.class)
    ResponseEntity<ErrorResponse> handleMemeNotFoundException(final MemeNotFoundException ex, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), Instant.now(), ex.getMessage(), request.getRequestURI()));
    }
}
