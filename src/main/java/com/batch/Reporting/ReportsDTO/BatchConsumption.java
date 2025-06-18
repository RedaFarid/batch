package com.batch.Reporting.ReportsDTO;

public class BatchConsumption extends DTO {
    private String index;
    private String wait;
    private String actionName;
    private String product;
    private String productName;
    private String source;
    private String destination;
    private String setPoint1;
    private String setPoint2;
    private String measure1;
    private String measure2;
    private String tolerance;
    private String unit;
    private String begin;
    private String end;
    private String stepDuration;
    private String state;

    private String batchCode;
    private String processNumber;
    private String blender;
    private String batchDest;
    private String batchProduct;
    private String batchProductDesc;
    private String batchFormula;
    private String batchFormulaDesc;
    private String batchRecipe;
    private String batchRecipeDesc;
    private String reqQty;
    private String producedQty;
    private String batchStart;
    private String batchEnd;
    private String batchDuration;
    private String family;
    private String batchCreate;
    private String batchModify;
    private String batchStatus;
    private String familyCode;
    private String createdBy;
    private String modifiedBy;

    public BatchConsumption(String index, String wait, String actionName,
                            String product, String productName,
                            String source,
                            String destination,
                            String setPoint1, String setPoint2, String measure1, String measure2,
                            String tolerance, String unit, String begin, String end, String stepDuration,
                            String state,
                            String batchCode, String processNumber,
                            String blender, String batchDest,
                            String batchProduct,
                            String batchProductDesc,
                            String batchFormula, String batchFormulaDesc,
                            String batchRecipe,
                            String batchRecipeDesc, String reqQty, String producedQty,
                            String batchStart, String batchEnd,
                            String batchDuration, String family,String batchCreate, String batchModify, String batchStatus, String familyCode
            , String createdBy, String modifiedBy) {
        this.index = index;
        this.wait = wait;
        this.actionName = actionName;
        this.product = product;
        this.productName = productName;
        this.source = source;
        this.destination = destination;
        this.setPoint1 = setPoint1;
        this.setPoint2 = setPoint2;
        this.measure1 = measure1;
        this.measure2 = measure2;
        this.tolerance = tolerance;
        this.unit = unit;
        this.begin = begin;
        this.end = end;
        this.stepDuration = stepDuration;
        this.state = state;
        this.batchCode = batchCode;
        this.processNumber = processNumber;
        this.blender = blender;
        this.batchDest = batchDest;
        this.batchProduct = batchProduct;
        this.batchProductDesc = batchProductDesc;
        this.batchFormula = batchFormula;
        this.batchFormulaDesc = batchFormulaDesc;
        this.batchRecipe = batchRecipe;
        this.batchRecipeDesc = batchRecipeDesc;
        this.reqQty = reqQty;
        this.producedQty = producedQty;
        this.batchStart = batchStart;
        this.batchEnd = batchEnd;
        this.batchDuration = batchDuration;
        this.family = family;
        this.batchCreate = batchCreate;
        this.batchModify = batchModify;
        this.batchStatus = batchStatus;
        this.familyCode = familyCode;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getWait() {
        return wait;
    }

    public void setWait(String wait) {
        this.wait = wait;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSetPoint1() {
        return setPoint1;
    }

    public void setSetPoint1(String setPoint1) {
        this.setPoint1 = setPoint1;
    }

    public String getSetPoint2() {
        return setPoint2;
    }

    public void setSetPoint2(String setPoint2) {
        this.setPoint2 = setPoint2;
    }

    public String getMeasure1() {
        return measure1;
    }

    public void setMeasure1(String measure1) {
        this.measure1 = measure1;
    }

    public String getMeasure2() {
        return measure2;
    }

    public void setMeasure2(String measure2) {
        this.measure2 = measure2;
    }

    public String getTolerance() {
        return tolerance;
    }

    public void setTolerance(String tolerance) {
        this.tolerance = tolerance;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStepDuration() {
        return stepDuration;
    }

    public void setStepDuration(String stepDuration) {
        this.stepDuration = stepDuration;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getProcessNumber() {
        return processNumber;
    }

    public void setProcessNumber(String processNumber) {
        this.processNumber = processNumber;
    }

    public String getBlender() {
        return blender;
    }

    public void setBlender(String blender) {
        this.blender = blender;
    }

    public String getBatchDest() {
        return batchDest;
    }

    public void setBatchDest(String batchDest) {
        this.batchDest = batchDest;
    }

    public String getBatchProduct() {
        return batchProduct;
    }

    public void setBatchProduct(String batchProduct) {
        this.batchProduct = batchProduct;
    }

    public String getBatchProductDesc() {
        return batchProductDesc;
    }

    public void setBatchProductDesc(String batchProductDesc) {
        this.batchProductDesc = batchProductDesc;
    }

    public String getBatchFormula() {
        return batchFormula;
    }

    public void setBatchFormula(String batchFormula) {
        this.batchFormula = batchFormula;
    }

    public String getBatchFormulaDesc() {
        return batchFormulaDesc;
    }

    public void setBatchFormulaDesc(String batchFormulaDesc) {
        this.batchFormulaDesc = batchFormulaDesc;
    }

    public String getBatchRecipe() {
        return batchRecipe;
    }

    public void setBatchRecipe(String batchRecipe) {
        this.batchRecipe = batchRecipe;
    }

    public String getBatchRecipeDesc() {
        return batchRecipeDesc;
    }

    public void setBatchRecipeDesc(String batchRecipeDesc) {
        this.batchRecipeDesc = batchRecipeDesc;
    }

    public String getReqQty() {
        return reqQty;
    }

    public void setReqQty(String reqQty) {
        this.reqQty = reqQty;
    }

    public String getProducedQty() {
        return producedQty;
    }

    public void setProducedQty(String producedQty) {
        this.producedQty = producedQty;
    }

    public String getBatchStart() {
        return batchStart;
    }

    public void setBatchStart(String batchStart) {
        this.batchStart = batchStart;
    }

    public String getBatchEnd() {
        return batchEnd;
    }

    public void setBatchEnd(String batchEnd) {
        this.batchEnd = batchEnd;
    }

    public String getBatchDuration() {
        return batchDuration;
    }

    public void setBatchDuration(String batchDuration) {
        this.batchDuration = batchDuration;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getBatchCreate() {
        return batchCreate;
    }

    public void setBatchCreate(String batchCreate) {
        this.batchCreate = batchCreate;
    }

    public String getBatchModify() {
        return batchModify;
    }

    public void setBatchModify(String batchModify) {
        this.batchModify = batchModify;
    }

    public String getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(String batchStatus) {
        this.batchStatus = batchStatus;
    }

    public String getFamilyCode() {
        return familyCode;
    }

    public void setFamilyCode(String familyCode) {
        this.familyCode = familyCode;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
