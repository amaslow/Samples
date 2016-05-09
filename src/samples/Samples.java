/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author AMaslowiec
 */
@Entity
@Table(name = "samples", catalog = "ELRO", schema = "")
@NamedQueries({
    @NamedQuery(name = "Samples.findAll", query = "SELECT s FROM Samples s"),
    @NamedQuery(name = "Samples.findBySample", query = "SELECT s FROM Samples s WHERE s.sample = :sample"),
    @NamedQuery(name = "Samples.findByHidden", query = "SELECT s FROM Samples s WHERE s.hidden = :hidden"),
    @NamedQuery(name = "Samples.findByItemS", query = "SELECT s FROM Samples s WHERE s.itemS = :itemS"),
    @NamedQuery(name = "Samples.findByDescr", query = "SELECT s FROM Samples s WHERE s.descr = :descr"),
    @NamedQuery(name = "Samples.findByMonster", query = "SELECT s FROM Samples s WHERE s.monster = :monster"),
    @NamedQuery(name = "Samples.findByEmail", query = "SELECT s FROM Samples s WHERE s.email = :email"),
    @NamedQuery(name = "Samples.findByReplacement", query = "SELECT s FROM Samples s WHERE s.replacement = :replacement"),
    @NamedQuery(name = "Samples.findByReplacementItem", query = "SELECT s FROM Samples s WHERE s.replacementItem = :replacementItem"),
    @NamedQuery(name = "Samples.findByBuyer", query = "SELECT s FROM Samples s WHERE s.buyer = :buyer"),
    @NamedQuery(name = "Samples.findByContact", query = "SELECT s FROM Samples s WHERE s.contact = :contact"),
    @NamedQuery(name = "Samples.findBySupplier", query = "SELECT s FROM Samples s WHERE s.supplier = :supplier"),
    @NamedQuery(name = "Samples.findByAssortment", query = "SELECT s FROM Samples s WHERE s.assortment = :assortment"),
    @NamedQuery(name = "Samples.findByPromotion", query = "SELECT s FROM Samples s WHERE s.promotion = :promotion"),
    @NamedQuery(name = "Samples.findNewitem", query = "SELECT s FROM Samples s WHERE s.newitem = :newitem"),
    @NamedQuery(name = "Samples.findByDateUpdate", query = "SELECT s FROM Samples s WHERE s.dateUpdate = :dateUpdate"),
    @NamedQuery(name = "Samples.findByCheckCe", query = "SELECT s FROM Samples s WHERE s.checkCe = :checkCe"),
    @NamedQuery(name = "Samples.findByCheckTech", query = "SELECT s FROM Samples s WHERE s.checkTech = :checkTech"),
    @NamedQuery(name = "Samples.findByCheckFunc", query = "SELECT s FROM Samples s WHERE s.checkFunc = :checkFunc"),
    @NamedQuery(name = "Samples.findByLvdCe", query = "SELECT s FROM Samples s WHERE s.lvdCe = :lvdCe"),
    @NamedQuery(name = "Samples.findByLvdTr", query = "SELECT s FROM Samples s WHERE s.lvdTr = :lvdTr"),
    @NamedQuery(name = "Samples.findByLvdStatus", query = "SELECT s FROM Samples s WHERE s.lvdStatus = :lvdStatus"),
    @NamedQuery(name = "Samples.findByEmcCe", query = "SELECT s FROM Samples s WHERE s.emcCe = :emcCe"),
    @NamedQuery(name = "Samples.findByEmcTr", query = "SELECT s FROM Samples s WHERE s.emcTr = :emcTr"),
    @NamedQuery(name = "Samples.findByEmcStatus", query = "SELECT s FROM Samples s WHERE s.emcStatus = :emcStatus"),
    @NamedQuery(name = "Samples.findByRfCe", query = "SELECT s FROM Samples s WHERE s.rfCe = :rfCe"),
    @NamedQuery(name = "Samples.findByRfTr", query = "SELECT s FROM Samples s WHERE s.rfTr = :rfTr"),
    @NamedQuery(name = "Samples.findByRfStatus", query = "SELECT s FROM Samples s WHERE s.rfStatus = :rfStatus"),
    @NamedQuery(name = "Samples.findByRohsCe", query = "SELECT s FROM Samples s WHERE s.rohsCe = :rohsCe"),
    @NamedQuery(name = "Samples.findByRohsTr", query = "SELECT s FROM Samples s WHERE s.rohsTr = :rohsTr"),
    @NamedQuery(name = "Samples.findByRohsStatus", query = "SELECT s FROM Samples s WHERE s.rohsStatus = :rohsStatus"),
    @NamedQuery(name = "Samples.findByErpLightTr", query = "SELECT s FROM Samples s WHERE s.erpLightTr = :erpLightTr"),
    @NamedQuery(name = "Samples.findByErpLightStatus", query = "SELECT s FROM Samples s WHERE s.erpLightStatus = :erpLightStatus"),
    @NamedQuery(name = "Samples.findByPhotoTr", query = "SELECT s FROM Samples s WHERE s.photoTr = :photoTr"),
    @NamedQuery(name = "Samples.findByPhotoStatus", query = "SELECT s FROM Samples s WHERE s.photoStatus = :photoStatus"),
    @NamedQuery(name = "Samples.findByPahCe", query = "SELECT s FROM Samples s WHERE s.pahCe = :pahCe"),
    @NamedQuery(name = "Samples.findByPahStatus", query = "SELECT s FROM Samples s WHERE s.pahStatus = :pahStatus"),
    @NamedQuery(name = "Samples.findByCpdCe", query = "SELECT s FROM Samples s WHERE s.cpdCe = :cpdCe"),
    @NamedQuery(name = "Samples.findByCpdStatus", query = "SELECT s FROM Samples s WHERE s.cpdStatus = :cpdStatus"),
    @NamedQuery(name = "Samples.findByPsLvd", query = "SELECT s FROM Samples s WHERE s.psLvd = :psLvd"),
    @NamedQuery(name = "Samples.findByPsLvdStatus", query = "SELECT s FROM Samples s WHERE s.psLvdStatus = :psLvdStatus"),
    @NamedQuery(name = "Samples.findByPsEmc", query = "SELECT s FROM Samples s WHERE s.psEmc = :psEmc"),
    @NamedQuery(name = "Samples.findByPsEmcStatus", query = "SELECT s FROM Samples s WHERE s.psEmcStatus = :psEmcStatus"),
    @NamedQuery(name = "Samples.findByPsRohs", query = "SELECT s FROM Samples s WHERE s.psRohs = :psRohs"),
    @NamedQuery(name = "Samples.findByPsRohsStatus", query = "SELECT s FROM Samples s WHERE s.psRohsStatus = :psRohsStatus"),
    @NamedQuery(name = "Samples.findByPsErp", query = "SELECT s FROM Samples s WHERE s.psErp = :psErp"),
    @NamedQuery(name = "Samples.findByPsErpStatus", query = "SELECT s FROM Samples s WHERE s.psErpStatus = :psErpStatus"),
    @NamedQuery(name = "Samples.findByBatteryCe", query = "SELECT s FROM Samples s WHERE s.batteryCe = :batteryCe"),
    @NamedQuery(name = "Samples.findByBatteryStatus", query = "SELECT s FROM Samples s WHERE s.batteryStatus = :batteryStatus"),
    @NamedQuery(name = "Samples.findOther1", query = "SELECT s FROM Samples s WHERE s.other1 = :other1"),
    @NamedQuery(name = "Samples.findOther1Text", query = "SELECT s FROM Samples s WHERE s.other1Text = :other1Text"),
    @NamedQuery(name = "Samples.findOther1Status", query = "SELECT s FROM Samples s WHERE s.other1Status = :other1Status"),
    @NamedQuery(name = "Samples.findOther2", query = "SELECT s FROM Samples s WHERE s.other2 = :other2"),
    @NamedQuery(name = "Samples.findOther2Text", query = "SELECT s FROM Samples s WHERE s.other2Text = :other2Text"),
    @NamedQuery(name = "Samples.findOther2Status", query = "SELECT s FROM Samples s WHERE s.other2Status = :other2Status"),
    @NamedQuery(name = "Samples.findOther3", query = "SELECT s FROM Samples s WHERE s.other3 = :other3"),
    @NamedQuery(name = "Samples.findOther3Text", query = "SELECT s FROM Samples s WHERE s.other3Text = :other3Text"),
    @NamedQuery(name = "Samples.findOther3Status", query = "SELECT s FROM Samples s WHERE s.other3Status = :other3Status"),
    @NamedQuery(name = "Samples.findOther4", query = "SELECT s FROM Samples s WHERE s.other4 = :other4"),
    @NamedQuery(name = "Samples.findOther4Text", query = "SELECT s FROM Samples s WHERE s.other4Text = :other4Text"),
    @NamedQuery(name = "Samples.findOther4Status", query = "SELECT s FROM Samples s WHERE s.other4Status = :other4Status"),
    @NamedQuery(name = "Samples.findOther5", query = "SELECT s FROM Samples s WHERE s.other5 = :other5"),
    @NamedQuery(name = "Samples.findOther5Text", query = "SELECT s FROM Samples s WHERE s.other5Text = :other5Text"),
    @NamedQuery(name = "Samples.findOther5Status", query = "SELECT s FROM Samples s WHERE s.other5Status = :other5Status"),
    @NamedQuery(name = "Samples.findByConclusionText", query = "SELECT s FROM Samples s WHERE s.conclusionText = :conclusionText"),
    @NamedQuery(name = "Samples.findByConclusion", query = "SELECT s FROM Samples s WHERE s.conclusion = :conclusion"),
    @NamedQuery(name = "Samples.findByIssues", query = "SELECT s FROM Samples s WHERE s.issues = :issues"),
    @NamedQuery(name = "Samples.findByDropbox", query = "SELECT s FROM Samples s WHERE s.dropbox = :dropbox"),
    @NamedQuery(name = "Samples.findByDateCheck", query = "SELECT s FROM Samples s WHERE s.dateCheck = :dateCheck"),
    @NamedQuery(name = "Samples.findByDateCreat", query = "SELECT s FROM Samples s WHERE s.dateCreat = :dateCreat")})
