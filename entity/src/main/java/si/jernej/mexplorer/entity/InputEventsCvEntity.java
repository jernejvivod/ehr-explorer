package si.jernej.mexplorer.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// Events relating to fluid input for patients whose data was originally stored in the CareVue database.
@Entity
@Table(name = "inputevents_cv", schema = "mimiciii", catalog = "mimic")
public class InputEventsCvEntity extends AEntity
{
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    private IcuStaysEntity icuStaysEntity;      // foreign key identifying the ICU stay
    private LocalDateTime chartTime;            // time that the input was started or received
    private DItemsEntity dItemsEntity;          // foreign key identifying the charted item
    private Double amount;                      // amount of the item administered to the patient
    private String amountUom;                   // unit of measurement for the amount
    private Double rate;                        // rate at which the item is being administered to the patient
    private String rateUom;                     // unit of measurement for the rate
    private LocalDateTime storeTime;            // time when the event was recorded in the system
    private CareGiversEntity careGiversEntity;  // foreign key identifying the caregiver
    private Integer orderId;                    // identifier linking items which are grouped in a solution
    private Integer linkOrderId;                // Identifier linking orders across multiple administrations. LINKORDERID is always equal to the first occurring ORDERID of the series.
    private String stopped;                     // Event was explicitly marked as stopped. Infrequently used by caregivers.
    private Integer newBottle;                  // indicates when a new bottle of the solution was hung at the bedside
    private Double originalAmount;              // Amount of the item which was originally charted
    private String originalAmountUom;           // Amount of the item which was originally charted
    private String originalRoute;               // Route of administration originally chosen for the item
    private Double originalRate;                // Rate of administration originally chosen for the item
    private String originalRateUom;             // unit of measurement for the rate originally chosen
    private String originalSite;                // anatomical site for the original administration of the item

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icustay_id", referencedColumnName = "icustay_id")
    public IcuStaysEntity getIcuStaysEntity()
    {
        return icuStaysEntity;
    }

    public void setIcuStaysEntity(IcuStaysEntity icuStaysEntity)
    {
        this.icuStaysEntity = icuStaysEntity;
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

    @Column(name = "amount", precision = 0)
    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    @Column(name = "amountuom", length = 30)
    public String getAmountUom()
    {
        return amountUom;
    }

    public void setAmountUom(String amountUom)
    {
        this.amountUom = amountUom;
    }

    @Column(name = "rate", precision = 0)
    public Double getRate()
    {
        return rate;
    }

    public void setRate(Double rate)
    {
        this.rate = rate;
    }

    @Column(name = "rateuom", length = 30)
    public String getRateUom()
    {
        return rateUom;
    }

    public void setRateUom(String rateUom)
    {
        this.rateUom = rateUom;
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

    @Column(name = "orderid")
    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Column(name = "linkorderid")
    public Integer getLinkOrderId()
    {
        return linkOrderId;
    }

    public void setLinkOrderId(Integer linkOrderId)
    {
        this.linkOrderId = linkOrderId;
    }

    @Column(name = "stopped", length = 30)
    public String getStopped()
    {
        return stopped;
    }

    public void setStopped(String stopped)
    {
        this.stopped = stopped;
    }

    @Column(name = "newbottle")
    public Integer getNewBottle()
    {
        return newBottle;
    }

    public void setNewBottle(Integer newBottle)
    {
        this.newBottle = newBottle;
    }

    @Column(name = "originalamount", precision = 0)
    public Double getOriginalAmount()
    {
        return originalAmount;
    }

    public void setOriginalAmount(Double originalAmount)
    {
        this.originalAmount = originalAmount;
    }

    @Column(name = "originalamountuom", length = 30)
    public String getOriginalAmountUom()
    {
        return originalAmountUom;
    }

    public void setOriginalAmountUom(String originalAmountUom)
    {
        this.originalAmountUom = originalAmountUom;
    }

    @Column(name = "originalroute", length = 30)
    public String getOriginalRoute()
    {
        return originalRoute;
    }

    public void setOriginalRoute(String originalRoute)
    {
        this.originalRoute = originalRoute;
    }

    @Column(name = "originalrate", precision = 0)
    public Double getOriginalRate()
    {
        return originalRate;
    }

    public void setOriginalRate(Double originalRate)
    {
        this.originalRate = originalRate;
    }

    @Column(name = "originalrateuom", length = 30)
    public String getOriginalRateUom()
    {
        return originalRateUom;
    }

    public void setOriginalRateUom(String originalRateUom)
    {
        this.originalRateUom = originalRateUom;
    }

    @Column(name = "originalsite", length = 30)
    public String getOriginalSite()
    {
        return originalSite;
    }

    public void setOriginalSite(String originalSite)
    {
        this.originalSite = originalSite;
    }
}
