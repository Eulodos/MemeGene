package com.aw.assignment.memegene.meme;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class MemeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private byte[] meme;

    private String author;

    protected MemeEntity() {
    }

    protected MemeEntity(final byte[] meme, final String author) {
        this.meme = meme;
        this.author = author;
    }
}
