package io.arenadata.testrestapi.dao;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public final class Catalog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany()
    private List<Category> category;

    public Long getId() {
        return id;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }
}
