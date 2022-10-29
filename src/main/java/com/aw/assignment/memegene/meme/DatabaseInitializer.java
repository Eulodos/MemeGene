package com.aw.assignment.memegene.meme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@ConditionalOnProperty(prefix = "populate.database", value = "enabled", havingValue = "true")
@Component
class DatabaseInitializer implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final MemeTemplateRepository memeTemplateRepository;

    DatabaseInitializer(final MemeTemplateRepository memeTemplateRepository) {
        this.memeTemplateRepository = memeTemplateRepository;
    }

    @Override
    public void run(final ApplicationArguments args) throws Exception {
        if (memeTemplateRepository.count() < 1) {
            final ImgflipResponse response = fetchMemes();
            LOG.info("{}", response);

            if (response != null) {
                persistMemes(response);
            }
        }
    }

    private ImgflipResponse fetchMemes() {
        final WebClient webClient = WebClient.builder().baseUrl("https://api.imgflip.com")
                .build();

        return webClient.get()
                .uri("/get_memes")
                .retrieve()
                .bodyToMono(ImgflipResponse.class)
                .block();
    }

    private void persistMemes(final ImgflipResponse response) {
        response.data().memes().forEach(meme -> {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                final byte[] imgBytes = readMemeBytes(meme, byteArrayOutputStream);
                final MemeTemplateEntity memeTemplateEntity = new MemeTemplateEntity(imgBytes, meme.name(), meme.width(), meme.height());
                memeTemplateRepository.save(memeTemplateEntity);
            } catch (final MalformedURLException e) {
                LOG.error("Couldn't create an URL");
            } catch (final IOException e) {
                LOG.error("Could not parse image for meme: {}, original ex: {}", meme.name(), e.getMessage());
            }
        });
    }

    private byte[] readMemeBytes(final Meme meme, final ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        final URL url = new URL(meme.url());
        final BufferedImage bufferedImage = ImageIO.read(url);
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    record Data(List<Meme> memes) {
    }

    record Meme(
            String id,
            String name,
            String url,
            int width,
            int height,
            int box_count) {
    }


    record ImgflipResponse(boolean success,
                           Data data) {
    }


}
