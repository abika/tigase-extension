/*
 * Kontalk XMPP Tigase extension
 * Copyright (C) 2014 Kontalk Devteam <devteam@kontalk.org>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kontalk.xmppserver.registration;

import org.kontalk.xmppserver.KontalkIqRegister;
import tigase.server.Iq;
import tigase.server.Packet;
import tigase.xml.Element;
import tigase.xmpp.JID;
import tigase.xmpp.StanzaType;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;


/**
 * Verification provider that delegates the actual SMS delivery to another server.
 * @author Daniele Ricci
 */
public class DelegatedProvider extends AbstractSMSVerificationProvider {

    @Override
    public void init(Map<String, Object> settings) {
        super.init(settings);
    }

    @Override
    public String getAckInstructions() {
        return "TODO";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public String sendVerificationCodeAsync(String phoneNumber, JID serverJid, Queue<Packet> results) throws IOException {
        // send request to a delegated server
        Element register = new Element(Iq.QUERY_NAME);
        register.setXMLNS(KontalkIqRegister.XMLNS);
        register.addChild(new Element("verification", phoneNumber));

        String id = UUID.randomUUID().toString();

        Element root = new Element("iq");
        root.setAttribute(Packet.TYPE_ATT, StanzaType.set.toString());
        root.setAttribute(Packet.ID_ATT, id);
        root.addChild(register);
        results.offer(Packet.packetInstance(root, serverJid, getDelegatedServer()));
        return id;
    }

    public String processAsyncResult(String requestId, String phoneNumber, Packet packet) {
        // TODO parse result packet and return code
        return null;
    }

    private JID getDelegatedServer() {
        // TODO
        return JID.jidInstanceNS("sms-delegate@TODO");
    }

    @Override
    public void sendVerificationCode(String phoneNumber, String code) throws IOException {
        throw new UnsupportedOperationException("this provider is asynchronous");
    }
}
