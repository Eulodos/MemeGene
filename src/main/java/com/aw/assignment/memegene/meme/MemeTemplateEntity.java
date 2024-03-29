package com.aw.assignment.memegene.meme;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class MemeTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private byte[] image;

    private String title;

    private Integer width;

    private Integer height;

    protected MemeTemplateEntity() {
    }

    public MemeTemplateEntity(final byte[] image, final String title, final Integer width, final Integer height) {
        this.image = image;
        this.title = title;
        this.width = width;
        this.height = height;
    }
}
