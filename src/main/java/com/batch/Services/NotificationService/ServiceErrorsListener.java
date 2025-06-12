
package com.batch.Services.NotificationService;

import java.util.List;
import java.util.Map;

public interface ServiceErrorsListener {
    void newServiceAdded(String serviceAdded);

    void serviceRemoved(String service);

    void newFamilyAdded(String service, String family, ErrorObject errorObject);

    void familyRemoved(String service, String family);

    void newErrorMessageAdded(String service, String family, String errorMessage, ErrorObject errorObject);

    void atRegistering(List<String> services, Map<String, List<ErrorObject>> errors);

    void newErrorMessagePopUpOnly(String service, String family, String errorMessage);
}
