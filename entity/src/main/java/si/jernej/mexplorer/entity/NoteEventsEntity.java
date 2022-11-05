package si.jernej.mexplorer.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "noteevents", schema = "mimiciii", catalog = "mimic")
public class NoteEventsEntity implements Serializable
{
    private Long rowId;
    // private Long subjectId;
    private PatientsEntity patientsEntity;
    private AdmissionsEntity admissionsEntity;
    private LocalDateTime chartdate;
    private LocalDateTime charttime;
    private LocalDateTime storetime;
    private String category;
    private String description;
    private Integer cgid;
    private String iserror;
    private String text;

    @Id
    @Column(name = "row_id", nullable = false)
    public Long getRowId()
    {
        return rowId;
    }

    public void setRowId(Long rowId)
    {
        this.rowId = rowId;
    }

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

    @Basic
    @Column(name = "chartdate", nullable = true)
    public LocalDateTime getChartdate()
    {
        return chartdate;
    }

    public void setChartdate(LocalDateTime chartdate)
    {
        this.chartdate = chartdate;
    }

    @Basic
    @Column(name = "charttime", nullable = true)
    public LocalDateTime getCharttime()
    {
        return charttime;
    }

    public void setCharttime(LocalDateTime charttime)
    {
        this.charttime = charttime;
    }

    @Basic
    @Column(name = "storetime", nullable = true)
    public LocalDateTime getStoretime()
    {
        return storetime;
    }

    public void setStoretime(LocalDateTime storetime)
    {
        this.storetime = storetime;
    }

    @Basic
    @Column(name = "category", nullable = true, length = 50)
    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 255)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Basic
    @Column(name = "cgid", nullable = true)
    public Integer getCgid()
    {
        return cgid;
    }

    public void setCgid(Integer cgid)
    {
        this.cgid = cgid;
    }

    @Basic
    @Column(name = "iserror", nullable = true, length = -1)
    public String getIserror()
    {
        return iserror;
    }

    public void setIserror(String iserror)
    {
        this.iserror = iserror;
    }

    @Basic
    @Column(name = "text", nullable = true, length = -1)
    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}
