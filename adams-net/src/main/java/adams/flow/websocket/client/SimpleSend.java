/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * SimpleSend.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.websocket.client;

import adams.core.logging.Logger;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;
import com.pusher.java_websocket.client.WebSocketClient;
import com.pusher.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.logging.Level;

/**
 * Simple client for sending messages.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleSend
  extends WebSocketClient
  implements FlowContextHandler {

  /** for logging purposes. */
  protected Logger m_Logger;

  /** the flow context. */
  protected Actor m_FlowContext;

  /**
   * Initializes the client with the specified URI.
   *
   * @param serverURI	the server to use
   * @param logger	the logger to use
   */
  public SimpleSend(URI serverURI, Logger logger) {
    super(serverURI);
    m_Logger = logger;
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns the logger instance to use.
   *
   * @return		the logger
   */
  protected synchronized Logger getLogger() {
    return m_Logger;
  }

  /**
   * On opening the connection.
   *
   * @param handshakedata	the handshake
   */
  @Override
  public void onOpen(ServerHandshake handshakedata) {
    getLogger().info("onOpen: " + handshakedata.getHttpStatus() + ": " + handshakedata.getHttpStatusMessage());
  }

  /**
   * When receiving a message.
   *
   * @param message	the message
   */
  @Override
  public void onMessage(String message) {
    getLogger().info("onMessage: " + message);
  }

  /**
   * On closing the connection.
   *
   * @param code	the code
   * @param reason	the reason
   * @param remote	whether remote
   */
  @Override
  public void onClose(int code, String reason, boolean remote) {
    getLogger().info("onClose: " + "code=" + code + ", reason=" + reason + ", remote=" + remote);
  }

  /**
   * In case of an error.
   *
   * @param ex		the exception
   */
  @Override
  public void onError(Exception ex) {
    getLogger().log(Level.SEVERE, "Error occurred!", ex);
  }
}