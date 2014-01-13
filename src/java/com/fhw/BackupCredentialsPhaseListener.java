package com.fhw;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class BackupCredentialsPhaseListener
        implements PhaseListener
{

    private static final long serialVersionUID = 8011859780356924172L;

    @Override
    public void afterPhase(PhaseEvent event)
    {
        System.out.println("after restore view"); 
    }

    @Override
    public void beforePhase(PhaseEvent event)
    {
        System.out.println("before restore view"); 
    }

    @Override
    public PhaseId getPhaseId()
    {
        return(PhaseId.RESTORE_VIEW); 
    }
}
