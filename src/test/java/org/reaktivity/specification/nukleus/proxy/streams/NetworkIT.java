/**
 * Copyright 2016-2021 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.specification.nukleus.proxy.streams;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class NetworkIT
{
    private final K3poRule k3po = new K3poRule()
        .addScriptRoot("scripts", "org/reaktivity/specification/nukleus/proxy/streams/network.v2");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "${scripts}/connected.local/client",
        "${scripts}/connected.local/server"})
    public void shouldConnectLocal() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.discard/client",
        "${scripts}/connected.local.discard/server"})
    public void shouldConnectLocalDiscard() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.client.sent.begin.ext/client",
        "${scripts}/connected.local.client.sent.begin.ext/server"})
    public void shouldConnectLocalClientSendsBeginExt() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.client.sent.close/client",
        "${scripts}/connected.local.client.sent.close/server"})
    public void shouldConnectLocalClientSendsClose() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.client.sent.abort/client",
        "${scripts}/connected.local.client.sent.abort/server"})
    public void shouldConnectLocalClientSendsAbort() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.client.sent.reset/client",
        "${scripts}/connected.local.client.sent.reset/server"})
    public void shouldConnectLocalClientSendsReset() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.client.sent.data/client",
        "${scripts}/connected.local.client.sent.data/server"})
    public void shouldConnectLocalClientSendsData() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.client.sent.flush/client",
        "${scripts}/connected.local.client.sent.flush/server"})
    public void shouldConnectLocalClientSendsFlush() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.client.sent.challenge/client",
        "${scripts}/connected.local.client.sent.challenge/server"})
    public void shouldConnectLocalClientSendsChallenge() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.server.sent.close/client",
        "${scripts}/connected.local.server.sent.close/server"})
    public void shouldConnectLocalServerSendsClose() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.server.sent.abort/client",
        "${scripts}/connected.local.server.sent.abort/server"})
    public void shouldConnectLocalServerSendsAbort() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.server.sent.reset/client",
        "${scripts}/connected.local.server.sent.reset/server"})
    public void shouldConnectLocalServerSendsReset() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.server.sent.data/client",
        "${scripts}/connected.local.server.sent.data/server"})
    public void shouldConnectLocalServerSendsData() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.server.sent.flush/client",
        "${scripts}/connected.local.server.sent.flush/server"})
    public void shouldConnectLocalServerSendsFlush() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.local.server.sent.challenge/client",
        "${scripts}/connected.local.server.sent.challenge/server"})
    public void shouldConnectLocalServerSendsChallenge() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4/client",
        "${scripts}/connected.tcp4/server"})
    public void shouldConnectTcp4() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.alpn/client",
        "${scripts}/connected.tcp4.alpn/server"})
    public void shouldConnectTcp4WithAlpn() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.authority/client",
        "${scripts}/connected.tcp4.authority/server"})
    public void shouldConnectTcp4WithAuthority() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.crc32c/client",
        "${scripts}/connected.tcp4.crc32c/server"})
    public void shouldConnectTcp4WithCrc32c() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.identity/client",
        "${scripts}/connected.tcp4.identity/server"})
    public void shouldConnectTcp4WithIdentity() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.namespace/client",
        "${scripts}/connected.tcp4.namespace/server"})
    public void shouldConnectTcp4WithNamespace() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.noop/client",
        "${scripts}/connected.tcp4.noop/server"})
    public void shouldConnectTcp4WithNoop() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.ssl/client",
        "${scripts}/connected.tcp4.ssl/server"})
    public void shouldConnectTcp4WithSsl() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.ssl.client.cert/client",
        "${scripts}/connected.tcp4.ssl.client.cert/server"})
    public void shouldConnectTcp4WithSslClientCertificate() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.ssl.client.cert.session/client",
        "${scripts}/connected.tcp4.ssl.client.cert.session/server"})
    public void shouldConnectTcp4WithSslClientCertificateSession() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.ssl.experimental/client",
        "${scripts}/connected.tcp4.ssl.experimental/server"})
    public void shouldConnectTcp4WithSslExperimental() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.experimental/client",
        "${scripts}/connected.tcp4.experimental/server"})
    public void shouldConnectTcp4WithExperimental() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.udp4/client",
        "${scripts}/connected.udp4/server"})
    public void shouldConnectUdp4() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp6/client",
        "${scripts}/connected.tcp6/server"})
    public void shouldConnectTcp6() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.udp6/client",
        "${scripts}/connected.udp6/server"})
    public void shouldConnectUdp6() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.sock.stream/client",
        "${scripts}/connected.sock.stream/server"})
    public void shouldConnectSockStream() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.sock.datagram/client",
        "${scripts}/connected.sock.datagram/server"})
    public void shouldConnectSockDatagram() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.header.mismatch/client",
        "${scripts}/rejected.header.mismatch/server"})
    public void shouldRejectHeaderMismatch() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.header.version.mismatch/client",
        "${scripts}/rejected.header.version.mismatch/server"})
    public void shouldRejectHeaderVersionMismatch() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.tcp4.crc32c.mismatch/client",
        "${scripts}/rejected.tcp4.crc32c.mismatch/server"})
    public void shouldRejectTcp4WithCrc32cMismatch() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.tcp4.crc32c.overflow/client",
        "${scripts}/rejected.tcp4.crc32c.overflow/server"})
    public void shouldRejectTcp4WithCrc32cOverflow() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.tcp4.crc32c.underflow/client",
        "${scripts}/rejected.tcp4.crc32c.underflow/server"})
    public void shouldRejectTcp4WithCrc32cUnderflow() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.tcp4.ssl.underflow/client",
        "${scripts}/rejected.tcp4.ssl.underflow/server"})
    public void shouldRejectTcp4WithSslUnderflow() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.tcp4.underflow/client",
        "${scripts}/rejected.tcp4.underflow/server"})
    public void shouldRejectTcp4Underflow() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.tcp6.underflow/client",
        "${scripts}/rejected.tcp6.underflow/server"})
    public void shouldRejectTcp6Underflow() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.sock.stream.underflow/client",
        "${scripts}/rejected.sock.stream.underflow/server"})
    public void shouldRejectSockStreamUnderflow() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.address.family.mismatch/client",
        "${scripts}/rejected.address.family.mismatch/server"})
    public void shouldRejectAddressFamilyMismatch() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.command.mismatch/client",
        "${scripts}/rejected.command.mismatch/server"})
    public void shouldRejectCommandMismatch() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }
}
