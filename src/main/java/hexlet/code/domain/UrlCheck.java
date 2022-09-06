package hexlet.code.domain;

import io.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.NotNull;

@Entity
public final class UrlCheck extends Model {

    @Id
    private long id;

    private int statusCode;

    private String title;

    private String h1;

    @Lob
    private String description;

    @WhenCreated
    private Instant createdAt;

    @ManyToOne
    @NotNull
    private Url url;

    public UrlCheck(int statusCode, String title, String h1, String description) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
    }

    public long getId() {
        return this.id;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getTitle() {
        return this.title;
    }

    public String getH1() {
        return this.h1;
    }

    public String getDescription() {
        return this.description;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Url getUrl() {
        return this.url;
    }
}
