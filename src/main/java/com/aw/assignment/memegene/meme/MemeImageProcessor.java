package com.aw.assignment.memegene.meme;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class MemeImageProcessor {

    private final MemeTemplateRepository memeTemplateRepository;
    private final MemeRepository memeRepository;

    public MemeImageProcessor(final MemeTemplateRepository memeTemplateRepository, final MemeRepository memeRepository) {
        this.memeTemplateRepository = memeTemplateRepository;
        this.memeRepository = memeRepository;
    }

    public MemeEntity createMeme(final CreateMemeInput input, final String author) throws IOException {
        final MemeTemplateEntity memeTemplate = this.memeTemplateRepository.findById(input.templateId())
                .orElseThrow(() -> new MemeNotFoundException("Template with ID " + input.templateId() + " was not found"));

        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(memeTemplate.getImage()));
        final BufferedImage image = ImageIO.read(bufferedInputStream);
        final Graphics graphics = image.getGraphics();
        final float fontSize = 30F;
        graphics.setFont(graphics.getFont().deriveFont(fontSize));
        input.captionsList().forEach(caption -> graphics.drawString(caption.text(), caption.x(), caption.y()));
        graphics.dispose();

        final byte[] finalImg;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            finalImg = byteArrayOutputStream.toByteArray();
        }

        return this.memeRepository.save(new MemeEntity(finalImg, author));
    }
}
