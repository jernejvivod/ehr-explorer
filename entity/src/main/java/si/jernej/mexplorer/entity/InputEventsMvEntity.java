package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// Events relating to fluid input for patients whose data was originally stored in the MetaVision database.
@Entity
@Table(name = "inputevents_mv", schema = "mimiciii", catalog = "mimic")
public class InputEventsMvEntity extends AEntity
{
    private PatientsEntity patientsEntity;         // foreign key identifying the patient
    private AdmissionsEntity admissionsEntity;     // foreign key identifying the hospital stay
    private IcuStaysEntity icuStaysEntity;         // foreign key identifying the ICU stay
    private LocalDateTime startTime;               // time when the event started
    private LocalDateTime endTime;                 // time when the event ended
    private DItemsEntity dItemsEntity;             // foreign key identifying the charted item
    private Double amount;                         // amount of the item administered to the patient
    private String amountUom;                      // unit of measurement for the amount
    private Double rate;                           // rate at which the item is being administered to the patient
    private String rateUom;                        // unit of measurement for the rate
    private LocalDateTime storeTime;               // time when the event was recorded in the system
    private CareGiversEntity careGiversEntity;     // foreign key identifying the caregiver
    private Integer orderId;                       // identifier linking items which are grouped in a solution
    private Integer linkOrderId;                   // Identifier linking orders across multiple administrations. LINKORDERID is always equal to the first occuring ORDERID of the series.
    private String orderCategoryName;              // a group which the item corresponds to
    private String secondaryOrderCategoryName;     // a secondary group for those items with more than one grouping possible
    private String orderComponentTypeDescription;  // the role of the item administered in the order
    private String orderCategoryDescription;       // the type of item administered
    private Double patientWeight;                  // for drugs dosed by weight, the value of the weight used in the calculation
    private Double totalAmount;                    // the total amount in the solution for the given item.
    private String totalAmountUom;                 // unit of measurement for the total amount in the solution
    private Short isOpenBag;                       // indicates whether the bag containing the solution is open
    private Short continueInNextDept;              // indicates whether the item will be continued in the next department where the patient is transferred to
    private Short cancelReason;                    // reason for cancellation, if cancelled
    private String statusDescription;              // the current status of the order: stopped, rewritten, running, or cancelled
    private String commentsEditedBy;               // the title of the caregiver who edited the order
    private String commentsCanceledBy;             // the title of the caregiver who canceled the order
    private LocalDateTime commentsDate;            // time at which the caregiver edited or cancelled the order
    private Double originalAmount;                 // amount of the item which was originally charted
    private Double originalRate;                   // rate of administration originally chosen for the item

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

    @Column(name = "starttime")
    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
    }

    @Column(name = "endtime")
    public LocalDateTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime)
    {
        this.endTime = endTime;
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

    @Column(name = "ordercategoryname", length = 100)
    public String getOrderCategoryName()
    {
        return orderCategoryName;
    }

    public void setOrderCategoryName(String orderCategoryName)
    {
        this.orderCategoryName = orderCategoryName;
    }

    @Column(name = "secondaryordercategoryname", length = 100)
    public String getSecondaryOrderCategoryName()
    {
        return secondaryOrderCategoryName;
    }

    public void setSecondaryOrderCategoryName(String secondaryOrderCategoryName)
    {
        this.secondaryOrderCategoryName = secondaryOrderCategoryName;
    }

    @Column(name = "ordercomponenttypedescription", length = 200)
    public String getOrderComponentTypeDescription()
    {
        return orderComponentTypeDescription;
    }

    public void setOrderComponentTypeDescription(String orderComponentTypeDescription)
    {
        this.orderComponentTypeDescription = orderComponentTypeDescription;
    }

    @Column(name = "ordercategorydescription", length = 50)
    public String getOrderCategoryDescription()
    {
        return orderCategoryDescription;
    }

    public void setOrderCategoryDescription(String orderCategoryDescription)
    {
        this.orderCategoryDescription = orderCategoryDescription;
    }

    @Column(name = "patientweight", precision = 0)
    public Double getPatientWeight()
    {
        return patientWeight;
    }

    public void setPatientWeight(Double patientWeight)
    {
        this.patientWeight = patientWeight;
    }

    @Column(name = "totalamount", precision = 0)
    public Double getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    @Column(name = "totalamountuom", length = 50)
    public String getTotalAmountUom()
    {
        return totalAmountUom;
    }

    public void setTotalAmountUom(String totalAmountUom)
    {
        this.totalAmountUom = totalAmountUom;
    }

    @Column(name = "isopenbag")
    public Short getIsOpenBag()
    {
        return isOpenBag;
    }

    public void setIsOpenBag(Short isOpenBag)
    {
        this.isOpenBag = isOpenBag;
    }

    @Column(name = "continueinnextdept")
    public Short getContinueInNextDept()
    {
        return continueInNextDept;
    }

    public void setContinueInNextDept(Short continueInNextDept)
    {
        this.continueInNextDept = continueInNextDept;
    }

    @Column(name = "cancelreason")
    public Short getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(Short cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    @Column(name = "statusdescription", length = 30)
    public String getStatusDescription()
    {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription)
    {
        this.statusDescription = statusDescription;
    }

    @Column(name = "comments_editedby", length = 30)
    public String getCommentsEditedBy()
    {
        return commentsEditedBy;
    }

    public void setCommentsEditedBy(String commentsEditedBy)
    {
        this.commentsEditedBy = commentsEditedBy;
    }

    @Column(name = "comments_canceledby", length = 40)
    public String getCommentsCanceledBy()
    {
        return commentsCanceledBy;
    }

    public void setCommentsCanceledBy(String commentsCanceledBy)
    {
        this.commentsCanceledBy = commentsCanceledBy;
    }

    @Column(name = "comments_date")
    public LocalDateTime getCommentsDate()
    {
        return commentsDate;
    }

    public void setCommentsDate(LocalDateTime commentsDate)
    {
        this.commentsDate = commentsDate;
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

    @Column(name = "originalrate", precision = 0)
    public Double getOriginalRate()
    {
        return originalRate;
    }

    public void setOriginalRate(Double originalRate)
    {
        this.originalRate = originalRate;
    }
}
