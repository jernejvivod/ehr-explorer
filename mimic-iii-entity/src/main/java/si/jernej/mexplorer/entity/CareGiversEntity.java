package si.jernej.mexplorer.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import si.jernej.mexplorer.annotation.PropertyOrder;

// List of caregivers associated with an ICU stay.
@Entity
@Table(name = "caregivers", schema = "mimiciii", catalog = "mimic")
public class CareGiversEntity extends AEntity
{
    @PropertyOrder(2)
    private int cgId;  // caregiver ID
    @PropertyOrder(3)
    private String label;  // title of the caregiver, for example MD or RN
    @PropertyOrder(4)
    private String description;  // more detailed description of the caregiver, if available
    @PropertyOrder(5)
    private Set<ChartEventsEntity> chartEventsEntitys;
    @PropertyOrder(6)
    private Set<DatetimeEventsEntity> datetimeEventsEntitys;
    @PropertyOrder(7)
    private Set<InputEventsCvEntity> inputEventsCvEntitys;
    @PropertyOrder(8)
    private Set<InputEventsMvEntity> inputEventsMvEntitys;
    @PropertyOrder(9)
    private Set<NoteEventsEntity> noteEventsEntitys;
    @PropertyOrder(10)
    private Set<OutputEventsEntity> outputEventsEntitys;
    @PropertyOrder(11)
    private Set<ProcedureEventsMvEntity> procedureEventsMvEntitys;

    @Column(name = "cgid", nullable = false)
    public int getCgId()
    {
        return cgId;
    }

    public void setCgId(int cgId)
    {
        this.cgId = cgId;
    }

    @Column(name = "label", length = 15)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Column(name = "description", length = 30)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @OneToMany(mappedBy = "careGiversEntity", fetch = FetchType.LAZY)
    public Set<ChartEventsEntity> getChartEventsEntitys()
    {
        return chartEventsEntitys;
    }

    public void setChartEventsEntitys(Set<ChartEventsEntity> chartEventsEntitys)
    {
        this.chartEventsEntitys = chartEventsEntitys;
    }

    @OneToMany(mappedBy = "careGiversEntity", fetch = FetchType.LAZY)
    public Set<DatetimeEventsEntity> getDatetimeEventsEntitys()
    {
        return datetimeEventsEntitys;
    }

    public void setDatetimeEventsEntitys(Set<DatetimeEventsEntity> datetimeEventsEntitys)
    {
        this.datetimeEventsEntitys = datetimeEventsEntitys;
    }

    @OneToMany(mappedBy = "careGiversEntity", fetch = FetchType.LAZY)
    public Set<InputEventsCvEntity> getInputEventsCvEntitys()
    {
        return inputEventsCvEntitys;
    }

    public void setInputEventsCvEntitys(Set<InputEventsCvEntity> inputEventsCvEntitys)
    {
        this.inputEventsCvEntitys = inputEventsCvEntitys;
    }

    @OneToMany(mappedBy = "careGiversEntity", fetch = FetchType.LAZY)
    public Set<InputEventsMvEntity> getInputEventsMvEntitys()
    {
        return inputEventsMvEntitys;
    }

    public void setInputEventsMvEntitys(Set<InputEventsMvEntity> inputEventsMvEntitys)
    {
        this.inputEventsMvEntitys = inputEventsMvEntitys;
    }

    @OneToMany(mappedBy = "careGiversEntity", fetch = FetchType.LAZY)
    public Set<NoteEventsEntity> getNoteEventsEntitys()
    {
        return noteEventsEntitys;
    }

    public void setNoteEventsEntitys(Set<NoteEventsEntity> noteEventsEntitys)
    {
        this.noteEventsEntitys = noteEventsEntitys;
    }

    @OneToMany(mappedBy = "careGiversEntity", fetch = FetchType.LAZY)
    public Set<OutputEventsEntity> getOutputEventsEntitys()
    {
        return outputEventsEntitys;
    }

    public void setOutputEventsEntitys(Set<OutputEventsEntity> outputEventsEntitys)
    {
        this.outputEventsEntitys = outputEventsEntitys;
    }

    @OneToMany(mappedBy = "careGiversEntity", fetch = FetchType.LAZY)
    public Set<ProcedureEventsMvEntity> getProcedureEventsMvEntitys()
    {
        return procedureEventsMvEntitys;
    }

    public void setProcedureEventsMvEntitys(Set<ProcedureEventsMvEntity> procedureEventsMvEntitys)
    {
        this.procedureEventsMvEntitys = procedureEventsMvEntitys;
    }
}
