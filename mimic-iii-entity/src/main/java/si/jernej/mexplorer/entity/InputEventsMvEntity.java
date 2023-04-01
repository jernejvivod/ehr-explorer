package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import si.jernej.mexplorer.annotation.PropertyOrder;

// Events relating to fluid input for patients whose data was originally stored in the MetaVision database.
@Entity
@Table(name = "inputevents_mv", schema = "mimiciii", catalog = "mimic")
public class InputEventsMvEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;         // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;     // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private IcuStaysEntity icuStaysEntity;         // foreign key identifying the ICU stay
    @PropertyOrder(5)
    private LocalDateTime startTime;               // time when the event started
    @PropertyOrder(6)
    private LocalDateTime endTime;                 // time when the event ended
    @PropertyOrder(7)
    private DItemsEntity dItemsEntity;             // foreign key identifying the charted item
    @PropertyOrder(8)
    private Double amount;                         // amount of the item administered to the patient
    @PropertyOrder(9)
    private String amountUom;                      // unit of measurement for the amount
    @PropertyOrder(10)
    private Double rate;                           // rate at which the item is being administered to the patient
    @PropertyOrder(11)
    private String rateUom;                        // unit of measurement for the rate
    @PropertyOrder(12)
    private LocalDateTime storeTime;               // time when the event was recorded in the system
    @PropertyOrder(13)
    private CareGiversEntity careGiversEntity;     // foreign key identifying the caregiver
    @PropertyOrder(14)
    private Integer orderId;                       // identifier linking items which are grouped in a solution
    @PropertyOrder(15)
    private Integer linkOrderId;                   // Identifier linking orders across multiple administrations. LINKORDERID is always equal to the first occuring ORDERID of the series.
    @PropertyOrder(16)
    private String orderCategoryName;              // a group which the item corresponds to
    @PropertyOrder(17)
    private String secondaryOrderCategoryName;     // a secondary group for those items with more than one grouping possible
    @PropertyOrder(18)
    private String orderComponentTypeDescription;  // the role of the item administered in the order
    @PropertyOrder(19)
    private String orderCategoryDescription;       // the type of item administered
    @PropertyOrder(20)
    private Double patientWeight;                  // for drugs dosed by weight, the value of the weight used in the calculation
    @PropertyOrder(21)
    private Double totalAmount;                    // the total amount in the solution for the given item.
    @PropertyOrder(22)
    private String totalAmountUom;                 // unit of measurement for the total amount in the solution
    @PropertyOrder(23)
    private Short isOpenBag;                       // indicates whether the bag containing the solution is open
    @PropertyOrder(24)
    private Short continueInNextDept;              // indicates whether the item will be continued in the next department where the patient is transferred to
    @PropertyOrder(25)
    private Short cancelReason;                    // reason for cancellation, if cancelled
    @PropertyOrder(26)
    private String statusDescription;              // the current status of the order: stopped, rewritten, running, or cancelled
    @PropertyOrder(27)
    private String commentsEditedBy;               // the title of the caregiver who edited the order
    @PropertyOrder(28)
    private String commentsCanceledBy;             // the title of the caregiver who canceled the order
    @PropertyOrder(29)
    private LocalDateTime commentsDate;            // time at which the caregiver edited or cancelled the order
    @PropertyOrder(30)
    private Double originalAmount;                 // amount of the item which was originally charted
    @PropertyOrder(31)
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
