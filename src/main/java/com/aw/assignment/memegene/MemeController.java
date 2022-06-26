package com.aw.assignment.memegene;

import com.aw.assignment.memegene.meme.MemeRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class MemeController {

    private final MemeRepository memeRepository;

    MemeController(final MemeRepository memeRepository) {
        this.memeRepository = memeRepository;
    }

    @GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getImage(@PathVariable final Long id) {
        return memeRepository.findById(id).orElseThrow(() -> new RuntimeException("Meme with id: " + id + " was not found")).getImage();
    }
}
