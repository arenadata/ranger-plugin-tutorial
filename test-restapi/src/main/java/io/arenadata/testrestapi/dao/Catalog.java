package io.arenadata.testrestapi.dao;
import io.arenadata.testrestapi.system.CommonConstants;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@NamedQuery(name = "Catalog.findAll", query = "SELECT x FROM Catalog x ORDER BY x.id")
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

    @Override
    public int hashCode() {
        return CommonConstants.DEFAULT_ODD_PRIME_VALUE * this.id.intValue();
    }

    @Override
    public boolean equals(Object inputObject) {
        if (this == inputObject)
            return true;

        if (inputObject == null || getClass() != inputObject.getClass())
            return false;

        Catalog inputDao = (Catalog)inputObject;
        return this.id.equals(inputDao.id);
    }
}
