package io.arenadata.testrestapi.dao;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@NamedQuery(name = "Category.findAll", query = "SELECT x FROM Category x ORDER BY x.id")
public final class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
