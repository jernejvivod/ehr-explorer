package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// Record of when patients were ready for discharge (called out), and the actual time of their discharge (or more generally, their outcome).
@Entity
@Table(name = "callout", schema = "mimiciii", catalog = "mimic")
public class CalloutEntity extends AEntity
{
    private PatientsEntity patientsEntity;         // foreign key identifying the patient
    private AdmissionsEntity admissionsEntity;     // foreign key identifying the hospital stay
    private Integer submitWardId;                  // identifier for the ward where the call-out request was submitted
    private String submitCareUnit;                 // if the ward where the call was submitted was an ICU, the ICU type is listed here
    private Integer currWardId;                    // identifies the ward where the patient is currently residing
    private String currCareUnit;                   // if the ward where the patient is currently residing is an ICU, the ICU type is listed here
    private Integer calloutWardId;                 // Identifies the ward where the patient is to be discharged to. A value of 1 indicated the first available ward. A value of 0 indicated home.
    private String calloutService;                 // identifies the type of service that the patient is called out to
    private short requestTele;                     // indicated if special precautions are required
    private short requestResp;                     // indicates if special precautions are required
    private short requestCdiff;                    // indicates if special precautions are required
    private short requestMrsa;                     // indicates if special precautions are required
    private short requestVre;                      // indicates if special precautions are required
    private String calloutStatus;                  // current status of the call-out request
    private String calloutOutcome;                 // the result of the call-out request; either a cancellation or a discharge
    private Integer dischargeWardId;               // the ward to which the patient was discharged
    private String acknowledgeStatus;              // the status of the response to the call-out request
    private LocalDateTime createTime;              // time at which the call-out request was created
    private LocalDateTime updateTime;              // last time at which the call-out request was updated
    private LocalDateTime acknowledgeTime;         // time at which the call-out request was acknowledged
    private LocalDateTime outcomeTime;             // time at which the outcome (cancelled or discharged) occurred
    private LocalDateTime firstReservationTime;    // first time at which a ward was reserved for the call-out request
    private LocalDateTime currentReservationTime;  // latest time at which a ward was reserved for the call-out request

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

    @Column(name = "submit_wardid")
    public Integer getSubmitWardId()
    {
        return submitWardId;
    }

    public void setSubmitWardId(Integer submitWardId)
    {
        this.submitWardId = submitWardId;
    }

    @Column(name = "submit_careunit", length = 15)
    public String getSubmitCareUnit()
    {
        return submitCareUnit;
    }

    public void setSubmitCareUnit(String submitCareUnit)
    {
        this.submitCareUnit = submitCareUnit;
    }

    @Column(name = "curr_wardid")
    public Integer getCurrWardId()
    {
        return currWardId;
    }

    public void setCurrWardId(Integer currWardId)
    {
        this.currWardId = currWardId;
    }

    @Column(name = "curr_careunit", length = 15)
    public String getCurrCareUnit()
    {
        return currCareUnit;
    }

    public void setCurrCareUnit(String currCareUnit)
    {
        this.currCareUnit = currCareUnit;
    }

    @Column(name = "callout_wardid")
    public Integer getCalloutWardId()
    {
        return calloutWardId;
    }

    public void setCalloutWardId(Integer calloutWardId)
    {
        this.calloutWardId = calloutWardId;
    }

    @Column(name = "callout_service", nullable = false, length = 10)
    public String getCalloutService()
    {
        return calloutService;
    }

    public void setCalloutService(String calloutService)
    {
        this.calloutService = calloutService;
    }

    @Column(name = "request_tele", nullable = false)
    public short getRequestTele()
    {
        return requestTele;
    }

    public void setRequestTele(short requestTele)
    {
        this.requestTele = requestTele;
    }

    @Column(name = "request_resp", nullable = false)
    public short getRequestResp()
    {
        return requestResp;
    }

    public void setRequestResp(short requestResp)
    {
        this.requestResp = requestResp;
    }

    @Column(name = "request_cdiff", nullable = false)
    public short getRequestCdiff()
    {
        return requestCdiff;
    }

    public void setRequestCdiff(short requestCdiff)
    {
        this.requestCdiff = requestCdiff;
    }

    @Column(name = "request_mrsa", nullable = false)
    public short getRequestMrsa()
    {
        return requestMrsa;
    }

    public void setRequestMrsa(short requestMrsa)
    {
        this.requestMrsa = requestMrsa;
    }

    @Column(name = "request_vre", nullable = false)
    public short getRequestVre()
    {
        return requestVre;
    }

    public void setRequestVre(short requestVre)
    {
        this.requestVre = requestVre;
    }

    @Column(name = "callout_status", nullable = false, length = 20)
    public String getCalloutStatus()
    {
        return calloutStatus;
    }

    public void setCalloutStatus(String calloutStatus)
    {
        this.calloutStatus = calloutStatus;
    }

    @Column(name = "callout_outcome", nullable = false, length = 20)
    public String getCalloutOutcome()
    {
        return calloutOutcome;
    }

    public void setCalloutOutcome(String calloutOutcome)
    {
        this.calloutOutcome = calloutOutcome;
    }

    @Column(name = "discharge_wardid")
    public Integer getDischargeWardId()
    {
        return dischargeWardId;
    }

    public void setDischargeWardId(Integer dischargeWardId)
    {
        this.dischargeWardId = dischargeWardId;
    }

    @Column(name = "acknowledge_status", nullable = false, length = 20)
    public String getAcknowledgeStatus()
    {
        return acknowledgeStatus;
    }

    public void setAcknowledgeStatus(String acknowledgeStatus)
    {
        this.acknowledgeStatus = acknowledgeStatus;
    }

    @Column(name = "createtime", nullable = false)
    public LocalDateTime getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime)
    {
        this.createTime = createTime;
    }

    @Column(name = "updatetime", nullable = false)
    public LocalDateTime getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime)
    {
        this.updateTime = updateTime;
    }

    @Column(name = "acknowledgetime")
    public LocalDateTime getAcknowledgeTime()
    {
        return acknowledgeTime;
    }

    public void setAcknowledgeTime(LocalDateTime acknowledgeTime)
    {
        this.acknowledgeTime = acknowledgeTime;
    }

    @Column(name = "outcometime", nullable = false)
    public LocalDateTime getOutcomeTime()
    {
        return outcomeTime;
    }

    public void setOutcomeTime(LocalDateTime outcomeTime)
    {
        this.outcomeTime = outcomeTime;
    }

    @Column(name = "firstreservationtime")
    public LocalDateTime getFirstReservationTime()
    {
        return firstReservationTime;
    }

    public void setFirstReservationTime(LocalDateTime firstReservationTime)
    {
        this.firstReservationTime = firstReservationTime;
    }

    @Column(name = "currentreservationtime")
    public LocalDateTime getCurrentReservationTime()
    {
        return currentReservationTime;
    }

    public void setCurrentReservationTime(LocalDateTime currentReservationTime)
    {
        this.currentReservationTime = currentReservationTime;
    }
}
