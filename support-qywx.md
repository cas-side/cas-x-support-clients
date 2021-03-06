# 委托授权——企业微信

> 基于企业微信的授权登录，目前支持：`扫码登录`、`工作台应用主页授权`，
登录后可以获取到企业微信用户信息，
同时支持关联到已有的用户服务

## 对接流程

```yaml
cas-x:
  authn:
    # 启用cas-x的委托登录
    enabled: true
    qy_wx:
      # 启用企业微信扫码登录
      enabled: true
      id: ${your corpid}
      secret: ${your secret}
      clientName: 企业微信
      agentid: ${your app agentid}
      principalAttributeId: UserId
      profileAttrs:
        # profile属性，即使不需要重命名，也务必在此声明一下
        UserId: user_id
        DeviceId: device_id
        
      # 自建APP授权访问的服务
      clients:
        shop:
          name: pet商店
          url: http://shop.example.com
```

## 工作台应用主页授权

> 在企业微信自建应用的主页，可以关联自己的系统应用，
通过企业微信授权自动登录

该功能需要配置`cas-x.authn.qy_wx.clients`

然后在 （自建APP -> 工作台应用主页） 设置URL：
`https://open.weixin.qq.com/connect/oauth2/authorize?appid={corp id}&redirect_uri=https://{cas server url}/cas_x_qy_wx/clients/tp/shop&response_type=code&scope=snsapi_base#wechat_redirect`

比如上面我们配置了一个key为`shop`的client，则URL为：
`http://{cas.server}/cas_x_qy_wx/clients/tp/shop`

同时，你必须保证shop已经对接了cas.server，否则...你还怎么做授权

## 在首页嵌入企业微信二维码

```html
<div id="wx_reg"></div>

<script>
$.get("cas_x_qy_wx/qr_code_params", function(res) {
    if(res.code === 0) {
        // 这里具体配置参考企业微信：
        // https://work.weixin.qq.com/api/doc#90000/90135/91019/%E6%9E%84%E9%80%A0%E5%86%85%E5%B5%8C%E7%99%BB%E5%BD%95%E4%BA%8C%E7%BB%B4%E7%A0%81
        window.WwLogin({
            "id" : "wx_reg",
            "appid" : res.data.corpId,
            "agentid" : res.data.agentId,
            "redirect_uri" : res.data.redirectUri,
            "state" : res.data.state,
            "href" : res.data.href
        });
    } else if(res.code === 404) {
        console.log("has no config qy_wx qr");
    } else {
        console.warn(res.msg);
    }
});
</script>
```

## 如何关联自己的用户数据库

如果你的系统已经将企业微信用户和数据库用户进行关联，
需要从数据库查询用户信息，可以定义一个Bean，
继承自 `com.github.casside.cas.support.qywx.UserProfileService`,
并重写 `get(String qyWxUserId)` 方法

## reference

- [对接流程](https://mp.weixin.qq.com/s/1veDc6tokDSS7TtTE4TokA).