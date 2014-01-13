package com.fhw;

import java.util.UUID;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BackupCredentialsPhaseListener
        implements PhaseListener
{

    private static final String SESSION_COOKIE_NAME = "BAPHA.sessionID";    
    private static final long serialVersionUID = 8011859780356924172L;

    @Override
    public void afterPhase(PhaseEvent event)
    {        
    }
    
    @Override
    public void beforePhase(PhaseEvent event)            
    {
        FacesContext fc = event.getFacesContext();
        HttpServletRequest hsr = (HttpServletRequest) fc.getExternalContext().getRequest(); 
        HttpServletResponse hsRsp = (HttpServletResponse) fc.getExternalContext().getResponse(); 
        String haSessionID = getHASessionId(hsr);
        if (null != haSessionID)
        {
            applySession(hsr, haSessionID, hsRsp);
        }
        else
        {
            haSessionID = generateSessionId();
            makeSession(hsr, haSessionID, hsRsp);
        }                                
    }
    
    private void applySession(final HttpServletRequest req, final String sessionId, final HttpServletResponse res)
    {
        System.out.println("applying existing HA Session"); 
    }
    
    private static synchronized String generateSessionId()
    {
        final String id = UUID.randomUUID().toString();
        final StringBuilder sb = new StringBuilder("BAP");
        final char[] chars = id.toCharArray();
        for (final char c : chars)
        {
            if (c != '-')
            {
                if (Character.isLetter(c))
                {
                    sb.append(Character.toUpperCase(c));
                }
                else
                {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }    
    
    private void makeSession(final HttpServletRequest req, final String sessionId, final HttpServletResponse res)
    {
        System.out.println("making a HA session with ID " + sessionId); 
        final Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        final Cookie jsessionCookie = findCookie("jsessionid", req);         
        if(null != jsessionCookie )
        {
            String s = jsessionCookie.getDomain(); 
            if(null != s)
                sessionCookie.setDomain(s);
            s=jsessionCookie.getPath();
            if(null != s)
                sessionCookie.setPath(jsessionCookie.getPath());
            sessionCookie.setHttpOnly(jsessionCookie.isHttpOnly());        
            sessionCookie.setMaxAge(jsessionCookie.getMaxAge());
            res.addCookie(sessionCookie);                
        }        
    }    
    
    @Override
    public PhaseId getPhaseId()
    {
        return(PhaseId.RESTORE_VIEW); 
    }
    
    
    private String getHASessionId(final HttpServletRequest req)
    {
        
        Cookie haCookie = findCookie(SESSION_COOKIE_NAME, req);         
        String sessId = null;
        if( null != haCookie)
        {
            sessId = haCookie.getValue(); 
        }
        return sessId;
    }    
    
    private Cookie findCookie(final String cookieName, final HttpServletRequest req)
    {
        Cookie daCookie = null;          
        final Cookie[] cookies = req.getCookies();
        if (cookies != null)
        {
            for (final Cookie cookie : cookies)
            {
                final String name = cookie.getName();
                if (name.equalsIgnoreCase(cookieName))
                {
                    daCookie = cookie;
                    break;
                }
            }
        }
        return daCookie;        
    }
}
