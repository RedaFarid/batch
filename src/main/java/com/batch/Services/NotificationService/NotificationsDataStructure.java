
package com.batch.Services.NotificationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NotificationsDataStructure {
    private final Map<String, Map<String, ErrorObject>> allAlarmsCache = new ConcurrentHashMap();
    private final List<ServiceErrorsListener> serviceErrorsListeners = new LinkedList();

    public Map<String, Map<String, ErrorObject>> getAllAlarmsCache() {
        return this.allAlarmsCache;
    }

    public void addServicePartition(String service) {
        this.allAlarmsCache.put(service, new HashMap());
        this.serviceErrorsListeners.forEach((listener) -> listener.newServiceAdded(service));
    }

    public void addFamilyPartition(String service, String familyName, AddErrorEvent addErrorEvent, ResetEvent resetEvent) {
        if (this.allAlarmsCache.containsKey(service)) {
            Map<String, ErrorObject> stringErrorObjectMap = (Map)this.allAlarmsCache.get(service);
            ErrorObject errorObject = new ErrorObject(service, familyName, resetEvent, addErrorEvent);
            stringErrorObjectMap.put(familyName, errorObject);
            this.serviceErrorsListeners.forEach((listener) -> listener.newFamilyAdded(service, familyName, errorObject));
        }

    }

    public void addErrorMessage(String serviceName, String familyName, MessageObject errorMessage, AddErrorEvent addErrorEvent, ResetEvent resetEvent) {
        if (this.allAlarmsCache.containsKey(serviceName)) {
            Map<String, ErrorObject> stringErrorObjectMap = (Map)this.allAlarmsCache.get(serviceName);
            if (stringErrorObjectMap.containsKey(familyName)) {
                ErrorObject errorObject = (ErrorObject)stringErrorObjectMap.get(familyName);
                if (errorObject != null) {
                    boolean contains = errorObject.getErrorMessageList().contains(errorMessage.getMessage());
                    errorObject.addError(errorMessage);
                    if (!contains) {
                        this.serviceErrorsListeners.forEach((listener) -> {
                            listener.newErrorMessageAdded(serviceName, familyName, errorMessage.getMessage(), errorObject);
                            listener.newErrorMessagePopUpOnly(serviceName, familyName, errorMessage.getMessage());
                        });
                    }
                }
            } else {
                this.addFamilyPartition(serviceName, familyName, addErrorEvent, resetEvent);
                this.addErrorMessage(serviceName, familyName, errorMessage, addErrorEvent, resetEvent);
            }
        } else {
            this.addServicePartition(serviceName);
            this.addErrorMessage(serviceName, familyName, errorMessage, addErrorEvent, resetEvent);
        }

    }

    public void removeServicePartition(String serviceName) {
        this.allAlarmsCache.remove(serviceName);
        this.serviceErrorsListeners.forEach((listener) -> listener.serviceRemoved(serviceName));
    }

    public void removeFamilyPartition(String serviceName, String familyName) {
        Map<String, ErrorObject> stringErrorObjectMap = (Map)this.allAlarmsCache.get(serviceName);
        stringErrorObjectMap.remove(familyName);
        if (stringErrorObjectMap.size() == 0) {
            this.removeServicePartition(serviceName);
        }

        this.serviceErrorsListeners.forEach((listener) -> listener.familyRemoved(serviceName, familyName));
    }

    public void removeErrorMessage(String serviceName, String familyName, String message) {
        Map<String, ErrorObject> stringErrorObjectMap = (Map)this.allAlarmsCache.get(serviceName);
        ErrorObject errorObject = (ErrorObject)stringErrorObjectMap.get(familyName);
        boolean b = errorObject.removeErrorMessage(message);
        if (b) {
            this.removeFamilyPartition(serviceName, familyName);
        }

    }

    public List<String> getListOfAllErrors() {
        LinkedList<String> errors = new LinkedList();
        this.allAlarmsCache.forEach((service, family) -> family.forEach((familyName, errorObject) -> errors.addAll(errorObject.getErrorMessageList())));
        return errors;
    }

    public List<String> getListOfAllServicesHasErrors() {
        return new ArrayList(this.allAlarmsCache.keySet());
    }

    public List<String> getListOfAllErrorFamilies(String serviceName) {
        return this.allAlarmsCache.values().stream().flatMap((item) -> item.keySet().stream()).collect(Collectors.toList());
    }

    public ErrorObject getFamilyErrorsAsMap(String serviceName, String familyName) {
        return (ErrorObject)((Map)this.allAlarmsCache.get(serviceName)).get(familyName);
    }

    public Map<String, ErrorObject> getServiceErrorsAsMap(String serviceName) {
        return (Map)this.allAlarmsCache.get(serviceName);
    }

    public Map<String, List<ErrorObject>> getAllFamiliesWithErrorsAsMap() {
        Map<String, List<ErrorObject>> allFamilies = new HashMap();
        this.allAlarmsCache.forEach((service, family) -> {
            List<ErrorObject> errors = new LinkedList();
            family.forEach((key, value) -> errors.add(value));
            allFamilies.put(service, errors);
        });
        return allFamilies;
    }

    public List<ErrorObject> getAllErrorObjects() {
        List<ErrorObject> allErrors = new LinkedList();
        this.allAlarmsCache.forEach((service, family) -> family.forEach((s, errorObject) -> allErrors.add(errorObject)));
        return allErrors;
    }

    public void addServicesListener(ServiceErrorsListener serviceErrorsListener) {
        this.serviceErrorsListeners.add(serviceErrorsListener);
        serviceErrorsListener.atRegistering(this.getListOfAllServicesHasErrors(), this.getAllFamiliesWithErrorsAsMap());
    }

    public void refreshListener(String serviceName, String familyName, String error) {
        this.serviceErrorsListeners.forEach((listener) -> listener.newErrorMessagePopUpOnly(serviceName, familyName, error));
    }
}
