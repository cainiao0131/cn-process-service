/**
 * 系统间调用接口，由同一【租户】中的其它【系统】调用<br />
 * 将当前应用配置为 Client Credential 类型的资源服务器，Filter Chain 的工作：<br />
 * <ol>
 *     <li>校验请求头中的 ClientId 与 ClientSecret</li>
 *     <li>从缓存获取当前【环境】的【系统网关】的 ClientId</li>
 *     <li>
 *         如果缓存未命中，则用当前【系统】的 ClientId 与 ClientSecret 调用授权中心的接口<br />
 *         获取当前【环境】的【系统网关】的 ClientId<br />
 *         这个授权中心接口，只允许在【服务编排系统】中被注册为【系统服务提供者】的 ClientId 访问，否则抛 403
 *     </li>
 *     <li>对比请求头中的 ClientId 与【系统网关】的 ClientId，只允许来自【系统网关】的请求</li>
 * </ol>
 */
package org.cainiao.process.controller.intersystem;
