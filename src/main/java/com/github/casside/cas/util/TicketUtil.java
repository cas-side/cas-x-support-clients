package com.github.casside.cas.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.util.Pac4jUtils;
import org.apereo.cas.web.DelegatedClientWebflowManager;
import org.apereo.cas.web.pac4j.DelegatedSessionCookieManager;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.springframework.http.HttpStatus;

@Slf4j
public class TicketUtil {

    /**
     * 生成登录票据
     * @param returnUrl 登录成功要回跳的URL，即从哪来，最后回到哪去
     */
    public static Ticket genTransientServiceTicket(DelegatedClientWebflowManager delegatedClientWebflowManager,
                                                   DelegatedSessionCookieManager delegatedSessionCookieManager,
                                                   IndirectClient client,
                                                   final HttpServletRequest request, final HttpServletResponse response,
                                                   String returnUrl) {
        try {
            request.setAttribute("service", returnUrl);
            final J2EContext webContext = Pac4jUtils.getPac4jJ2EContext(request, response);
            final Ticket     ticket     = delegatedClientWebflowManager.store(webContext, client);

            /*
             * client.getRedirectAction(webContext);
             *
             * 该函数的核心就是下面这句，把ticket.id放到session中，因为这里[org.pac4j.oauth.credentials.extractor.OAuth20CredentialsExtractor#getOAuthCredentials(org.pac4j.core.context.WebContext)]
             * 要验证微信返回的state，和你保存的state（就是ticketId）是不是一样
             *
             * ps：就这点作用而已
             */
            webContext.getSessionStore().set(webContext, new OAuth20Configuration().getStateSessionAttributeName(client.getName()), ticket.getId());
            delegatedSessionCookieManager.store(webContext);
            return ticket;
        } catch (final HttpAction e) {
            if (e.getCode() == HttpStatus.UNAUTHORIZED.value()) {
                log.debug("Authentication request was denied from the provider [{}]", client.getName(), e);
            } else {
                log.warn(e.getMessage(), e);
            }
            throw new UnauthorizedServiceException(e.getMessage(), e);
        }
    }

}
