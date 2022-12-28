package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import si.jernej.mexplorer.entity.annotation.PropertyOrder;

// Notes associated with hospital stays.
@Entity
@Table(name = "noteevents", schema = "mimiciii", catalog = "mimic")
public class NoteEventsEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private LocalDateTime chartDate;            // date when the note was charted
    @PropertyOrder(5)
    private LocalDateTime chartTime;            // Date and time when the note was charted. Note that some notes (e.g. discharge summaries) do not have a time associated with them: these notes have NULL in this column.
    @PropertyOrder(6)
    private LocalDateTime storeTime;            // ?
    @PropertyOrder(7)
    private String category;                    // Category of the note, e.g. Discharge summary.
    @PropertyOrder(8)
    private String description;                 // a more detailed categorization for the note, sometimes entered by free-text
    @PropertyOrder(9)
    private CareGiversEntity careGiversEntity;  // foreign key identifying the caregiver
    @PropertyOrder(10)
    private String isError;                     // flag to highlight an error with the note
    @PropertyOrder(11)
    private String text;                        // content of the note

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", referencedColumnName = "subject_id")
    public PatientsEntity getPatientsEntity()
    {
        return patientsEntity;
    }

    public void setPatientsEntity(PatientsEntity patientsEntity)
    {
        this.patientsEntity = patientsEntity;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hadm_id", referencedColumnName = "hadm_id")
    public AdmissionsEntity getAdmissionsEntity()
    {
        return admissionsEntity;
    }

    public void setAdmissionsEntity(AdmissionsEntity admissionsEntity)
    {
        this.admissionsEntity = admissionsEntity;
    }

    @Column(name = "chartdate")
    public LocalDateTime getChartDate()
    {
        return chartDate;
    }

    public void setChartDate(LocalDateTime chartDate)
    {
        this.chartDate = chartDate;
    }

    @Column(name = "charttime")
    public LocalDateTime getChartTime()
    {
        return chartTime;
    }

    public void setChartTime(LocalDateTime chartTime)
    {
        this.chartTime = chartTime;
    }

    @Column(name = "storetime")
    public LocalDateTime getStoreTime()
    {
        return storeTime;
    }

    public void setStoreTime(LocalDateTime storeTime)
    {
        this.storeTime = storeTime;
    }

    @Column(name = "category", length = 50)
    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    @Column(name = "description", length = 255)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cgid", referencedColumnName = "cgid")
    public CareGiversEntity getCareGiversEntity()
    {
        return careGiversEntity;
    }

    public void setCareGiversEntity(CareGiversEntity careGiversEntity)
    {
        this.careGiversEntity = careGiversEntity;
    }

    @Column(name = "iserror", length = -1)
    public String getIsError()
    {
        return isError;
    }

    public void setIsError(String isError)
    {
        this.isError = isError;
    }

    @Column(name = "text", length = -1)
    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}
