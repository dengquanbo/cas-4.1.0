/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.web.flow;

import javax.validation.constraints.NotNull;

import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.RequestContext;

/**
 * Generates the login ticket parameter as described in section 3.5 of the
 * <a href="http://www.jasig.org/cas/protocol">CAS protocol</a>.
 *
 * @author Marvin S. Addison
 * @since 3.4.9
 *
 */
public class GenerateLoginTicketAction {
	/** 3.5.1 - Login tickets SHOULD begin with characters "LT-". */
	private static final String PREFIX = "LT";

	/** Logger instance. */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@NotNull
	private UniqueTicketIdGenerator ticketIdGenerator;

	/**
	 * Generate the login ticket.
	 *
	 * @param context
	 *            the context
	 * @return <code>"generated"</code>
	 */
	public final String generate(final RequestContext context) {
		// 这里调用 ticketIdGenerator 生成 loginTicket，这里默认配置的为 DefaultUniqueTicketIdGenerator
		final String loginTicket = this.ticketIdGenerator.getNewTicketId(PREFIX);
		logger.debug("Generated login ticket {}", loginTicket);

		// 往 flow scope 放入键值对 key = loginTicket, value = 生成的loginTicket
		WebUtils.putLoginTicket(context, loginTicket);
		return "generated";
	}

	public void setTicketIdGenerator(final UniqueTicketIdGenerator generator) {
		this.ticketIdGenerator = generator;
	}
}
