package com.batch.Reporting.ReportsDTO;

public class Compatibility extends DTO {
    private String prevFamilyCode;
    private String nextFamilyCode;
    private String compatibility;
    private String familyAssociation;
    private String creationDate;
    private String modifyDate;
    private String createdBy;
    private String lastModifiedBy;

    public Compatibility(String prevFamilyCode, String nextFamilyCode, String compatibility, String familyAssociation, String creationDate, String modifyDate, String createdBy, String lastModifiedBy) {
        this.prevFamilyCode = prevFamilyCode;
        this.nextFamilyCode = nextFamilyCode;
        this.compatibility = compatibility;
        this.familyAssociation = familyAssociation;
        this.creationDate = creationDate;
        this.modifyDate = modifyDate;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getPrevFamilyCode() {
        return prevFamilyCode;
    }

    public void setPrevFamilyCode(String prevFamilyCode) {
        this.prevFamilyCode = prevFamilyCode;
    }

    public String getNextFamilyCode() {
        return nextFamilyCode;
    }

    public void setNextFamilyCode(String nextFamilyCode) {
        this.nextFamilyCode = nextFamilyCode;
    }

    public String getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    public String getFamilyAssociation() {
        return familyAssociation;
    }

    public void setFamilyAssociation(String familyAssociation) {
        this.familyAssociation = familyAssociation;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }


}
