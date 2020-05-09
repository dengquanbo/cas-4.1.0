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
package org.jasig.cas.authentication.principal;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

/**
 * Represents a service which wishes to use the CAS protocol.
 *
 * @author Scott Battaglia
 * @since 3.1
 */
public final class SimpleWebApplicationServiceImpl extends AbstractWebApplicationService {

	private static final long serialVersionUID = 8334068957483758042L;

	private static final String CONST_PARAM_SERVICE = "service";

	private static final String CONST_PARAM_TARGET_SERVICE = "targetService";

	private static final String CONST_PARAM_TICKET = "ticket";

	private static final String CONST_PARAM_METHOD = "method";

	private final Response.ResponseType responseType;

	/**
	 * Instantiates a new simple web application service impl.
	 *
	 * @param id
	 *            the id
	 */
	public SimpleWebApplicationServiceImpl(final String id) {
		this(id, id, null, null);
	}

	/**
	 * Instantiates a new simple web application service impl.
	 *
	 * @param id
	 *            the id
	 * @param originalUrl
	 *            the original url
	 * @param artifactId
	 *            the artifact id
	 * @param responseType
	 *            the response type
	 */
	private SimpleWebApplicationServiceImpl(final String id, final String originalUrl, final String artifactId,
			final Response.ResponseType responseType) {
		super(id, originalUrl, artifactId);
		this.responseType = responseType;
	}

	/**
	 * Creates the service from the request.f
	 *
	 * @param request
	 *            the request
	 * @return the simple web application service impl
	 */
	public static SimpleWebApplicationServiceImpl createServiceFrom(final HttpServletRequest request) {
		// 获得 targetService
		final String targetService = request.getParameter(CONST_PARAM_TARGET_SERVICE);
		// 获得 service
		final String service = request.getParameter(CONST_PARAM_SERVICE);
		final String serviceAttribute = (String) request.getAttribute(CONST_PARAM_SERVICE);
		// 获得 method
		final String method = request.getParameter(CONST_PARAM_METHOD);
		final String serviceToUse;
		if (StringUtils.hasText(targetService)) {
			serviceToUse = targetService;
		} else if (StringUtils.hasText(service)) {
			serviceToUse = service;
		} else {
			serviceToUse = serviceAttribute;
		}

		if (!StringUtils.hasText(serviceToUse)) {
			return null;
		}
        // 客户端的地址，这里地址会去除查询参数和jsession
		final String id = cleanupUrl(serviceToUse);
		// 获取 ticket
		final String artifactId = request.getParameter(CONST_PARAM_TICKET);

		return new SimpleWebApplicationServiceImpl(id, serviceToUse, artifactId,
				"POST".equals(method) ? Response.ResponseType.POST : Response.ResponseType.REDIRECT);
	}

	@Override
	public Response getResponse(final String ticketId) {
		final Map<String, String> parameters = new HashMap<>();

		if (StringUtils.hasText(ticketId)) {
			parameters.put(CONST_PARAM_TICKET, ticketId);
		}

		if (Response.ResponseType.POST == this.responseType) {
			return DefaultResponse.getPostResponse(getOriginalUrl(), parameters);
		}
		return DefaultResponse.getRedirectResponse(getOriginalUrl(), parameters);
	}
}
