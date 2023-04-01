package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import si.jernej.mexplorer.annotation.PropertyOrder;

// Hospital services that patients were under during their hospital stay.
@Entity
@Table(name = "services", schema = "mimiciii", catalog = "mimic")
public class ServicesEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private LocalDateTime transferTime;         // time when the transfer occurred
    @PropertyOrder(5)
    private String prevService;                 // previous service type
    @PropertyOrder(6)
    private String currService;                 // current service type

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

    @Column(name = "transfertime", nullable = false)
    public LocalDateTime getTransferTime()
    {
        return transferTime;
    }

    public void setTransferTime(LocalDateTime transferTime)
    {
        this.transferTime = transferTime;
    }

    @Column(name = "prev_service", length = 20)
    public String getPrevService()
    {
        return prevService;
    }

    public void setPrevService(String prevService)
    {
        this.prevService = prevService;
    }

    @Column(name = "curr_service", length = 20)
    public String getCurrService()
    {
        return currService;
    }

    public void setCurrService(String currService)
    {
        this.currService = currService;
    }
}
