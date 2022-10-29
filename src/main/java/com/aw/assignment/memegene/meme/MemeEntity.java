package com.aw.assignment.memegene.meme;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Getter
@Setter
public class MemeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meme_entity_sequence")
    @SequenceGenerator(name = "meme_entity_sequence", allocationSize = 1)
    private Integer id;

    @Lob
    private byte[] meme;

    private String author;

    private int hash;

    protected MemeEntity() {
    }

    protected MemeEntity(final byte[] meme, final String author) {
        this.meme = meme;
        this.author = author;
        this.hash = hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemeEntity that = (MemeEntity) o;
        return Arrays.equals(meme, that.meme) && Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(author);
        result = 31 * result + Arrays.hashCode(meme);
        return result;
    }
}
