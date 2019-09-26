package com.github.casside.cas.support.qywx;

import com.github.casside.cas.support.ClientServers;
import com.github.casside.cas.support.ClientServers.ClientServer;
import com.github.casside.cas.support.DelagatedClientProperties;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.util.Pac4jUtils;
import org.apereo.cas.web.DelegatedClientWebflowManager;
import org.apereo.cas.web.pac4j.DelegatedSessionCookieManager;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.exception.HttpAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 企业微信授权 action
 * @deprecated `/qy_wx` 这个URI是我自己保留使用，将在以后废弃
 */
@Controller
@RequestMapping(value = {"/qy_wx", "/cas-x-qy-wx/clients"})
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBean(ClientServers.class)
public class QyWxAuthenticationAction {

    @Value("${cas.server.name}")
    private       String                    ssoUrl;
    private final ClientServers             clientServers;
    private final DelagatedClientProperties delagatedClientProperties;

    private final Clients                       clients;
    private final DelegatedClientWebflowManager delegatedClientWebflowManager;
    private final DelegatedSessionCookieManager delegatedSessionCookieManager;

    /**
     * 实现在企业微信app中自动登录到各个客户端，同时借助
     *
     * 为了实现企业微信APP认证之后，自动跳转，我们看一下委托登录的跳转流程 {@code org.apereo.cas.web.DelegatedClientNavigationController#redirectToProvider(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)}
     *
     * @param client 我们要登录的客户端的简称，在配置文件里已经配置，比如网盘，简称：pan
     */
    @RequestMapping("/{client}")
    public RedirectView to(@PathVariable("client") String client, final HttpServletRequest request, final HttpServletResponse response)
        throws UnsupportedEncodingException {
        ClientServer clientServer = clientServers.get(client);
        if (clientServer == null) {
            throw new NullPointerException("no such a client server");
        }

        String clientName = delagatedClientProperties.getQyWx().getClientName();

        // 指定service，登录成功后，要返回的地址
        // service 用于生成 ticket，登录成功后，从ticket中解析出service，然后重定向到该地址
        // 这个地址是客户端地址，且是用于sso登录的地址
        String clientUrl = clientServer.getUrl();
        request.setAttribute("service", clientUrl);

        // 单点登录URL，也是微信端要重定向的URL
        // 这里也要指定service，如果是已登录状态，会根据这里的service进行重定向
        String ssoLoginUrl = String.format("%s/login?client_name=%s&service=%s", ssoUrl, clientName, clientUrl);

        Ticket ticket = genTicket(clientName, request, response);

        // 微信授权URL
        String qyWxAuthorizeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect";
        String appId            = delagatedClientProperties.getQyWx().getId();
        String url              = String.format(qyWxAuthorizeUrl, appId, URLEncoder.encode(ssoLoginUrl, "UTF-8"), ticket.getId());
        return new RedirectView(url);
    }

    /**
     * 生成ticket
     */
    private Ticket genTicket(String clientName, final HttpServletRequest request, final HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(clientName)) {
                throw new UnauthorizedServiceException("No client name parameter is provided in the incoming request");
            }
            final IndirectClient client     = (IndirectClient) this.clients.findClient(clientName);
            final J2EContext     webContext = Pac4jUtils.getPac4jJ2EContext(request, response);
            final Ticket         ticket     = delegatedClientWebflowManager.store(webContext, client);

            // TODO
            client.getRedirectAction(webContext);
            this.delegatedSessionCookieManager.store(webContext);
            return ticket;
        } catch (final HttpAction e) {
            if (e.getCode() == HttpStatus.UNAUTHORIZED.value()) {
                log.debug("Authentication request was denied from the provider [{}]", clientName, e);
            } else {
                log.warn(e.getMessage(), e);
            }
            throw new UnauthorizedServiceException(e.getMessage(), e);
        }
    }
}
