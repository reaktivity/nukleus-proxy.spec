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
        "${scripts}/client.connected/client",
        "${scripts}/client.connected/server"})
    public void shouldProxyClientConnected() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.tcp4/client",
        "${scripts}/client.connected.tcp4/server"})
    public void shouldProxyClientConnectedTcp4() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.tcp4.tlv.alpn/client",
        "${scripts}/client.connected.tcp4.tlv.alpn/server"})
    public void shouldProxyClientConnectedTcp4WithAlpn() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.tcp4.tlv.authority/client",
        "${scripts}/client.connected.tcp4.tlv.authority/server"})
    public void shouldProxyClientConnectedTcp4WithAuthority() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.tcp4.tlv.crc32c/client",
        "${scripts}/client.connected.tcp4.tlv.crc32c/server"})
    public void shouldProxyClientConnectedTcp4WithCrc32c() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.rejected.tcp4.tlv.crc32c.mismatch/client",
        "${scripts}/client.rejected.tcp4.tlv.crc32c.mismatch/server"})
    public void shouldProxyClientRejectedTcp4WithCrc32cMismatch() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.rejected.tcp4.tlv.crc32c.overflow/client",
        "${scripts}/client.rejected.tcp4.tlv.crc32c.overflow/server"})
    public void shouldProxyClientRejectedTcp4WithCrc32cOverflow() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.rejected.tcp4.tlv.crc32c.underflow/client",
        "${scripts}/client.rejected.tcp4.tlv.crc32c.underflow/server"})
    public void shouldProxyClientRejectedTcp4WithCrc32cUnderflow() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.tcp4.tlv.namespace/client",
        "${scripts}/client.connected.tcp4.tlv.namespace/server"})
    public void shouldProxyClientConnectedTcp4WithNamespace() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.tcp4.tlv.noop/client",
        "${scripts}/client.connected.tcp4.tlv.noop/server"})
    public void shouldProxyClientConnectedTcp4WithNoop() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.udp4/client",
        "${scripts}/client.connected.udp4/server"})
    public void shouldProxyClientConnectedUdp4() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.tcp6/client",
        "${scripts}/client.connected.tcp6/server"})
    public void shouldProxyClientConnectedTcp6() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.udp6/client",
        "${scripts}/client.connected.udp6/server"})
    public void shouldProxyClientConnectedUdp6() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.sock.stream/client",
        "${scripts}/client.connected.sock.stream/server"})
    public void shouldProxyClientConnectedSockStream() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.connected.sock.datagram/client",
        "${scripts}/client.connected.sock.datagram/server"})
    public void shouldProxyClientConnectedSockDatagram() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.sent.data/client",
        "${scripts}/client.sent.data/server"})
    public void shouldProxyClientSentData() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.sent.flush/client",
        "${scripts}/client.sent.flush/server"})
    public void shouldProxyClientSentFlush() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/client.sent.challenge/client",
        "${scripts}/client.sent.challenge/server"})
    public void shouldProxyClientSentChallenge() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/server.sent.data/client",
        "${scripts}/server.sent.data/server"})
    public void shouldProxyServerSentData() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/server.sent.flush/client",
        "${scripts}/server.sent.flush/server"})
    public void shouldProxyServerSentFlush() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }

    @Test
    @Specification({
        "${scripts}/server.sent.challenge/client",
        "${scripts}/server.sent.challenge/server"})
    public void shouldProxyServerSentChallenge() throws Exception
    {
        k3po.start();
        k3po.notifyBarrier("ROUTED_SERVER");
        k3po.finish();
    }
}
