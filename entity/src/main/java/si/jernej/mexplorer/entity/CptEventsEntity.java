package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import si.jernej.mexplorer.entity.annotation.PropertyOrder;

// Events recorded in Current Procedural Terminology.

/**
 * Current Procedural Terminology (CPT) is a standard vocabulary for surgical procedures, minor procedures that physicians perform in the office, radiology tests,
 * and a small number of laboratory tests (approximately 1,000). Whereas hospitals use ICD-9-CM for billing, physicians use CPT to bill for their services.
 */
@Entity
@Table(name = "cptevents", schema = "mimiciii", catalog = "mimic")
public class CptEventsEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private String costCenter;                  // center recording the code, for example the ICU or the respiratory unit
    @PropertyOrder(5)
    private LocalDateTime chartDate;            // date when the event occurred, if available
    @PropertyOrder(6)
    private String cptCd;                       // current Procedural Terminology code
    @PropertyOrder(7)
    private Integer cptNumber;                  // numerical element of the Current Procedural Terminology code
    @PropertyOrder(8)
    private String cptSuffix;                   // text element of the current procedural terminology, if any. Indicates code category.
    @PropertyOrder(9)
    private Integer ticketIdSeq;                // sequence number of the event, derived from the ticked ID
    @PropertyOrder(10)
    private String sectionHeader;               // high-level section of the Current Procedural Terminology code
    @PropertyOrder(11)
    private String subsectionHeader;            // subsection of the Current Procedural Terminology code
    @PropertyOrder(12)
    private String description;                 // description of the Current Procedural Terminology, if available

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

    @Column(name = "costcenter", nullable = false, length = 10)
    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter(String costCenter)
    {
        this.costCenter = costCenter;
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

    @Column(name = "cpt_cd", nullable = false, length = 10)
    public String getCptCd()
    {
        return cptCd;
    }

    public void setCptCd(String cptCd)
    {
        this.cptCd = cptCd;
    }

    @Column(name = "cpt_number")
    public Integer getCptNumber()
    {
        return cptNumber;
    }

    public void setCptNumber(Integer cptNumber)
    {
        this.cptNumber = cptNumber;
    }

    @Column(name = "cpt_suffix", length = 5)
    public String getCptSuffix()
    {
        return cptSuffix;
    }

    public void setCptSuffix(String cptSuffix)
    {
        this.cptSuffix = cptSuffix;
    }

    @Column(name = "ticket_id_seq")
    public Integer getTicketIdSeq()
    {
        return ticketIdSeq;
    }

    public void setTicketIdSeq(Integer ticketIdSeq)
    {
        this.ticketIdSeq = ticketIdSeq;
    }

    @Column(name = "sectionheader", length = 50)
    public String getSectionHeader()
    {
        return sectionHeader;
    }

    public void setSectionHeader(String sectionHeader)
    {
        this.sectionHeader = sectionHeader;
    }

    @Column(name = "subsectionheader", length = 255)
    public String getSubsectionHeader()
    {
        return subsectionHeader;
    }

    public void setSubsectionHeader(String subsectionHeader)
    {
        this.subsectionHeader = subsectionHeader;
    }

    @Column(name = "description", length = 200)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
