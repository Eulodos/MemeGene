package com.aw.assignment.memegene.meme;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MemeImageProcessor {

    private static final Map<String, Field> COLORS = Arrays.stream(Color.class.getDeclaredFields())
            .filter(field -> field.getType().equals(Color.class))
            .filter(field -> Modifier.isStatic(field.getModifiers()))
            .filter(field -> Modifier.isFinal(field.getModifiers()))
            .collect(Collectors.toMap(Field::getName, Function.identity()));
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
        setDefaultColor(graphics);
        input.captionList().forEach(caption -> {
            if (input.color() != null && !input.color().isBlank()) {
                graphics.setColor(parseColor(input.color()));
            }
            graphics.drawString(caption.text(), caption.x(), caption.y());
        });
        graphics.dispose();

        final byte[] finalImg;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            finalImg = byteArrayOutputStream.toByteArray();
        }

        MemeEntity newMeme = new MemeEntity(finalImg, author);
        MemeEntity byHash = this.memeRepository.findFirstByHash(newMeme.hashCode());
        System.out.println("Result for query by hash: " + byHash);
        if (null == byHash) {
            return this.memeRepository.save(newMeme);
        }
        System.out.println("Meme was already in the database, returning the previous record");
        return byHash;
    }

    private Color parseColor(String color) {
        Field foundColor = COLORS.get(color.toUpperCase());
        try {
            return foundColor == null ? Color.BLACK : (Color) foundColor.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setDefaultColor(Graphics graphics) {
        graphics.setColor(Color.BLACK);
    }
}
