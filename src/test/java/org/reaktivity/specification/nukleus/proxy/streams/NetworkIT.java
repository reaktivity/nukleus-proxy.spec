/**
 * Copyright 2016-2020 The Reaktivity Project
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
        "${scripts}/connected.unspecified/client",
        "${scripts}/connected.unspecified/server"})
    public void shouldConnectUnspecified() throws Exception
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
        "${scripts}/connected.tcp4.tlv.alpn/client",
        "${scripts}/connected.tcp4.tlv.alpn/server"})
    public void shouldConnectTcp4WithAlpn() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.tlv.authority/client",
        "${scripts}/connected.tcp4.tlv.authority/server"})
    public void shouldConnectTcp4WithAuthority() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.tlv.crc32c/client",
        "${scripts}/connected.tcp4.tlv.crc32c/server"})
    public void shouldConnectTcp4WithCrc32c() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.tcp4.tlv.crc32c.mismatch/client",
        "${scripts}/rejected.tcp4.tlv.crc32c.mismatch/server"})
    public void shouldRejectTcp4WithCrc32cMismatch() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.tcp4.tlv.crc32c.overflow/client",
        "${scripts}/rejected.tcp4.tlv.crc32c.overflow/server"})
    public void shouldRejectTcp4WithCrc32cOverflow() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/rejected.tcp4.tlv.crc32c.underflow/client",
        "${scripts}/rejected.tcp4.tlv.crc32c.underflow/server"})
    public void shouldRejectTcp4WithCrc32cUnderflow() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.tlv.namespace/client",
        "${scripts}/connected.tcp4.tlv.namespace/server"})
    public void shouldConnectTcp4WithNamespace() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.tcp4.tlv.noop/client",
        "${scripts}/connected.tcp4.tlv.noop/server"})
    public void shouldConnectTcp4WithNoop() throws Exception
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
        "${scripts}/connected.unspecified.client.sent.data/client",
        "${scripts}/connected.unspecified.client.sent.data/server"})
    public void shouldConnectUnspecifiedClientSendsData() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.unspecified.client.sent.flush/client",
        "${scripts}/connected.unspecified.client.sent.flush/server"})
    public void shouldConnectUnspecifiedClientSendsFlush() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.unspecified.client.sent.challenge/client",
        "${scripts}/connected.unspecified.client.sent.challenge/server"})
    public void shouldConnectUnspecifiedClientSendsChallenge() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.unspecified.server.sent.data/client",
        "${scripts}/connected.unspecified.server.sent.data/server"})
    public void shouldConnectUnspecifiedServerSendsData() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.unspecified.server.sent.flush/client",
        "${scripts}/connected.unspecified.server.sent.flush/server"})
    public void shouldConnectUnspecifiedServerSendsFlush() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/connected.unspecified.server.sent.challenge/client",
        "${scripts}/connected.unspecified.server.sent.challenge/server"})
    public void shouldConnectUnspecifiedServerSendsChallenge() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }
}
