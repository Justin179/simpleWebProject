package com.bot.TB;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Servlet Filter implementation class SetCharacterEncodingFilter
 */
public class SetCharacterEncodingFilter implements Filter {
	private String encoding;
	@SuppressWarnings("unused")
	private FilterConfig fConfig;
	private boolean ignore;

	/**
	 * Default constructor.
	 */
	public SetCharacterEncodingFilter() {
		encoding = null;
		fConfig = null;
		ignore = true;

	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		encoding = null;
		fConfig = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (ignore || request.getCharacterEncoding() == null) {
			String encoding = selectEncoding(request);
			if (encoding != null)
				request.setCharacterEncoding(encoding);
		}

		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.fConfig = fConfig;
		encoding = fConfig.getInitParameter("encoding");
		String value = fConfig.getInitParameter("ignore");
		ignore = !"false".equalsIgnoreCase(value);

	}

	private String selectEncoding(ServletRequest request) {
		return encoding;
	}

}
