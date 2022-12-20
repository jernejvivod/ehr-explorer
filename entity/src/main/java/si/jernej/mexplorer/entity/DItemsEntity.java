package si.jernej.mexplorer.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

// Dictionary of non-laboratory-related charted items.
@Entity
@Table(name = "d_items", schema = "mimiciii", catalog = "mimic")
public class DItemsEntity extends AEntity
{
    private int itemId;           // charted item ID
    private String label;         // label identifying the item
    private String abbreviation;  // abbreviation associated with the item
    private String dbSource;      // source database of the item
    private String linksTo;       // table which contains data for the given ITEMID
    private String category;      // category of data which the concept falls under
    private String unitName;      // unit associated with the item
    private String paramType;     // type of item, for example solution or ingredient
    private Integer conceptId;    // identifier used to harmonize concepts identified by multiple ITEMIDs. CONCEPTIDs are plannet but not yet implemented (all values are NULL).
    private Set<ChartEventsEntity> chartEventsEntitys;
    private Set<DatetimeEventsEntity> datetimeEventsEntitys;
    private Set<DLabitemsEntity> dLabitemsEntitys;
    private Set<InputEventsCvEntity> inputEventsCvEntitys;
    private Set<InputEventsMvEntity> inputEventsMvEntitys;
    private Set<LabEventsEntity> labEventsEntitys;
    private Set<MicrobiologyEventsEntity> microbiologyEventsEntitysSpecItem;
    private Set<MicrobiologyEventsEntity> microbiologyEventsEntitysOrgItem;
    private Set<MicrobiologyEventsEntity> microbiologyEventsEntitysAbItem;
    private Set<OutputEventsEntity> outputEventsEntitys;
    private Set<ProcedureEventsMvEntity> procedureEventsMvEntitys;

    @Column(name = "itemid", nullable = false)
    public int getItemId()
    {
        return itemId;
    }

    public void setItemId(int itemId)
    {
        this.itemId = itemId;
    }

    @Column(name = "label", length = 200)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Column(name = "abbreviation", length = 100)
    public String getAbbreviation()
    {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation)
    {
        this.abbreviation = abbreviation;
    }

    @Column(name = "dbsource", length = 20)
    public String getDbSource()
    {
        return dbSource;
    }

    public void setDbSource(String dbSource)
    {
        this.dbSource = dbSource;
    }

    @Column(name = "linksto", length = 50)
    public String getLinksTo()
    {
        return linksTo;
    }

    public void setLinksTo(String linksTo)
    {
        this.linksTo = linksTo;
    }

    @Column(name = "category", length = 100)
    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    @Column(name = "unitname", length = 100)
    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    @Column(name = "param_type", length = 30)
    public String getParamType()
    {
        return paramType;
    }

    public void setParamType(String paramType)
    {
        this.paramType = paramType;
    }

    @Column(name = "conceptid")
    public Integer getConceptId()
    {
        return conceptId;
    }

    public void setConceptId(Integer conceptId)
    {
        this.conceptId = conceptId;
    }

    @OneToMany(mappedBy = "dItemsEntity", fetch = FetchType.LAZY)
    public Set<ChartEventsEntity> getChartEventsEntitys()
    {
        return chartEventsEntitys;
    }

    public void setChartEventsEntitys(Set<ChartEventsEntity> chartEventsEntitys)
    {
        this.chartEventsEntitys = chartEventsEntitys;
    }

    @OneToMany(mappedBy = "dItemsEntity", fetch = FetchType.LAZY)
    public Set<DatetimeEventsEntity> getDatetimeEventsEntitys()
    {
        return datetimeEventsEntitys;
    }

    public void setDatetimeEventsEntitys(Set<DatetimeEventsEntity> datetimeEventsEntitys)
    {
        this.datetimeEventsEntitys = datetimeEventsEntitys;
    }