public class Samples implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "SAMPLE")
    private String sample;
    @Column(name = "HIDDEN")
    private boolean hidden;
    @Column(name = "ITEM_S")
    private String itemS;
    @Column(name = "DESCR")
    private String descr;
    @Column(name = "MONSTER")
    private String monster;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "REPLACEMENT")
    private boolean replacement;
    @Column(name = "REPLACEMENT_ITEM")
    private String replacementItem;
    @Column(name = "BUYER")
    private String buyer;
    @Column(name = "CONTACT")
    private String contact;
    @Column(name = "SUPPLIER")
    private String supplier;
    @Column(name = "ASSORTMENT")
    private boolean assortment;
    @Column(name = "PROMOTION")
    private boolean promotion;
    @Column(name = "NEWITEM")
    private boolean newitem;
    @Column(name = "DATE_UPDATE")
    @Temporal(TemporalType.DATE)
    private Date dateUpdate;
    @Column(name = "CHECK_CE")
    private boolean checkCe;
    @Column(name = "CHECK_TECH")
    private boolean checkTech;
    @Column(name = "CHECK_FUNC")
    private boolean checkFunc;
    @Column(name = "LVD_CE")
    private boolean lvdCe;
    @Column(name = "LVD_TR")
    private boolean lvdTr;
    @Column(name = "LVD_STATUS")
    private String lvdStatus;
    @Column(name = "EMC_CE")
    private boolean emcCe;
    @Column(name = "EMC_TR")
    private boolean emcTr;
    @Column(name = "EMC_STATUS")
    private String emcStatus;
    @Column(name = "RF_CE")
    private boolean rfCe;
    @Column(name = "RF_TR")
    private boolean rfTr;
    @Column(name = "RF_STATUS")
    private String rfStatus;
    @Column(name = "ROHS_CE")
    private boolean rohsCe;
    @Column(name = "ROHS_TR")
    private boolean rohsTr;
    @Column(name = "ROHS_STATUS")
    private String rohsStatus;
    @Column(name = "ERP_LIGHT_TR")
    private boolean erpLightTr;
    @Column(name = "ERP_LIGHT_STATUS")
    private String erpLightStatus;
    @Column(name = "PHOTO_TR")
    private boolean photoTr;
    @Column(name = "PHOTO_STATUS")
    private String photoStatus;
    @Column(name = "PAH_CE")
    private boolean pahCe;
    @Column(name = "PAH_STATUS")
    private String pahStatus;
    @Column(name = "CPD_CE")
    private boolean cpdCe;
    @Column(name = "CPD_STATUS")
    private String cpdStatus;
    @Column(name = "PS_LVD")
    private boolean psLvd;
    @Column(name = "PS_LVD_STATUS")
    private String psLvdStatus;
    @Column(name = "PS_EMC")
    private boolean psEmc;
    @Column(name = "PS_EMC_STATUS")
    private String psEmcStatus;
    @Column(name = "PS_ROHS")
    private boolean psRohs;
    @Column(name = "PS_ROHS_STATUS")
    private String psRohsStatus;
    @Column(name = "PS_ERP")
    private boolean psErp;
    @Column(name = "PS_ERP_STATUS")
    private String psErpStatus;
    @Column(name = "BATTERY_CE")
    private boolean batteryCe;
    @Column(name = "BATTERY_STATUS")
    private String batteryStatus;
    @Column(name = "OTHER1")
    private boolean other1;
    @Column(name = "OTHER1_TEXT")
    private String other1Text;
    @Column(name = "OTHER1_STATUS")
    private String other1Status;
    @Column(name = "OTHER2")
    private boolean other2;
    @Column(name = "OTHER2_TEXT")
    private String other2Text;
    @Column(name = "OTHER2_STATUS")
    private String other2Status;
    @Column(name = "OTHER3")
    private boolean other3;
    @Column(name = "OTHER3_TEXT")
    private String other3Text;
    @Column(name = "OTHER3_STATUS")
    private String other3Status;
    @Column(name = "OTHER4")
    private boolean other4;
    @Column(name = "OTHER4_TEXT")
    private String other4Text;
    @Column(name = "OTHER4_STATUS")
    private String other4Status;
    @Column(name = "OTHER5")
    private boolean other5;
    @Column(name = "OTHER5_TEXT")
    private String other5Text;
    @Column(name = "OTHER5_STATUS")
    private String other5Status;
    @Column(name = "CONCLUSION_TEXT")
    private String conclusionText;
    @Column(name = "CONCLUSION")
    private boolean conclusion;
    @Column(name = "ISSUES")
    private String issues;
    @Column(name = "DROPBOX")
    private String dropbox;
    @Column(name = "DATE_CHECK")
    @Temporal(TemporalType.DATE)
    private Date dateCheck;
    @Column(name = "DATE_CREAT")
    private String dateCreat;

    public Samples() {
    }

    public Samples(String sample) {
        this.sample = sample;
    }

    public Samples(String sample, boolean hidden, boolean replacement, boolean assortment, boolean promotion, boolean newitem,
            boolean checkCe, boolean checkTech, boolean checkFunc, boolean lvdCe, boolean lvdTr, boolean emcCe, 
            boolean emcTr, boolean rfCe, boolean rfTr, boolean rohsCe, boolean rohsTr, 
            boolean erpLightTr, boolean photoTr, boolean pahCe, boolean cpdCe, boolean psLvd, 
            boolean psEmc, boolean psRohs, boolean psErp, boolean batteryCe, boolean other1, boolean other2,
            boolean other3, boolean other4, boolean other5, boolean conclusion) {
        this.sample = sample;
        this.hidden = hidden;
        this.replacement = replacement;
        this.assortment = assortment;
        this.promotion = promotion;
        this.newitem = newitem;
        this.checkCe = checkCe;
        this.checkTech = checkTech;
        this.checkFunc = checkFunc;
        this.lvdCe = lvdCe;
        this.lvdTr = lvdTr;
        this.emcCe = emcCe;
        this.emcTr = emcTr;
        this.rfCe = rfCe;
        this.rfTr = rfTr;
        this.rohsCe = rohsCe;
        this.rohsTr = rohsTr;
        this.erpLightTr = erpLightTr;
        this.photoTr = photoTr;
        this.pahCe = pahCe;
        this.cpdCe = cpdCe;
        this.psLvd = psLvd;
        this.psEmc = psLvd;
        this.psRohs = psRohs;
        this.psErp = psErp ;
        this.batteryCe = batteryCe;
        this.other1 = other1;
        this.other2 = other2;
        this.other3 = other3;
        this.other4 = other4;
        this.other5 = other5;
        this.conclusion = conclusion;
    }
    
    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        String oldSample = this.sample;
        this.sample = sample;
        changeSupport.firePropertyChange("sample", oldSample, sample);
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        boolean oldHidden = this.hidden;
        this.hidden = hidden;
        changeSupport.firePropertyChange("hidden", oldHidden, hidden);
    }
    
    public String getItemS() {
        return itemS;
    }

    public void setItemS(String itemS) {
        String oldItemS = this.itemS;
        this.itemS = itemS;
        changeSupport.firePropertyChange("itemS", oldItemS, itemS);
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        String oldDescr = this.descr;
        this.descr = descr;
        changeSupport.firePropertyChange("descr", oldDescr, descr);
    }
    
    public String getMonster() {
        return monster;
    }

    public void setMonster(String monster) {
        String oldMonster = this.monster;
        this.monster = monster;
        changeSupport.firePropertyChange("monster", oldMonster, monster);
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String oldEmail = this.email;
        this.email = email;
        changeSupport.firePropertyChange("email", oldEmail, email);
    }
    
    public boolean getReplacement() {
        return replacement;
    }

    public void setReplacement(boolean replacement) {
        boolean oldReplacement = this.replacement;
        this.replacement = replacement;
        changeSupport.firePropertyChange("replacement", oldReplacement, replacement);
    }

    public String getReplacementItem() {
        return replacementItem;
    }

    public void setReplacementItem(String replacementItem) {
        String oldReplacementItem = this.replacementItem;
        this.replacementItem = replacementItem;
        changeSupport.firePropertyChange("replacementItem", oldReplacementItem, replacementItem);
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        String oldBuyer = this.buyer;
        this.buyer = buyer;
        changeSupport.firePropertyChange("buyer", oldBuyer, buyer);
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        String oldContact = this.contact;
        this.contact = contact;
        changeSupport.firePropertyChange("contact", oldContact, contact);
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        String oldSupplier = this.supplier;
        this.supplier = supplier;
        changeSupport.firePropertyChange("supplier", oldSupplier, supplier);
    }

    public boolean getAssortment() {
        return assortment;
    }

    public void setAssortment(boolean assortment) {
        boolean oldAssortment = this.assortment;
        this.assortment = assortment;
        changeSupport.firePropertyChange("assortment", oldAssortment, assortment);
    }

    public boolean getPromotion() {
        return promotion;
    }

    public void setPromotion(boolean promotion) {
        boolean oldPromotion = this.promotion;
        this.promotion = promotion;
        changeSupport.firePropertyChange("promotion", oldPromotion, promotion);
    }

     public boolean getNewitem() {
        return newitem;
    }

    public void setNewitem(boolean newitem) {
        boolean oldNewitem = this.newitem;
        this.newitem = newitem;
        changeSupport.firePropertyChange("newitem", oldNewitem, newitem);
    }
    public Date getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(Date dateUpdate) {
        Date oldDateUpdate = this.dateUpdate;
        this.dateUpdate = dateUpdate;
        changeSupport.firePropertyChange("dateUpdate", oldDateUpdate, dateUpdate);
    }

    public boolean getCheckCe() {
        return checkCe;
    }

    public void setCheckCe(boolean checkCe) {
        boolean oldCheckCe = this.checkCe;
        this.checkCe = checkCe;
        changeSupport.firePropertyChange("checkCe", oldCheckCe, checkCe);
    }

    public boolean getCheckTech() {
        return checkTech;
    }

    public void setCheckTech(boolean checkTech) {
        boolean oldCheckTech = this.checkTech;
        this.checkTech = checkTech;
        changeSupport.firePropertyChange("checkTech", oldCheckTech, checkTech);
    }

    public boolean getCheckFunc() {
        return checkFunc;
    }

    public void setCheckFunc(boolean checkFunc) {
        boolean oldCheckFunc = this.checkFunc;
        this.checkFunc = checkFunc;
        changeSupport.firePropertyChange("checkFunc", oldCheckFunc, checkFunc);
    }

    public boolean getLvdCe() {
        return lvdCe;
    }

    public void setLvdCe(boolean lvdCe) {
        boolean oldLvdCe = this.lvdCe;
        this.lvdCe = lvdCe;
        changeSupport.firePropertyChange("lvdCe", oldLvdCe, lvdCe);
    }

    public boolean getLvdTr() {
        return lvdTr;
    }

    public void setLvdTr(boolean lvdTr) {
        boolean oldLvdTr = this.lvdTr;
        this.lvdTr = lvdTr;
        changeSupport.firePropertyChange("lvdTr", oldLvdTr, lvdTr);
    }

    public String getLvdStatus() {
        return lvdStatus;
    }

    public void setLvdStatus(String lvdStatus) {
        String oldLvdStatus = this.lvdStatus;
        this.lvdStatus = lvdStatus;
        changeSupport.firePropertyChange("lvdStatus", oldLvdStatus, lvdStatus);
    }

    public boolean getEmcCe() {
        return emcCe;
    }

    public void setEmcCe(boolean emcCe) {
        boolean oldEmcCe = this.emcCe;
        this.emcCe = emcCe;
        changeSupport.firePropertyChange("emcCe", oldEmcCe, emcCe);
    }

    public boolean getEmcTr() {
        return emcTr;
    }

    public void setEmcTr(boolean emcTr) {
        boolean oldEmcTr = this.emcTr;
        this.emcTr = emcTr;
        changeSupport.firePropertyChange("emcTr", oldEmcTr, emcTr);
    }

    public String getEmcStatus() {
        return emcStatus;
    }

    public void setEmcStatus(String emcStatus) {
        String oldEmcStatus = this.emcStatus;
        this.emcStatus = emcStatus;
        changeSupport.firePropertyChange("emcStatus", oldEmcStatus, emcStatus);
    }

    public boolean getRfCe() {
        return rfCe;
    }

    public void setRfCe(boolean rfCe) {
        boolean oldRfCe = this.rfCe;
        this.rfCe = rfCe;
        changeSupport.firePropertyChange("rfCe", oldRfCe, rfCe);
    }

    public boolean getRfTr() {
        return rfTr;
    }

    public void setRfTr(boolean rfTr) {
        boolean oldRfTr = this.rfTr;
        this.rfTr = rfTr;
        changeSupport.firePropertyChange("rfTr", oldRfTr, rfTr);
    }

    public String getRfStatus() {
        return rfStatus;
    }

    public void setRfStatus(String rfStatus) {
        String oldRfStatus = this.rfStatus;
        this.rfStatus = rfStatus;
        changeSupport.firePropertyChange("rfStatus", oldRfStatus, rfStatus);
    }

    public boolean getRohsCe() {
        return rohsCe;
    }

    public void setRohsCe(boolean rohsCe) {
        boolean oldRohsCe = this.rohsCe;
        this.rohsCe = rohsCe;
        changeSupport.firePropertyChange("rohsCe", oldRohsCe, rohsCe);
    }

    public boolean getRohsTr() {
        return rohsTr;
    }

    public void setRohsTr(boolean rohsTr) {
        boolean oldRohsTr = this.rohsTr;
        this.rohsTr = rohsTr;
        changeSupport.firePropertyChange("rohsTr", oldRohsTr, rohsTr);
    }

    public String getRohsStatus() {
        return rohsStatus;
    }

    public void setRohsStatus(String rohsStatus) {
        String oldRohsStatus = this.rohsStatus;
        this.rohsStatus = rohsStatus;
        changeSupport.firePropertyChange("rohsStatus", oldRohsStatus, rohsStatus);
    }

    public boolean getErpLightTr() {
        return erpLightTr;
    }

    public void setErpLightTr(boolean erpLightTr) {
        boolean oldErpLightTr = this.erpLightTr;
        this.erpLightTr = erpLightTr;
        changeSupport.firePropertyChange("erpLightTr", oldErpLightTr, erpLightTr);
    }

    public String getErpLightStatus() {
        return erpLightStatus;
    }

    public void setErpLightStatus(String erpLightStatus) {
        String oldErpLightStatus = this.erpLightStatus;
        this.erpLightStatus = erpLightStatus;
        changeSupport.firePropertyChange("erpLightStatus", oldErpLightStatus, erpLightStatus);
    }

    public boolean getPhotoTr() {
        return photoTr;
    }

    public void setPhotoTr(boolean photoTr) {
        boolean oldPhotoTr = this.photoTr;
        this.photoTr = photoTr;
        changeSupport.firePropertyChange("photoTr", oldPhotoTr, photoTr);
    }

    public String getPhotoStatus() {
        return photoStatus;
    }

    public void setPhotoStatus(String photoStatus) {
        String oldPhotoStatus = this.photoStatus;
        this.photoStatus = photoStatus;
        changeSupport.firePropertyChange("photoStatus", oldPhotoStatus, photoStatus);
    }

    public boolean getPahCe() {
        return pahCe;
    }

    public void setPahCe(boolean pahCe) {
        boolean oldPahCe = this.pahCe;
        this.pahCe = pahCe;
        changeSupport.firePropertyChange("pahCe", oldPahCe, pahCe);
    }

    public String getPahStatus() {
        return pahStatus;
    }

    public void setPahStatus(String pahStatus) {
        String oldPahStatus = this.pahStatus;
        this.pahStatus = pahStatus;
        changeSupport.firePropertyChange("pahStatus", oldPahStatus, pahStatus);
    }

    public boolean getCpdCe() {
        return cpdCe;
    }

    public void setCpdCe(boolean cpdCe) {
        boolean oldCpdCe = this.cpdCe;
        this.cpdCe = cpdCe;
        changeSupport.firePropertyChange("cpdCe", oldCpdCe, cpdCe);
    }

    public String getCpdStatus() {
        return cpdStatus;
    }

    public void setCpdStatus(String cpdStatus) {
        String oldCpdStatus = this.cpdStatus;
        this.cpdStatus = cpdStatus;
        changeSupport.firePropertyChange("cpdStatus", oldCpdStatus, cpdStatus);
    }

    public boolean getPsLvd() {
        return psLvd;
    }

    public void setPsLvd(boolean psLvd) {
        boolean oldPsLvd = this.psLvd;
        this.psLvd = psLvd;
        changeSupport.firePropertyChange("psLvd", oldPsLvd, psLvd);
    }

    public String getPsLvdStatus() {
        return psLvdStatus;
    }

    public void setPsLvdStatus(String psLvdStatus) {
        String oldPsLvdStatus = this.psLvdStatus;
        this.psLvdStatus = psLvdStatus;
        changeSupport.firePropertyChange("psLvdStatus", oldPsLvdStatus, psLvdStatus);
    }

    public boolean getPsEmc() {
        return psEmc;
    }

    public void setPsEmc(boolean psEmc) {
        boolean oldPsEmc = this.psEmc;
        this.psEmc = psEmc;
        changeSupport.firePropertyChange("psEmc", oldPsEmc, psEmc);
    }

    public String getPsEmcStatus() {
        return psEmcStatus;
    }

    public void setPsEmcStatus(String psEmcStatus) {
        String oldPsEmcStatus = this.psEmcStatus;
        this.psEmcStatus = psEmcStatus;
        changeSupport.firePropertyChange("psEmcStatus", oldPsEmcStatus, psEmcStatus);
    }

    public boolean getPsRohs() {
        return psRohs;
    }

    public void setPsRohs(boolean psRohs) {
        boolean oldPsRohs = this.psRohs;
        this.psRohs = psRohs;
        changeSupport.firePropertyChange("psRohs", oldPsRohs, psRohs);
    }

    public String getPsRohsStatus() {
        return psRohsStatus;
    }

    public void setPsRohsStatus(String psRohsStatus) {
        String oldPsRohsStatus = this.psRohsStatus;
        this.psRohsStatus = psRohsStatus;
        changeSupport.firePropertyChange("psRohsStatus", oldPsRohsStatus, psRohsStatus);
    }

    public boolean getPsErp() {
        return psErp;
    }

    public void setPsErp(boolean psErp) {
        boolean oldPsErp = this.psErp;
        this.psErp = psErp;
        changeSupport.firePropertyChange("psErp", oldPsErp, psErp);
    }

    public String getPsErpStatus() {
        return psErpStatus;
    }

    public void setPsErpStatus(String psErpStatus) {
        String oldPsErpStatus = this.psErpStatus;
        this.psErpStatus = psErpStatus;
        changeSupport.firePropertyChange("psErpStatus", oldPsErpStatus, psErpStatus);
    }

    public boolean getBatteryCe() {
        return batteryCe;
    }

    public void setBatteryCe(boolean batteryCe) {
        boolean oldBatteryCe = this.batteryCe;
        this.batteryCe = batteryCe;
        changeSupport.firePropertyChange("batteryCe", oldBatteryCe, batteryCe);
    }

    public String getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(String batteryStatus) {
        String oldBatteryStatus = this.batteryStatus;
        this.batteryStatus = batteryStatus;
        changeSupport.firePropertyChange("batteryStatus", oldBatteryStatus, batteryStatus);
    }
    
    public boolean getOther1() {
        return other1;
    }

    public void setOther1(boolean other1) {
        boolean oldOther1 = this.other1;
        this.other1 = other1;
        changeSupport.firePropertyChange("other1", oldOther1, other1);
    }

    public String getOther1Text() {
        return other1Text;
    }

    public void setOther1Text(String other1Text) {
        String oldOther1Text = this.other1Text;
        this.other1Text = other1Text;
        changeSupport.firePropertyChange("other1Text", oldOther1Text, other1Text);
    }
    
    public String getOther1Status() {
        return other1Status;
    }

    public void setOther1Status(String other1Status) {
        String oldOther1Status = this.other1Status;
        this.other1Status = other1Status;
        changeSupport.firePropertyChange("other1Status", oldOther1Status, other1Status);
    }
    
    public boolean getOther2() {
        return other2;
    }

    public void setOther2(boolean other2) {
        boolean oldOther2 = this.other2;
        this.other2 = other2;
        changeSupport.firePropertyChange("other2", oldOther2, other2);
    }

    public String getOther2Text() {
        return other2Text;
    }

    public void setOther2Text(String other2Text) {
        String oldOther2Text = this.other2Text;
        this.other2Text = other2Text;
        changeSupport.firePropertyChange("other2Text", oldOther2Text, other2Text);
    }
    
    public String getOther2Status() {
        return other2Status;
    }

    public void setOther2Status(String other2Status) {
        String oldOther2Status = this.other2Status;
        this.other2Status = other2Status;
        changeSupport.firePropertyChange("other2Status", oldOther2Status, other2Status);
    }
    
    public boolean getOther3() {
        return other3;
    }

    public void setOther3(boolean other3) {
        boolean oldOther3 = this.other3;
        this.other3 = other3;
        changeSupport.firePropertyChange("other3", oldOther3, other3);
    }

    public String getOther3Text() {
        return other3Text;
    }

    public void setOther3Text(String other3Text) {
        String oldOther3Text = this.other3Text;
        this.other3Text = other3Text;
        changeSupport.firePropertyChange("other3Text", oldOther3Text, other3Text);
    }
    
    public String getOther3Status() {
        return other3Status;
    }

    public void setOther3Status(String other3Status) {
        String oldOther3Status = this.other3Status;
        this.other3Status = other3Status;
        changeSupport.firePropertyChange("other3Status", oldOther3Status, other3Status);
    }
    
    public boolean getOther4() {
        return other4;
    }

    public void setOther4(boolean other4) {
        boolean oldOther4 = this.other4;
        this.other4 = other4;
        changeSupport.firePropertyChange("other4", oldOther4, other4);
    }

    public String getOther4Text() {
        return other4Text;
    }

    public void setOther4Text(String other4Text) {
        String oldOther4Text = this.other4Text;
        this.other4Text = other4Text;
        changeSupport.firePropertyChange("other4Text", oldOther4Text, other4Text);
    }
    
    public String getOther4Status() {
        return other4Status;
    }

    public void setOther4Status(String other4Status) {
        String oldOther4Status = this.other4Status;
        this.other4Status = other4Status;
        changeSupport.firePropertyChange("other4Status", oldOther4Status, other4Status);
    }
    
    public boolean getOther5() {
        return other5;
    }

    public void setOther5(boolean other5) {
        boolean oldOther5 = this.other5;
        this.other5 = other5;
        changeSupport.firePropertyChange("other5", oldOther5, other5);
    }

    public String getOther5Text() {
        return other5Text;
    }

    public void setOther5Text(String other5Text) {
        String oldOther5Text = this.other5Text;
        this.other5Text = other5Text;
        changeSupport.firePropertyChange("other5Text", oldOther5Text, other5Text);
    }
    
    public String getOther5Status() {
        return other5Status;
    }

    public void setOther5Status(String other5Status) {
        String oldOther5Status = this.other5Status;
        this.other5Status = other5Status;
        changeSupport.firePropertyChange("other5Status", oldOther5Status, other5Status);
    }
    
    public String getConclusionText() {
        return conclusionText;
    }

    public void setConclusionText(String conclusionText) {
        String oldConclusionText = this.conclusionText;
        this.conclusionText = conclusionText;
        changeSupport.firePropertyChange("conclusionText", oldConclusionText, conclusionText);
    }

    public boolean getConclusion() {
        return conclusion;
    }

    public void setConclusion(boolean conclusion) {
        boolean oldConclusion = this.conclusion;
        this.conclusion = conclusion;
        changeSupport.firePropertyChange("conclusion", oldConclusion, conclusion);
    }

    public String getIssues() {
        return issues;
    }

    public void setIssues(String issues) {
        String oldIssues = this.issues;
        this.issues = issues;
        changeSupport.firePropertyChange("issues", oldIssues, issues);
    }

      public String getDropbox() {
        return dropbox;
    }

    public void setDropbox(String dropbox) {
        String oldDropbox = this.dropbox;
        this.dropbox = dropbox;
        changeSupport.firePropertyChange("dropbox", oldDropbox, dropbox);
    }
    
    
    public Date getDateCheck() {
        return dateCheck;
    }

    public void setDateCheck(Date dateCheck) {
        Date oldDateCheck = this.dateCheck;
        this.dateCheck = dateCheck;
        changeSupport.firePropertyChange("dateCheck", oldDateCheck, dateCheck);
    }

    public String getDateCreat() {
        return dateCreat;
    }

    public void setDateCreat(String dateCreat) {
        String oldDateCreat = this.dateCreat;
        this.dateCreat = dateCreat;
        changeSupport.firePropertyChange("dateCreat", oldDateCreat, dateCreat);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sample != null ? sample.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Samples)) {
            return false;
        }
        Samples other = (Samples) object;
        if ((this.sample == null && other.sample != null) || (this.sample != null && !this.sample.equals(other.sample))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "samples.Samples[ sample=" + sample + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
