package cz.sio2.bibpub;

import com.liferay.faces.portal.context.LiferayFacesContext;

import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.*;
import javax.portlet.faces.preference.Preference;
import java.util.*;

@ManagedBean(name="bbconfiguration")
public class BBConfiguration {

    private PortletPreferences getPreferences() {
        return LiferayFacesContext.getInstance().getPortletPreferences();//((PortletRequest) getExternalContext().getRequest()).getPreferences();
    }

    private ExternalContext getExternalContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public void submit() {
        @SuppressWarnings("unchecked")
        final Map<String, Preference> mutablePreferenceMap =
                (Map<String, Preference>) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(
                        FacesContext.getCurrentInstance().getELContext(), null, "mutablePortletPreferencesValues");
        PortletPreferences portletPreferences = getPreferences();
        try {
            for (Map.Entry<String,Preference> entry : mutablePreferenceMap.entrySet()) {
                if (!portletPreferences.isReadOnly(entry.getKey())) {
                    portletPreferences.setValue(entry.getKey(), entry.getValue().getValue());
                }
            }
            portletPreferences.store();
            ActionResponse actionResponse =
                    (ActionResponse) getExternalContext().getResponse();
            actionResponse.setPortletMode(PortletMode.VIEW);
            actionResponse.setWindowState(WindowState.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