    @OneToMany(mappedBy = "dItemsEntity", fetch = FetchType.LAZY)
    public Set<DLabitemsEntity> getdLabitemsEntitys()
    {
        return dLabitemsEntitys;
    }

    public void setdLabitemsEntitys(Set<DLabitemsEntity> dLabitemsEntitys)
    {
        this.dLabitemsEntitys = dLabitemsEntitys;
    }

    @OneToMany(mappedBy = "dItemsEntity", fetch = FetchType.LAZY)
    public Set<InputEventsCvEntity> getInputEventsCvEntitys()
    {
        return inputEventsCvEntitys;
    }

    public void setInputEventsCvEntitys(Set<InputEventsCvEntity> inputEventsCvEntitys)
    {
        this.inputEventsCvEntitys = inputEventsCvEntitys;
    }

    @OneToMany(mappedBy = "dItemsEntity", fetch = FetchType.LAZY)
    public Set<InputEventsMvEntity> getInputEventsMvEntitys()
    {
        return inputEventsMvEntitys;
    }

    public void setInputEventsMvEntitys(Set<InputEventsMvEntity> inputEventsMvEntitys)
    {
        this.inputEventsMvEntitys = inputEventsMvEntitys;
    }

    @OneToMany(mappedBy = "dItemsEntity", fetch = FetchType.LAZY)
    public Set<LabEventsEntity> getLabEventsEntitys()
    {
        return labEventsEntitys;
    }

    public void setLabEventsEntitys(Set<LabEventsEntity> labEventsEntitys)
    {
        this.labEventsEntitys = labEventsEntitys;
    }

    @OneToMany(mappedBy = "specItem", fetch = FetchType.LAZY)
    public Set<MicrobiologyEventsEntity> getMicrobiologyEventsEntitysSpecItem()
    {
        return microbiologyEventsEntitysSpecItem;
    }

    public void setMicrobiologyEventsEntitysSpecItem(Set<MicrobiologyEventsEntity> microbiologyEventsEntitysSpecItem)
    {
        this.microbiologyEventsEntitysSpecItem = microbiologyEventsEntitysSpecItem;
    }

    @OneToMany(mappedBy = "orgItem", fetch = FetchType.LAZY)
    public Set<MicrobiologyEventsEntity> getMicrobiologyEventsEntitysOrgItem()
    {
        return microbiologyEventsEntitysOrgItem;
    }

    public void setMicrobiologyEventsEntitysOrgItem(Set<MicrobiologyEventsEntity> microbiologyEventsEntitysOrgItem)
    {
        this.microbiologyEventsEntitysOrgItem = microbiologyEventsEntitysOrgItem;
    }

    @OneToMany(mappedBy = "abItem", fetch = FetchType.LAZY)
    public Set<MicrobiologyEventsEntity> getMicrobiologyEventsEntitysAbItem()
    {
        return microbiologyEventsEntitysAbItem;
    }

    public void setMicrobiologyEventsEntitysAbItem(Set<MicrobiologyEventsEntity> microbiologyEventsEntitysabItem)
    {
        this.microbiologyEventsEntitysAbItem = microbiologyEventsEntitysabItem;
    }

    @OneToMany(mappedBy = "dItemsEntity", fetch = FetchType.LAZY)
    public Set<OutputEventsEntity> getOutputEventsEntitys()
    {
        return outputEventsEntitys;
    }

    public void setOutputEventsEntitys(Set<OutputEventsEntity> outputEventsEntitys)
    {
        this.outputEventsEntitys = outputEventsEntitys;
    }

    @OneToMany(mappedBy = "dItemsEntity", fetch = FetchType.LAZY)
    public Set<ProcedureEventsMvEntity> getProcedureEventsMvEntitys()
    {
        return procedureEventsMvEntitys;
    }

    public void setProcedureEventsMvEntitys(Set<ProcedureEventsMvEntity> procedureEventsMvEntitys)
    {
        this.procedureEventsMvEntitys = procedureEventsMvEntitys;
    }
}
