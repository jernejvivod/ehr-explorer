package si.jernej.mexplorer.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// Dictionary of laboratory-related items.
@Entity
@Table(name = "d_labitems", schema = "mimiciii", catalog = "mimic")
public class DLabitemsEntity extends AEntity
{
    private DItemsEntity dItemsEntity;  // foreign key identifying the charted item
    private String label;               // label identifying the item
    private String fluid;               // fluid associated with the item, for example blood or urine
    private String category;            // category of item, for example chemistry or hematology
    private String loincCode;           // Logical Observation Identifiers Names and Codes (LOINC) mapped to the item, if available.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemid", referencedColumnName = "itemid")
    public DItemsEntity getdItemsEntity()
    {
        return dItemsEntity;
    }

    public void setdItemsEntity(DItemsEntity dItemsEntity)
    {
        this.dItemsEntity = dItemsEntity;
    }

    @Column(name = "label", nullable = false, length = 100)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Column(name = "fluid", nullable = false, length = 100)
    public String getFluid()
    {
        return fluid;
    }

    public void setFluid(String fluid)
    {
        this.fluid = fluid;
    }

    @Column(name = "category", nullable = false, length = 100)
    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    @Column(name = "loinc_code", length = 100)
    public String getLoincCode()
    {
        return loincCode;
    }

    public void setLoincCode(String loincCode)
    {
        this.loincCode = loincCode;
    }
}
